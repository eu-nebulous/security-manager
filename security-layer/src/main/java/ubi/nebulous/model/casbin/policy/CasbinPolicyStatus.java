package ubi.nebulous.model.casbin.policy;

import io.fabric8.kubernetes.api.model.KubernetesResource;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CasbinPolicyStatus implements KubernetesResource {}
