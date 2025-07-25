/*
 * Copyright (c) 2025     Ubitech LTD.
 */

package eu.nebulouscloud.securitymanager.service.opa;

import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;

public interface ConstraintHandler {

    // Called to create or update the constraint in Kubernetes
    void createOrUpdate(ConstraintDTO dto);

    // Returns true if this handler supports the given constraint type
    boolean supports(ConstraintDTO dto);
}

