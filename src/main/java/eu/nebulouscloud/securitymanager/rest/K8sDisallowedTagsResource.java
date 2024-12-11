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
import eu.nebulouscloud.securitymanager.model.opa.disallowed.K8sDisallowedTags;
import eu.nebulouscloud.securitymanager.service.opa.K8sDisallowedTagsService;

import java.util.List;

@Path("/k8s-disallowed-tags")
public class K8sDisallowedTagsResource {

    @Inject
    K8sDisallowedTagsService k8sDisallowedTagsService;

    @POST
    @Operation(summary = "Create a K8sDisallowedTags", description = "Creates a K8sDisallowedTags based on the provided DTO.")
    @RequestBody(required = true, content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = ConstraintDTO.class),
            examples = @ExampleObject(name = "example",
                    value = "{\n" +
                            "  \"name\": \"container-image-must-not-have-latest-tag\",\n" +
                            "  \"kinds\": [\n" +
                            "    {\n" +
                            "      \"apiGroups\": [\"\"],\n" +
                            "      \"kinds\": [\"Pod\"]\n" +
                            "    },\n" +
                            "    {\n" +
                            "      \"apiGroups\": [\"apps\"],\n" +
                            "      \"kinds\": [\"Deployment\"]\n" +
                            "    }\n" +
                            "  ],\n" +
                            "  \"namespaces\": [\"default\"],\n" +
                            "  \"tags\": [\"latest\"],\n" +
                            "  \"exemptImages\": [\"openpolicyagent/opa-exp:latest\", \"openpolicyagent/opa-exp2:latest\"]\n" +
                            "}")
    ))
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrUpdateK8sDisallowedTags(ConstraintDTO k8sDisallowedTagsDTO) {
        try {
            K8sDisallowedTags createdTags = k8sDisallowedTagsService.createOrUpdateK8sDisallowedTags(k8sDisallowedTagsDTO);
            return Response.status(Response.Status.CREATED).entity(createdTags).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating/updating K8sDisallowedTags: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{namespace}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieves K8sDisallowedTags CRDs", description = "Retrieves a list of K8sDisallowedTags CRDs.")
    public Response listK8sDisallowedTags(@PathParam("namespace") @DefaultValue("default") String namespace) {
        try {
            List<K8sDisallowedTags> tags = k8sDisallowedTagsService.listK8sDisallowedTags(namespace);
            return Response.ok(tags).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error listing K8sDisallowedTags: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{k8sDisallowedTagsName}")
    @Parameter(name = "k8sDisallowedTagsName", description = "The name of the K8sDisallowedTags to be deleted", required = true)
    @Operation(summary = "Deletes a Kubernetes K8sDisallowedTags CRD", description = "Deletes a K8sDisallowedTags CRD based on the provided k8sDisallowedTagsName.")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteK8sDisallowedTags(@PathParam("k8sDisallowedTagsName") String k8sDisallowedTagsName, @QueryParam("namespace") @DefaultValue("default") String namespace) {
        try {
            if (!k8sDisallowedTagsService.k8sDisallowedTagsExists(k8sDisallowedTagsName, namespace)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("K8sDisallowedTags not found")
                        .build();
            }
            k8sDisallowedTagsService.deleteK8sDisallowedTags(k8sDisallowedTagsName, namespace);
            return Response.ok("K8sDisallowedTags deleted").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting K8sDisallowedTags: " + e.getMessage())
                    .build();
        }
    }
}
