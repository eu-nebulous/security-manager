/*
 * Copyright (c) 2025     Ubitech LTD.
 */

package eu.nebulouscloud.securitymanager.rest;

import eu.nebulouscloud.securitymanager.dto.ConstraintGroupDTO;
import eu.nebulouscloud.securitymanager.service.opa.ConstraintApplierService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/constraints")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConstraintResource {

    @Inject
    ConstraintApplierService constraintApplierService;

    @POST
    @Path("/apply-group")
    public Response applyGroup(ConstraintGroupDTO dto) {
        try {
            List<String> created = constraintApplierService.applyGroup(dto);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("createdConstraints", created);
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("status", "error", "message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity(Map.of("status", "error", "message", "Unexpected error: " + e.getMessage()))
                    .build();
        }
    }

}
