package ubi.nebulous.rest;

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
import ubi.nebulous.dto.CasbinModelDTO;
import ubi.nebulous.dto.mapper.CasbinModelMapper;
import ubi.nebulous.model.DeploymentModel;
import ubi.nebulous.model.casbin.model.CasbinModel;
import ubi.nebulous.service.CasbinModelService;

import java.util.List;

@Path("/casbin-model")
public class CasbinModelResource {

    @Inject
    CasbinModelService casbinModelService;

    /**
     * Creates a CasbinModel based on the provided DTO
     *
     * @param casbinModelDTO The CasbinModel containing the relevant policy model
     * @return The created CasbinModel resource as object
     */
    @POST
    @Operation(summary = "Create a CasbinModel", description = "Creates a CasbinModel based on the provided Policy Model.")
    @RequestBody(required = true, content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = CasbinModelDTO.class),
            examples = @ExampleObject(name = "example",
                    value = "{\n" +
                            "  \"name\": \"allowed-repo\",\n" +
                            "  \"enabled\": true,\n" +
                            "  \"modelText\": \"[request_definition]\\nr =  obj\\n\\n[policy_definition]\\np =  obj,eft\\n\\n[policy_effect]\\ne = !some(where (p.eft == deny))\\n\\n[matchers]\\nm = ${NAMESPACE} == \\\"default\\\" && ${RESOURCE} ==\\\"deployments\\\" && access(${OBJECT}.Spec.Template.Spec.Containers , 0, \\\"Image\\\") == p.obj\"\n" +
                            "}")
    ))
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrUpdateCasbinModel(CasbinModelDTO casbinModelDTO) {
        try {
            CasbinModel createdModel = casbinModelService.createOrUpdateCasbinModel(casbinModelDTO);
            return Response.status(Response.Status.CREATED).entity(createdModel).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating/updating CasbinModel: " + e.getMessage())
                    .build();
        }
    }

    /**
     * lists crd of CasbinModel in the specified namespace.
     *
     * @param namespace from which to retrieve CasbinModel
     * @return A list of CasbinModel
     */
    @GET
    @Path("/{namespace}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieves CasbinModel CRDs", description = "Retrieves a list of CasbinModel CRDs.")
    public Response listCasbinModels(@PathParam("namespace") @DefaultValue("default") String namespace) {
        try {
            List<CasbinModel> models = casbinModelService.listCasbinModels(namespace);
            return Response.ok(models).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error listing CasbinModels: " + e.getMessage())
                    .build();
        }
    }


    /**
     * Deletes CasbinModel with the given name
     *
     * @param casbinModelName The name of the CasbinModel to be deleted
     * @return response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{casbinModelName}")
    @Parameter(name = "casbinmodelname", description = "The name of the CasbinModel to be deleted", required = true)
    @Operation(summary = "Deletes a Kubernetes CasbinModel CRD", description = "Deletes a CasbinModel CRD based on the provided casbinmodelname.")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteCasbinModel(@PathParam("casbinModelName") String casbinModelName, @QueryParam("namespace") @DefaultValue("default") String namespace) {
        try {
            if (!casbinModelService.casbinModelExists(casbinModelName, namespace)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("CasbinModel not found")
                        .build();
            }
            casbinModelService.deleteCasbinModel(casbinModelName, namespace);
            return Response.ok("CasbinModel deleted").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting CasbinModel: " + e.getMessage())
                    .build();
        }
    }
}