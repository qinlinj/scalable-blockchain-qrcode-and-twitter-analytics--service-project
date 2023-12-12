#!/bin/bash

# End Process

CLUSTER_NAME="phase-3-cluster"

helm uninstall blc 

helm uninstall qrcode

helm uninstall twitter

kubectl delete ingress ingress-test

eksctl delete cluster --region=us-east-1 --name=$CLUSTER_NAME --wait