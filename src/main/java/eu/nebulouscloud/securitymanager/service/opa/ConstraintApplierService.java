/*
 * Copyright (c) 2025     Ubitech LTD.
 */

package eu.nebulouscloud.securitymanager.service.opa;

import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.dto.ConstraintGroupDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ConstraintApplierService {

    @Inject
    List<ConstraintHandler> handlers;

    public List<String> applyGroup(ConstraintGroupDTO groupDto) {
        String baseName = groupDto.getName();
        String namespace = groupDto.getNamespace() != null ? groupDto.getNamespace() : "default";

        List<String> createdConstraints = new ArrayList<>();

        for (ConstraintDTO dto : groupDto.getConstraints()) {
            if (dto.getType() == null) {
                throw new IllegalArgumentException("Missing 'type' in constraint: " + dto);
            }

            dto.setName(baseName + "-" + dto.getType().getSuffix());
            dto.setNamespace(namespace);

            boolean handled = false;

            for (ConstraintHandler handler : handlers) {
                if (handler.supports(dto)) {
                    handler.createOrUpdate(dto);
                    createdConstraints.add(dto.getName());
                    handled = true;
                    break;
                }
            }

            if (!handled) {
                throw new IllegalArgumentException("No handler found for constraint type: " + dto.getType());
            }
        }

        return createdConstraints;
    }
}