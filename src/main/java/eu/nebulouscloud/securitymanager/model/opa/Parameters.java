package eu.nebulouscloud.securitymanager.model.opa;

import lombok.Getter;
import lombok.Setter;
import eu.nebulouscloud.securitymanager.model.opa.parameters.Range;

import java.util.List;

@Getter
@Setter
public class Parameters {
    private List<String> repos;
    private List<Range> ranges;
    private List<String> tags;
    private List<String> exemptImages;

}
