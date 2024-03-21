package ubi.nebulous.model.casbin.policy;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;
import lombok.Getter;
import lombok.Setter;

import javax.naming.Name;

@Getter
@Setter
@Group("auth.casbin.org")
@Version("v1")
public class CasbinPolicy extends CustomResource<CasbinPolicySpec,CasbinPolicyStatus> implements Namespaced {

    private String namespace;

    @Override
    public String toString() {
        return "CasbinPolicy{" +
                "metadata=" + getMetadata() +
                ", spec=" + spec +
                ", status=" + status +
                '}';
    }
}
