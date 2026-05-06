# Alzheimer Support System — DevOps Deployment Guide

## Architecture Overview

```
Git Push
  └─► GitHub Webhook
        └─► Jenkins (192.168.188.128:8080)
              ├─► SonarQube Analysis (192.168.188.128:30005)
              ├─► Maven / npm Build
              ├─► Docker Build + Push → Nexus (192.168.188.128:30082)
              └─► kubectl apply → Kubernetes (kubeadm)
                    └─► Pods in namespace: alzheimer
                          ├─► frontend        (NodePort 30420)
                          ├─► api-gateway     (NodePort 30080)
                          ├─► discovery-server (NodePort 30761)
                          ├─► keycloak        (NodePort 30081)
                          ├─► user-service
                          ├─► patient-service
                          ├─► cognitive-service
                          ├─► alzheimerapp
                          ├─► support-network
                          ├─► mysql
                          └─► rabbitmq
```

## Service Access (Laptop 2 — 192.168.188.128)

| Service        | URL                                   | Credentials        |
|----------------|---------------------------------------|--------------------|
| Frontend       | http://192.168.188.128:30420           | Keycloak login     |
| API Gateway    | http://192.168.188.128:30080           | All API calls      |
| Keycloak       | http://192.168.188.128:30081           | admin / admin      |
| Eureka         | http://192.168.188.128:30761           | Service registry   |
| Jenkins        | http://192.168.188.128:8080            | CI/CD              |
| Nexus UI       | http://192.168.188.128:30000           | admin / admin1234  |
| Nexus Docker   | 192.168.188.128:30082                  | Docker registry    |
| SonarQube      | http://192.168.188.128:30005           | admin / admin      |
| Prometheus     | http://192.168.188.128:30090           | Metrics            |
| Grafana        | http://192.168.188.128:30030           | admin / admin      |
| PhpMyAdmin     | http://192.168.188.128:30085           | DB admin           |

---

## Step-by-Step Deployment on Laptop 2

### 0. Prerequisites — run ONCE after fresh install

```bash
# Disable swap (required by Kubernetes)
sudo swapoff -a
sudo sed -i '/ swap / s/^/#/' /etc/fstab

# Required by SonarQube (Elasticsearch)
sudo sysctl -w vm.max_map_count=262144
echo 'vm.max_map_count=262144' | sudo tee /etc/sysctl.d/99-sonar.conf

# Install local-path provisioner (provides dynamic PVC provisioning)
kubectl apply -f https://raw.githubusercontent.com/rancher/local-path-provisioner/v0.0.26/deploy/local-path-storage.yaml
kubectl patch storageclass local-path -p '{"metadata": {"annotations":{"storageclass.kubernetes.io/is-default-class":"true"}}}'

# Verify nodes are Ready
kubectl get nodes
```

### 1. Create Namespaces

```bash
kubectl apply -f k8s/namespace.yaml
```

### 2. Create Nexus Image-Pull Secret

Run this command ONCE. It allows Kubernetes to pull images from your Nexus registry:

```bash
kubectl create secret docker-registry nexus-creds \
  --docker-server=192.168.188.128:30082 \
  --docker-username=admin \
  --docker-password=admin1234 \
  --docker-email=admin@alzheimer.local \
  -n alzheimer
```

### 3. Configure Docker + containerd to trust Nexus registry

Edit `/etc/docker/daemon.json` (create if missing):

```json
{
  "insecure-registries": ["192.168.188.128:30082"]
}
```

Restart Docker:

```bash
sudo systemctl restart docker
```

Also configure containerd (used by Kubernetes):

```bash
sudo mkdir -p /etc/containerd/certs.d/192.168.188.128:30082
cat <<EOF | sudo tee /etc/containerd/certs.d/192.168.188.128:30082/hosts.toml
[host."http://192.168.188.128:30082"]
  capabilities = ["pull", "resolve", "push"]
  skip_verify = true
EOF
sudo systemctl restart containerd
```

### 4. Deploy Infrastructure (MySQL + RabbitMQ)

```bash
kubectl apply -f k8s/infrastructure.yaml
# Wait for MySQL to be ready (takes ~30s)
kubectl rollout status deployment/mysql -n alzheimer --timeout=120s
```

### 5. Deploy Nexus + SonarQube

```bash
kubectl apply -f k8s/nexus.yaml
kubectl apply -f k8s/sonarqube.yaml
```

Wait for Nexus (first boot takes 2-3 minutes):

```bash
kubectl rollout status deployment/nexus -n alzheimer --timeout=300s
```

**Configure Nexus Docker repository (do once via UI):**
1. Open http://192.168.188.128:30000 and log in (admin / admin1234)
2. Settings → Repositories → Create repository
3. Choose **docker (hosted)**
4. Name: `alzheimer-docker`
5. HTTP port: `8082`
6. Enable "Allow anonymous docker pull" (optional)
7. Save

Test push from Laptop 2:

```bash
docker login 192.168.188.128:30082 -u admin -p admin1234
docker pull hello-world
docker tag hello-world 192.168.188.128:30082/hello-world:test
docker push 192.168.188.128:30082/hello-world:test
```

### 6. Deploy Monitoring (Prometheus + Grafana)

```bash
kubectl apply -f k8s-monitoring/monitoring.yaml
kubectl rollout status deployment/prometheus -n monitoring --timeout=120s
kubectl rollout status deployment/grafana    -n monitoring --timeout=120s
```

**Configure Grafana (do once via UI):**
1. Open http://192.168.188.128:30030 → admin / admin
2. Connections → Data sources → Add data source → Prometheus
3. URL: `http://prometheus.monitoring.svc.cluster.local:9090`
4. Save & Test
5. Dashboards → Import → use dashboard ID `3119` (Kubernetes cluster monitoring)

### 7. Configure SonarQube (do once)

1. Open http://192.168.188.128:30005 → admin / admin (change password on first login)
2. My Account → Security → Generate Token
3. Name: `jenkins-sonar-token`
4. Copy the token value

### 8. Configure Jenkins

**Install required plugins** (Manage Jenkins → Plugin Manager → Available):
- GitHub Integration Plugin
- Kubernetes Plugin
- Docker Pipeline
- SonarQube Scanner
- Pipeline: Declarative

**Add SonarQube token as Jenkins credential:**
- Manage Jenkins → Credentials → Global → Add Credentials
- Kind: Secret text
- Secret: (paste token from step 7)
- ID: `SONAR_TOKEN`

**Configure SonarQube server in Jenkins:**
- Manage Jenkins → Configure System → SonarQube servers → Add
- Name: `SonarQube`
- Server URL: `http://192.168.188.128:30005`
- Server authentication token: `SONAR_TOKEN`

**Configure Kubernetes cloud in Jenkins:**
- Manage Jenkins → Configure Clouds → Add a new cloud → Kubernetes
- Kubernetes URL: `https://kubernetes.default.svc`
- Kubernetes Namespace: `alzheimer`
- Jenkins URL: `http://192.168.188.128:8080`
- Test Connection → should say "Connected to Kubernetes"

**Create the four pipeline jobs** (New Item → Pipeline → Pipeline script from SCM):

| Job Name                 | Jenkinsfile Path           |
|--------------------------|---------------------------|
| `alzheimer-orchestrator` | `Jenkinsfile`             |
| `alzheimer-backend-ci`   | `Jenkinsfile.backend-ci`  |
| `alzheimer-frontend`     | `Jenkinsfile.frontend`    |
| `alzheimer-backend-cd`   | `Jenkinsfile.backend-cd`  |

**Grant Jenkins permission to manage Kubernetes pods:**

```bash
kubectl create clusterrolebinding jenkins-cluster-admin \
  --clusterrole=cluster-admin \
  --serviceaccount=alzheimer:default
```

### 9. Configure GitHub Webhook

1. GitHub repo → Settings → Webhooks → Add webhook
2. Payload URL: `http://192.168.188.128:8080/github-webhook/`
3. Content type: `application/json`
4. Event: "Just the push event"
5. Active: checked → Add webhook

### 10. First Manual Build

Before the webhook runs for the first time, trigger the CD pipeline manually to deploy all manifests:

```bash
# Apply everything in order
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/gateway-config.yaml
kubectl apply -f k8s/keycloak-realm-config.yaml
kubectl apply -f k8s/infrastructure.yaml
kubectl apply -f k8s/discovery-server.yaml
kubectl apply -f k8s/keycloak.yaml
kubectl apply -f k8s/microservices.yaml
kubectl apply -f k8s/frontend.yaml
kubectl apply -f k8s/sonarqube.yaml
kubectl apply -f k8s/nexus.yaml
kubectl apply -f k8s/phpmyadmin.yaml
kubectl apply -f k8s-monitoring/monitoring.yaml
```

### 11. Verify Everything Is Running

```bash
# All pods should show Running
kubectl get pods -n alzheimer -o wide
kubectl get pods -n monitoring -o wide

# Services and exposed ports
kubectl get svc -n alzheimer

# Check a specific service log
kubectl logs -f deployment/api-gateway -n alzheimer
kubectl logs -f deployment/user-service  -n alzheimer

# Check Eureka — all services should be UP
curl http://192.168.188.128:30761/eureka/apps | grep -o '<app>.*</app>'
```

---

## Expected Final State

Once the full pipeline runs successfully you should see:

- Jenkins: all 4 pipeline jobs green
- Nexus: images present in `alzheimer-docker` repository
- Eureka (http://192.168.188.128:30761): all 7 microservices registered
- SonarQube: analysis visible for `alzheimer-backend`, `alzheimer-main-app`, `alzheimer-support-network`
- Grafana: Kubernetes cluster dashboard showing CPU/RAM/pods
- Frontend accessible: http://192.168.188.128:30420

---

## Troubleshooting

### Pod in ImagePullBackOff

```bash
kubectl describe pod <pod-name> -n alzheimer | grep -A5 Events
# Check nexus-creds exists
kubectl get secret nexus-creds -n alzheimer
# If missing, re-run step 2
```

### SonarQube not starting (Exit 1 / Out of Memory)

```bash
sysctl vm.max_map_count   # must be >= 262144
sudo sysctl -w vm.max_map_count=262144
kubectl rollout restart deployment/sonarqube -n alzheimer
```

### Jenkins Kubernetes agent pods not starting

```bash
kubectl create clusterrolebinding jenkins-cluster-admin \
  --clusterrole=cluster-admin \
  --serviceaccount=alzheimer:default
```

### Nexus Docker push fails (unauthorized)

```bash
docker login 192.168.188.128:30082 -u admin -p admin1234
# Also check insecure-registries in /etc/docker/daemon.json
```

### PVC stuck in Pending

```bash
kubectl get storageclass
# Must have local-path as default. If missing, re-run step 0.
kubectl describe pvc <pvc-name> -n alzheimer
```

### Keycloak redirect_uri mismatch error in browser

- Keycloak client `alzheimer-angular-client` has `redirectUris` set to `http://192.168.188.128:30420/*`
- If your IP changed, update `k8s/keycloak-realm-config.yaml` and re-apply:
  ```bash
  kubectl apply -f k8s/keycloak-realm-config.yaml
  kubectl rollout restart deployment/keycloak -n alzheimer
  ```

---

## Project Structure

```
alzheimer-system/
├── backend/
│   ├── api-gateway/              Spring Cloud Gateway       (port 8080)
│   ├── discovery-server/         Eureka Server              (port 8761)
│   ├── user-service/             User management            (port 8084)
│   ├── patient-service/          Patient management         (port 8082)
│   ├── cognitive-service/        Cognitive activities       (port 8083)
│   ├── AlzheimerApp/             Main app — Spring Boot 3   (port 8085)
│   └── support-network/          Support network — SB3      (port 8089)
├── frontend/
│   └── alzheimer-angular/        Angular 18 SPA
├── keycloak/                     Custom Keycloak 24 image
├── k8s/
│   ├── namespace.yaml            Namespaces: alzheimer + monitoring
│   ├── configmap.yaml            Global env config
│   ├── gateway-config.yaml       API Gateway CORS override
│   ├── keycloak-realm-config.yaml  Realm auto-import
│   ├── infrastructure.yaml       MySQL 8 + RabbitMQ
│   ├── discovery-server.yaml     Eureka (NodePort 30761)
│   ├── keycloak.yaml             Keycloak (NodePort 30081)
│   ├── microservices.yaml        All 6 backend services
│   ├── frontend.yaml             Angular (NodePort 30420)
│   ├── nexus.yaml                Nexus UI:30000, Docker:30082
│   ├── sonarqube.yaml            SonarQube (NodePort 30005)
│   ├── phpmyadmin.yaml           phpMyAdmin (NodePort 30085)
│   └── secrets.yaml              nexus-creds creation guide
├── k8s-monitoring/
│   └── monitoring.yaml           Prometheus (30090) + Grafana (30030)
├── database/
│   └── init.sql                  Creates all databases
├── Jenkinsfile                   Master orchestrator
├── Jenkinsfile.backend-ci        Backend CI (build+sonar+push)
├── Jenkinsfile.frontend          Frontend CI (build+push)
└── Jenkinsfile.backend-cd        CD (kubectl apply + rollout)
```
