#!/bin/bash

$dau bash -c '

# Grant cluster-admin permissions to the current kubectl user
kubectl create clusterrolebinding cluster-admin-binding \
  --clusterrole=cluster-admin \
  --user="$USER"

# Set the location of your constraint templates
REPO_URL="https://raw.githubusercontent.com/eu-nebulous/security-manager/dev/opa-gatekeeper-library"

# Install Gatekeeper
kubectl apply -f https://raw.githubusercontent.com/open-policy-agent/gatekeeper/v3.16.3/deploy/gatekeeper.yaml

echo "Applying custom constraint templates..."
kubectl apply -f "$REPO_URL/k8sallowedrepos.yaml"
kubectl apply -f "$REPO_URL/k8sdisallowedrepos.yaml"
kubectl apply -f "$REPO_URL/k8sreplicalimits.yaml"
kubectl apply -f "$REPO_URL/k8sdisallowedtags.yaml"
'

echo "OPA Gatekeeper installation completed."

