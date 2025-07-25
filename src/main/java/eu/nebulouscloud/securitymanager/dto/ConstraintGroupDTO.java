/*
 * Copyright (c) 2025     Ubitech LTD.
 */

package eu.nebulouscloud.securitymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConstraintGroupDTO {
    private String name;
    private String namespace;
    private List<ConstraintDTO> constraints;
}
