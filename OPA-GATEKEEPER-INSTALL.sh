# Cluster admin permissions binding before the installation
kubectl create clusterrolebinding cluster-admin-binding \
    --clusterrole cluster-admin \
    --user $USER

# Deploy a released version of Gatekeeper in cluster with a prebuilt image
kubectl apply -f https://raw.githubusercontent.com/open-policy-agent/gatekeeper/v3.16.3/deploy/gatekeeper.yaml

# Allowed repos custom constraint template install for pods and deployments
kubectl apply -f  opa-gatekeeper-library/k8sallowedrepos.yaml

# Disallowed repos custom constraint template install for pods and deployments
kubectl apply -f  opa-gatekeeper-library/k8sdisallowedrepos.yaml

# Requires that objects with the field spec.replicas specify a number of replicas within defined ranges
kubectl apply -f  opa-gatekeeper-library/k8sreplicalimits.yaml

# Requires container images to have an image tag different from the ones in the specified list
kubectl apply -f  opa-gatekeeper-library/k8sdisallowedtags.yaml
