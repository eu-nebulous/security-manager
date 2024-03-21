package ubi.nebulous.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CasbinPolicyDTO {
    private String name;
    private String policyItem;
    private String namespace;

}
