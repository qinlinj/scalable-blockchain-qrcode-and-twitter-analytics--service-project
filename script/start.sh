#!/bin/bash

# Script to set up an EKS cluster and deploy services

# Set AWS Profile for the session
export AWS_PROFILE=user1

# Go to cluster folder
cd ../k8s/cluster

# Define cluster name and team AWS account ID
CLUSTER_NAME="phase-3-cluster"
TEAM_AWS_ID="146445828406"

# Create EKS cluster based on the provided configuration file
eksctl create cluster -f cluster.yaml

# Associate IAM OIDC provider with the EKS cluster
eksctl utils associate-iam-oidc-provider --region us-east-1 --cluster $CLUSTER_NAME --approve

# Create IAM service account for the AWS Load Balancer Controller
eksctl create iamserviceaccount --cluster=$CLUSTER_NAME --namespace=kube-system --name=aws-load-balancer-controller --attach-policy-arn=arn:aws:iam::$TEAM_AWS_ID:policy/AWSLoadBalancerControllerIAMPolicy --override-existing-serviceaccounts --approve

# Apply CRDs for AWS Load Balancer Controller from the EKS charts repository
kubectl apply -k "github.com/aws/eks-charts/stable/aws-load-balancer-controller/crds?ref=master"

# Add the EKS Helm chart repository
helm repo add eks https://aws.github.io/eks-charts

# Install or upgrade the AWS Load Balancer Controller using Helm
helm upgrade -i aws-load-balancer-controller eks/aws-load-balancer-controller --set clusterName=$CLUSTER_NAME --set serviceAccount.create=false --set region=us-east-1 --set serviceAccount.name=aws-load-balancer-controller -n kube-system

# Check if AWS Load Balancer Controller pod is running
echo "Waiting for AWS Load Balancer Controller to become operational..."
while ! kubectl get pods -n kube-system | grep aws-load-balancer-controller | grep Running; do
  echo "Waiting for AWS Load Balancer Controller..."
  sleep 10
done
echo "AWS Load Balancer Controller is operational."

# Deploy services using Helm

# Navigate to Kubernetes configuration directory
cd k8s

# Create ingress resources
kubectl create -f Ingress/ingress.yaml

# Install the BLC, QR Code, and Twitter services using Helm
helm install blc helm/blc-vertx
helm install qrcode helm/qr-vertx
helm install twitter helm/twitter-vertx

# Apply the Kubernetes Metrics Server for autoscaling features
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
