package eu.nebulouscloud.securitymanager.rest;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClientException;
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
import org.jboss.logging.Logger;
import eu.nebulouscloud.securitymanager.dto.DeploymentDTO;
import eu.nebulouscloud.securitymanager.model.DeploymentModel;
import eu.nebulouscloud.securitymanager.service.DeploymentService;

import java.util.List;

@Path("/deployment")
public class DeploymentResource {
    private static final Logger LOG = Logger.getLogger(DeploymentResource.class);

    @Inject
    DeploymentService kubernetesDeploymentService;


    /**
     * lists deployments in the specified namespace.
     *
     * @param namespace from which to retrieve deployments
     * @return A list of Deployments
     */
    @GET
    @Path("/{namespace}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieves K8s Deployments", description = "Retrieves a list of Kubernetes deployments.")
    public Response getDeployments(@PathParam("namespace")  @DefaultValue("default") String namespace) {
        try {
            List<Deployment> deployments = kubernetesDeploymentService.getDeployments(namespace);
            return Response.ok(deployments).build();
        } catch (RuntimeException e) {
            LOG.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving deployments: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Creates a Kubernetes Deployment based on the provided DeploymentDTO
     *
     * @param deploymentDTO containing the deployment specifications
     * @return The created Deployment resource as object
     */
    @POST
    @Operation(summary = "Create a Kubernetes Deployment", description = "Creates a Kubernetes Deployment based on the provided DeploymentDTO.")
    @RequestBody(required = true, content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = DeploymentModel.class),
            examples = @ExampleObject(name = "example",
                    value = "{\"name\": \"nginx-deployment\", \"image\": \"nginx:1.13.1\", \"replicas\": 1, \"containerPort\": 80}")
    ))
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createDeployment(DeploymentDTO deploymentDTO) {
        try {
            Deployment deployment = kubernetesDeploymentService.createDeployment(deploymentDTO);
            return Response.status(Response.Status.CREATED)
                    .entity(deployment)
                    .build();
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating deployment: " + e.getMessage())
                    .build();
        }
    }


    /**
     * Deletes a Kubernetes Deployment with the given name
     *
     * @param deploymentName The name of the deployment to be deleted
     * @return response indicating the outcome of the delete operation
     */
    @DELETE
    @Parameter(name = "deploymentname", description = "The name of the deployment to be deleted", required = true)
    @Operation(summary = "Deletes a Kubernetes Deployment", description = "Deletes a Kubernetes Deployment based on the provided deploymentname.")
    @Path("/{deploymentname}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteDeployment(@PathParam("deploymentname") String deploymentName,
                                     @QueryParam("namespace") @DefaultValue("default") String namespace) {
        try {
            // check if exists
            boolean exists = kubernetesDeploymentService.deploymentExists(deploymentName, namespace);
            if (!exists) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(String.format("Deployment '%s' not found in namespace '%s'.", deploymentName, namespace))
                        .build();
            }
            // delete
            kubernetesDeploymentService.deleteDeployment(deploymentName, namespace);
            return Response.ok()
                    .entity(String.format("Deployment '%s' deleted successfully from namespace '%s'.", deploymentName, namespace))
                    .build();
        } catch (RuntimeException e) {
            LOG.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(String.format("Error deleting deployment '%s' from namespace '%s': %s", deploymentName, namespace, e.getMessage()))
                    .build();
        }
    }
}
