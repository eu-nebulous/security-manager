package eu.nebulouscloud.securitymanager.model.opa;

import eu.nebulouscloud.securitymanager.model.opa.match.Kind;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Match {
    private List<Kind> kinds;
    private List<String> namespaces;
}
