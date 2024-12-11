package eu.nebulouscloud.securitymanager.rest;

import eu.nebulouscloud.securitymanager.model.opa.disallowed.repository.K8sDisallowedRepos;
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
import eu.nebulouscloud.securitymanager.service.opa.K8sDisallowedReposService;


import java.util.List;

@Path("/k8s-disallowed-repos")
public class K8sDisallowedReposResource {

    @Inject
    K8sDisallowedReposService k8sDisallowedReposService;

    @POST
    @Operation(summary = "Create a K8sDisallowedRepos", description = "Creates a K8sDisallowedRepos based on the provided DTO.")
    @RequestBody(required = true, content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = ConstraintDTO.class),
            examples = @ExampleObject(name = "example",
                    value = "{\n" +
                            "  \"name\": \"repo-must-not-be-k8s-gcr-io\",\n" +
                            "  \"kinds\": [\n" +
                            "    {\n" +
                            "      \"apiGroups\": [\"\"],\n" +
                            "      \"kinds\": [\"Pod\"]\n" +
                            "    }\n" +
                            "  ],\n" +
                            "  \"namespaces\": [\"default\"],\n" +
                            "  \"repos\": [\n" +
                            "  \"k8s.gcr.io/\"\n" +
                            "]\n" +
                            "}")
    ))
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrUpdateK8sDisallowedRepos(ConstraintDTO k8sDisallowedReposDTO) {
        try {
            K8sDisallowedRepos createdRepos = k8sDisallowedReposService.createOrUpdateK8sDisallowedRepos(k8sDisallowedReposDTO);
            return Response.status(Response.Status.CREATED).entity(createdRepos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating/updating K8sDisallowedRepos: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{namespace}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieves K8sDisallowedRepos CRDs", description = "Retrieves a list of K8sDisallowedRepos CRDs.")
    public Response listK8sDisallowedRepos(@PathParam("namespace") @DefaultValue("default") String namespace) {
        try {
            List<K8sDisallowedRepos> repos = k8sDisallowedReposService.listK8sDisallowedRepos(namespace);
            return Response.ok(repos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error listing K8sDisallowedRepos: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{k8sDisallowedReposName}")
    @Parameter(name = "k8sDisallowedReposName", description = "The name of the K8sDisallowedRepos to be deleted", required = true)
    @Operation(summary = "Deletes a Kubernetes K8sDisallowedRepos CRD", description = "Deletes a K8sDisallowedRepos CRD based on the provided k8sDisallowedReposName.")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteK8sDisallowedRepos(@PathParam("k8sDisallowedReposName") String k8sDisallowedReposName, @QueryParam("namespace") @DefaultValue("default") String namespace) {
        try {
            if (!k8sDisallowedReposService.k8sDisallowedReposExists(k8sDisallowedReposName, namespace)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("K8sDisallowedRepos not found")
                        .build();
            }
            k8sDisallowedReposService.deleteK8sDisallowedRepos(k8sDisallowedReposName, namespace);
            return Response.ok("K8sDisallowedRepos deleted").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting K8sDisallowedRepos: " + e.getMessage())
                    .build();
        }
    }
}