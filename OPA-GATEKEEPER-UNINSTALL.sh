# delete the constraint template of "disallowed tags"
kubectl delete -f  opa-gatekeeper-library/k8sdisallowedtags.yaml

# delete the constraint template of "replica limits"
kubectl delete -f  opa-gatekeeper-library/k8sreplicalimits.yaml

# delete the constraint template of "disallowed repos"
kubectl delete -f  opa-gatekeeper-library/k8sdisallowedrepos.yaml

# delete the constraint template of "allowed repos"
kubectl delete -f  opa-gatekeeper-library/k8sallowedrepos.yaml

# delete installation of open policy agent
kubectl delete -f https://raw.githubusercontent.com/open-policy-agent/gatekeeper/v3.16.3/deploy/gatekeeper.yaml