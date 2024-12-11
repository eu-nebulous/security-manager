package eu.nebulouscloud.securitymanager.model.opa;

import io.fabric8.kubernetes.api.model.KubernetesResource;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConstraintSpec implements KubernetesResource {
    private Match match;
    private Parameters parameters;
}
