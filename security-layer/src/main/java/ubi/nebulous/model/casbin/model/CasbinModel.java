package ubi.nebulous.model.casbin.model;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Group("auth.casbin.org")
@Version("v1")
public class CasbinModel extends CustomResource<CasbinModelSpec,CasbinModelStatus> implements Namespaced {

    private String namespace;

    @Override
    public String toString() {
        return "CasbinModel{" +
                "spec=" + spec +
                ", status=" + status +
                '}';
    }
}

