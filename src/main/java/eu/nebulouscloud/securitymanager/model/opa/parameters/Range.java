package eu.nebulouscloud.securitymanager.model.opa.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Range {
    @JsonProperty("min_replicas")
    private int minReplicas;

    @JsonProperty("max_replicas")
    private int maxReplicas;
}