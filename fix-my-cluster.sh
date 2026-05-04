#!/bin/bash

echo "🚀 Starting Alzheimer System Cluster Recovery..."

# 1. Disable swap just in case
sudo swapoff -a

# 2. Restart core engines
sudo systemctl restart docker
sudo systemctl restart kubelet

echo "🧹 Cleaning up 'Unknown' or 'Terminating' pods..."

# Delete pods in 'Unknown' state across all namespaces
kubectl get pods -A | grep -i 'Unknown' | awk '{print $1 " " $2}' | xargs -L1 kubectl delete pod -n

# Delete pods in 'Terminating' state that are stuck
kubectl get pods -A | grep -i 'Terminating' | awk '{print $1 " " $2}' | xargs -L1 kubectl delete pod -n --force --grace-period=0

# Force restart Jenkins if it's stuck
kubectl delete pod jenkins-0 -n jenkins --force --grace-period=0 2>/dev/null

echo "✅ Cluster should be healthy in 60 seconds. Check with: kubectl get pods -A"
