package eu.nebulouscloud.securitymanager.model.opa.allowed.repository;

import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;
import lombok.Getter;
import lombok.Setter;
import eu.nebulouscloud.securitymanager.model.opa.ConstraintSpec;
import eu.nebulouscloud.securitymanager.model.opa.ConstraintStatus;

@Getter
@Setter
@Group("constraints.gatekeeper.sh")
@Version("v1beta1")
public class K8sAllowedRepos extends CustomResource<ConstraintSpec, ConstraintStatus> {

    @Override
    public String toString() {
        return "K8sAllowedRepos{" +
                "spec=" + spec +
                ", status=" + status +
                '}';
    }
}
