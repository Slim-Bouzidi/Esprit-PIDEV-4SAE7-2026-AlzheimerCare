pipeline {
    agent {
        kubernetes {
            yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: maven
    image: maven:3.9.6-eclipse-temurin-17
    command: ['cat']
    tty: true
  - name: node
    image: node:20-alpine
    command: ['cat']
    tty: true
  - name: docker
    image: docker:latest
    command: ['cat']
    tty: true
    volumeMounts:
    - name: docker-sock
      mountPath: /var/run/docker.sock
  - name: kubectl
    image: alpine/k8s:1.29.0
    command: ['cat']
    tty: true
    securityContext:
      runAsUser: 0
  volumes:
  - name: docker-sock
    hostPath:
      path: /var/run/docker.sock
'''
        }
    }

    options {
        disableConcurrentBuilds()
    }

    environment {
        NEXUS_REGISTRY = "192.168.192.130:30083"
        NEXUS_PASSWORD = "admin1234" 
    }

    triggers {
        // Poll GitHub every 5 minutes for changes
        pollSCM('H/5 * * * *') 
    }

    stages {
        stage('Detect Changes') {
            steps {
                script {
                    // Check which files changed in the last commit
                    def changedFiles = sh(script: 'git diff --name-only HEAD~1', returnStdout: true).trim()
                    echo "Changed files: ${changedFiles}"
                    
                    env.BACKEND_CHANGED = changedFiles.contains('backend/') ? 'true' : 'false'
                    env.FRONTEND_CHANGED = changedFiles.contains('frontend/') ? 'true' : 'false'
                    env.KEYCLOAK_CHANGED = changedFiles.contains('keycloak/') ? 'true' : 'false'
                }
            }
        }

        stage('Login to Nexus') {
            steps {
                container('docker') {
                    sh "echo ${NEXUS_PASSWORD} | docker login ${NEXUS_REGISTRY} --username admin --password-stdin"
                }
            }
        }

        stage('Build & Push Backend') {
            when { expression { return env.BACKEND_CHANGED == 'true' } }
            steps {
                container('maven') {
                    dir('backend') {
                        sh 'mvn clean install -DskipTests'
                    }
                }
                container('docker') {
                    sh '''
                        # Build, Tag, and Push each backend service
                        for service in user-service api-gateway cognitive-service patient-service discovery-server AlzheimerApp; do
                            if [ "$service" == "AlzheimerApp" ]; then
                                img_name="alzheimer-main-app"
                                ctx="./backend/AlzheimerApp"
                            else
                                img_name="alzheimer-$service"
                                ctx="./backend/$service"
                            fi
                            
                            docker build -t $NEXUS_REGISTRY/$img_name:latest $ctx
                            docker push $NEXUS_REGISTRY/$img_name:latest
                        done
                    '''
                }
            }
        }

        stage('Build & Push Keycloak') {
            when { expression { return env.KEYCLOAK_CHANGED == 'true' } }
            steps {
                container('docker') {
                    sh '''
                        docker build -t $NEXUS_REGISTRY/alzheimer-keycloak:latest -f keycloak/Dockerfile .
                        docker push $NEXUS_REGISTRY/alzheimer-keycloak:latest
                    '''
                }
            }
        }

        stage('Build & Push Frontend') {
            when { expression { return env.FRONTEND_CHANGED == 'true' } }
            steps {
                container('node') {
                    dir('frontend/alzheimer-angular') {
                        sh 'npm install'
                        sh 'npm run build'
                    }
                }
                container('docker') {
                    sh '''
                        docker build -t $NEXUS_REGISTRY/alzheimer-frontend:latest ./frontend/alzheimer-angular
                        docker push $NEXUS_REGISTRY/alzheimer-frontend:latest
                    '''
                }
            }
        }

        stage('Deploy to K8s') {
            steps {
                container('kubectl') {
                    sh '''
                        # Apply all manifests
                        kubectl apply -f k8s/ -n alzheimer
                        
                        # Restart only the app deployments to pull new images
                        kubectl rollout restart deployment user-service api-gateway cognitive-service patient-service discovery-server alzheimerapp frontend keycloak -n alzheimer
                    '''
                }
            }
        }
    }
}
