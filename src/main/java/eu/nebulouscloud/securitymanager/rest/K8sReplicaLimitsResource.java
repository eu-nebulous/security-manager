package eu.nebulouscloud.securitymanager.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.model.opa.allowed.K8sReplicaLimits;
import eu.nebulouscloud.securitymanager.service.opa.K8sReplicaLimitsService;

import java.util.List;

@Path("/k8s-replica-limits")
public class K8sReplicaLimitsResource {

    @Inject
    K8sReplicaLimitsService k8sReplicaLimitsService;

    @POST
    @Operation(summary = "Create a K8sReplicaLimits", description = "Creates a K8sReplicaLimits based on the provided DTO.")
    @RequestBody(required = true, content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = ConstraintDTO.class),
            examples = @ExampleObject(name = "example",
                    value = "{\n" +
                            "  \"name\": \"replica-limits\",\n" +
                            "  \"kinds\": [\n" +
                            "    {\n" +
                            "      \"apiGroups\": [\"apps\"],\n" +
                            "      \"kinds\": [\"Deployment\"]\n" +
                            "    }\n" +
                            "  ],\n" +
                            "  \"ranges\": [\n" +
                            "    {\n" +
                            "      \"min_replicas\": 3,\n" +
                            "      \"max_replicas\": 50\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}")
    ))
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrUpdateK8sReplicaLimits(ConstraintDTO k8sReplicaLimitsDTO) {
        try {
            K8sReplicaLimits createdLimits = k8sReplicaLimitsService.createOrUpdateK8sReplicaLimits(k8sReplicaLimitsDTO);
            return Response.status(Response.Status.CREATED).entity(createdLimits).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating/updating K8sReplicaLimits: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{namespace}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieves K8sReplicaLimits CRDs", description = "Retrieves a list of K8sReplicaLimits CRDs.")
    public Response listK8sReplicaLimits(@PathParam("namespace") @DefaultValue("default") String namespace) {
        try {
            List<K8sReplicaLimits> limits = k8sReplicaLimitsService.listK8sReplicaLimits(namespace);
            return Response.ok(limits).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error listing K8sReplicaLimits: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{k8sReplicaLimitsName}")
    @Parameter(name = "k8sReplicaLimitsName", description = "The name of the K8sReplicaLimits to be deleted", required = true)
    @Operation(summary = "Deletes a Kubernetes K8sReplicaLimits CRD", description = "Deletes a K8sReplicaLimits CRD based on the provided k8sReplicaLimitsName.")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteK8sReplicaLimits(@PathParam("k8sReplicaLimitsName") String k8sReplicaLimitsName, @QueryParam("namespace") @DefaultValue("default") String namespace) {
        try {
            if (!k8sReplicaLimitsService.k8sReplicaLimitsExists(k8sReplicaLimitsName, namespace)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("K8sReplicaLimits not found")
                        .build();
            }
            k8sReplicaLimitsService.deleteK8sReplicaLimits(k8sReplicaLimitsName, namespace);
            return Response.ok("K8sReplicaLimits deleted").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting K8sReplicaLimits: " + e.getMessage())
                    .build();
        }
    }
}
