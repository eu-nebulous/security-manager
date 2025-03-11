# Define the repository URL as an environment variable
REPO_URL="https://raw.githubusercontent.com/eu-nebulous/security-manager/dev/opa-gatekeeper-library"


# delete the constraint template of "disallowed tags"
kubectl delete -f  "$REPO_URL/k8sdisallowedtags.yaml"

# delete the constraint template of "replica limits"
kubectl delete -f  "$REPO_URL/k8sreplicalimits.yaml"

# delete the constraint template of "disallowed repos"
kubectl delete -f   "$REPO_URL/k8sdisallowedrepos.yaml"

# delete the constraint template of "allowed repos"
kubectl delete -f   "$REPO_URL/k8sallowedrepos.yaml"

# delete installation of open policy agent
kubectl delete -f https://raw.githubusercontent.com/open-policy-agent/gatekeeper/v3.16.3/deploy/gatekeeper.yaml
