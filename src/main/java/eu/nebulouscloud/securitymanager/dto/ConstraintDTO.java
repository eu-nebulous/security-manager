package eu.nebulouscloud.securitymanager.dto;

import eu.nebulouscloud.securitymanager.model.opa.match.Kind;
import eu.nebulouscloud.securitymanager.model.opa.parameters.Range;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConstraintDTO {
    private String name;
    private String namespace;


    private List<Kind> kinds;
    private List<String> namespaces;
    private List<String> repos;
    private List<Range> ranges;
    private List<String> tags;
    private List<String> exemptImages;
}
