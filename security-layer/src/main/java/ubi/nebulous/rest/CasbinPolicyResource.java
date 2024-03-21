package ubi.nebulous.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import ubi.nebulous.dto.CasbinModelDTO;
import ubi.nebulous.dto.CasbinPolicyDTO;
import ubi.nebulous.dto.mapper.CasbinPolicyMapper;
import ubi.nebulous.model.casbin.policy.CasbinPolicy;
import ubi.nebulous.service.CasbinPolicyService;

import java.util.List;

@Path("/casbin-policy")
public class CasbinPolicyResource {

    @Inject
    CasbinPolicyService casbinPolicyService;

    /**
     * Creates a CasbinPolicy based on the provided DTO
     *
     * @param casbinPolicyDTO containing the relevant policy
     * @return The created CasbinPolicy resource as object
     */
    @POST
    @Operation(summary = "Create or Update a CasbinPolicy", description = "Creates or updates a CasbinPolicy based on the provided Policy DTO.")
    @RequestBody(required = true, content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = CasbinPolicy.class),
            examples = @ExampleObject(name = "example",
                    value = "{\n" +
                            "    \"name\": \"allowed-repo\",\n" +
                            "    \"policyItem\": \"p, \\\"nginx:1.13.1\\\",allow\\np, \\\"nginx:1.14.1\\\",deny\"\n" +
                            "  }")
    ))
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrUpdateCasbinPolicy(CasbinPolicyDTO casbinPolicyDTO) {
        try {
            CasbinPolicy createdPolicy = casbinPolicyService.createOrUpdateCasbinPolicy(casbinPolicyDTO);
            return Response.status(Response.Status.CREATED).entity(createdPolicy).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating/updating CasbinPolicy: " + e.getMessage())
                    .build();
        }
    }



    /**
     * lists crd of CasbinPolicy in the specified namespace.
     *
     * @param namespace from which to retrieve CasbinPolicy
     * @return A list of CasbinPolicy
     */
    @GET
    @Path("/{namespace}")
    @Operation(summary = "List CasbinPolicies", description = "Lists all CasbinPolicies in the specified namespace.")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listCasbinPolicies(@PathParam("namespace") String namespace) {
        try {
            List<CasbinPolicy> policies = casbinPolicyService.listCasbinPolicies(namespace);
            return Response.ok(policies).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error listing CasbinPolicies: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Deletes CasbinPolicy with the given name
     *
     * @param casbinPolicyName The name of the CasbinPolicy to be deleted
     * @return response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{casbinPolicyName}")
    @Operation(summary = "Delete a CasbinPolicy", description = "Deletes a CasbinPolicy with the specified name from the given namespace.")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteCasbinPolicy(@PathParam("casbinPolicyName") String casbinPolicyName, @QueryParam("namespace") @DefaultValue("default") String namespace) {
        try {
            if (!casbinPolicyService.casbinPolicyExists(casbinPolicyName, namespace)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("CasbinPolicy not found")
                        .build();
            }
            casbinPolicyService.deleteCasbinPolicy(casbinPolicyName, namespace);
            return Response.ok("CasbinPolicy deleted").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting CasbinPolicy: " + e.getMessage())
                    .build();
        }
    }
}

