/*
 * Copyright (c) 2025     Ubitech LTD.
 */

package eu.nebulouscloud.securitymanager.service.opa;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ConstraintHandlerProducer {

    @Inject
    K8sAllowedReposService allowedRepos;

    @Inject
    K8sDisallowedReposService disallowedRepos;

    @Inject
    K8sDisallowedTagsService disallowedTags;

    @Inject
    K8sReplicaLimitsService replicaLimits;

    @Produces
    public List<ConstraintHandler> constraintHandlers() {
        return List.of(
                allowedRepos,
                disallowedRepos,
                disallowedTags,
                replicaLimits
        );
    }
}
