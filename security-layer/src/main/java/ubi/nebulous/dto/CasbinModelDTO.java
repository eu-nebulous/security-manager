package ubi.nebulous.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CasbinModelDTO {
    private String name;
    private boolean enabled;
    private String modelText;
    private String namespace;

}
