package ubi.nebulous.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentDTO implements Serializable {
    private String namespace;
    private String name;
    private String image;
    private int replicas;
    private int containerPort;

    @Override
    public String toString() {
        return "DeploymentDTO{" +
                "namespace='" + namespace + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", replicas=" + replicas +
                ", containerPort=" + containerPort +
                '}';
    }
}
