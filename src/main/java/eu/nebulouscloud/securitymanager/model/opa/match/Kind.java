package eu.nebulouscloud.securitymanager.model.opa.match;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Kind {
    private List<String> apiGroups;
    private List<String> kinds;
}
