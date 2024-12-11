package eu.nebulouscloud.securitymanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentModel {
    private String namespace;
    private String name;
    private String image;
    private int replicas;
    private int containerPort;


    // if there is no namespace use default
    public String getNamespace() {
        return namespace != null ? namespace : "default";
    }
}
