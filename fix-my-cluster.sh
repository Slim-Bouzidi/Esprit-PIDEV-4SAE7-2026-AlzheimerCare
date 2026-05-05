#!/bin/bash
echo "🚀 Starting Alzheimer System & DevOps Recovery..."

# 1. Turn off Swap (Kubernetes hates swap)
echo "🔧 Disabling swap..."
sudo swapoff -a

# 2. Fix SonarQube memory requirement
echo "🧠 Setting Elasticsearch memory map..."
sudo sysctl -w vm.max_map_count=262144

# 3. Kill Ghost Pods in ALL namespaces
echo "👻 Deleting Ghost/Unknown pods..."
kubectl delete pods --all-namespaces --field-selector=status.phase!=Running --force

# 4. Restart Jenkins to ensure it wakes up properly
echo "🏗️ Waking up Jenkins..."
kubectl delete pod -l app.kubernetes.io/name=jenkins -n jenkins --force

# 5. Restart Nexus to ensure microservices don't get stuck pulling images
echo "📦 Restarting Nexus Registry..."
kubectl delete pod -l app=nexus -n alzheimer --force

echo "✅ Recovery Complete! Wait 3 minutes for Jenkins and Nexus to fully boot up."
