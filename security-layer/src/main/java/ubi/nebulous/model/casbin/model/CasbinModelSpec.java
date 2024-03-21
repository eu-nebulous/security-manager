package ubi.nebulous.model.casbin.model;

import io.fabric8.kubernetes.api.model.KubernetesResource;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CasbinModelSpec implements KubernetesResource {
    private boolean enabled;
    private String modelText;
}
