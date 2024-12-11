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
import eu.nebulouscloud.securitymanager.model.opa.allowed.repository.K8sAllowedRepos;
import eu.nebulouscloud.securitymanager.service.opa.K8sAllowedReposService;

import java.util.List;

@Path("/k8s-allowed-repos")
public class K8sAllowedReposResource {

    @Inject
    K8sAllowedReposService k8sAllowedReposService;

    @POST
    @Operation(summary = "Create a K8sAllowedRepos", description = "Creates a K8sAllowedRepos based on the provided DTO.")
    @RequestBody(required = true, content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = ConstraintDTO.class),
            examples = @ExampleObject(name = "example",
                    value = "{\n" +
                            "  \"name\": \"repo-is-openpolicyagent\",\n" +
                            "  \"kinds\": [\n" +
                            "    {\n" +
                            "      \"apiGroups\": [\"\"],\n" +
                            "      \"kinds\": [\"Pod\"]\n" +
                            "    }\n" +
                            "  ],\n" +
                            "  \"namespaces\": [\"default\"],\n" +
                            "  \"repos\": [\n" +
                            "  \"docker.io/\",\n" +
                            "  \"hub.docker.com/\",\n" +
                            "  \"quay.io/\",\n" +
                            "  \"gcr.io/\",\n" +
                            "  \"public.ecr.aws/\",\n" +
                            "  \"mcr.microsoft.com/\",\n" +
                            "  \"ghcr.io/\",\n" +
                            "  \"registry.access.redhat.com/\",\n" +
                            "  \"goharbor.io/\",\n" +
                            "  \"docker.bintray.io/\",\n" +
                            "  \"k8s.gcr.io/\"\n" +
                            "]\n" +
                            "}")
    ))
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrUpdateK8sAllowedRepos(ConstraintDTO k8sAllowedReposDTO) {
        try {
            K8sAllowedRepos createdRepos = k8sAllowedReposService.createOrUpdateK8sAllowedRepos(k8sAllowedReposDTO);
            return Response.status(Response.Status.CREATED).entity(createdRepos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating/updating K8sAllowedRepos: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{namespace}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieves K8sAllowedRepos CRDs", description = "Retrieves a list of K8sAllowedRepos CRDs.")
    public Response listK8sAllowedRepos(@PathParam("namespace") @DefaultValue("default") String namespace) {
        try {
            List<K8sAllowedRepos> repos = k8sAllowedReposService.listK8sAllowedRepos(namespace);
            return Response.ok(repos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error listing K8sAllowedRepos: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{k8sAllowedReposName}")
    @Parameter(name = "k8sAllowedReposName", description = "The name of the K8sAllowedRepos to be deleted", required = true)
    @Operation(summary = "Deletes a Kubernetes K8sAllowedRepos CRD", description = "Deletes a K8sAllowedRepos CRD based on the provided k8sAllowedReposName.")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteK8sAllowedRepos(@PathParam("k8sAllowedReposName") String k8sAllowedReposName, @QueryParam("namespace") @DefaultValue("default") String namespace) {
        try {
            if (!k8sAllowedReposService.k8sAllowedReposExists(k8sAllowedReposName, namespace)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("K8sAllowedRepos not found")
                        .build();
            }
            k8sAllowedReposService.deleteK8sAllowedRepos(k8sAllowedReposName, namespace);
            return Response.ok("K8sAllowedRepos deleted").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting K8sAllowedRepos: " + e.getMessage())
                    .build();
        }
    }
}