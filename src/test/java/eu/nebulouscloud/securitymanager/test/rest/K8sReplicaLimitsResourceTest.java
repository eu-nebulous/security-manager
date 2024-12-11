package eu.nebulouscloud.securitymanager.test.rest;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.model.opa.allowed.K8sReplicaLimits;
import eu.nebulouscloud.securitymanager.model.opa.match.Kind;
import eu.nebulouscloud.securitymanager.service.opa.K8sReplicaLimitsService;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

@QuarkusTest
public class K8sReplicaLimitsResourceTest {

    @InjectMock
    K8sReplicaLimitsService k8sReplicaLimitsService;

    @Test
    public void testCreateOrUpdateK8sReplicaLimits() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName("replica-limits");
        Kind kind = new Kind();
        kind.setApiGroups(Collections.singletonList("apps"));
        kind.setKinds(Collections.singletonList("Deployment"));
        dto.setKinds(Collections.singletonList(kind));
        dto.setNamespace("default");

        K8sReplicaLimits replicaLimits = new K8sReplicaLimits();
        when(k8sReplicaLimitsService.createOrUpdateK8sReplicaLimits(argThat(argument ->
                argument.getName().equals(dto.getName()) &&
                        argument.getNamespace().equals(dto.getNamespace()) &&
                        argument.getKinds().equals(dto.getKinds())
        ))).thenReturn(replicaLimits);

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/k8s-replica-limits")
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode());

        verify(k8sReplicaLimitsService, times(1)).createOrUpdateK8sReplicaLimits(any(ConstraintDTO.class));
    }

    @Test
    public void testListK8sReplicaLimits() {
        K8sReplicaLimits replicaLimits = new K8sReplicaLimits();
        when(k8sReplicaLimitsService.listK8sReplicaLimits("default")).thenReturn(Collections.singletonList(replicaLimits));

        given()
                .when()
                .get("/k8s-replica-limits/default")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("$.size()", is(1));

        verify(k8sReplicaLimitsService, times(1)).listK8sReplicaLimits("default");
    }

    @Test
    public void testDeleteK8sReplicaLimits() {
        when(k8sReplicaLimitsService.k8sReplicaLimitsExists("replica-limits", "default")).thenReturn(true);

        given()
                .when()
                .delete("/k8s-replica-limits/replica-limits?namespace=default")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        verify(k8sReplicaLimitsService, times(1)).deleteK8sReplicaLimits("replica-limits", "default");
    }

    @Test
    public void testDeleteK8sReplicaLimitsNotFound() {
        when(k8sReplicaLimitsService.k8sReplicaLimitsExists("replica-limits", "default")).thenReturn(false);

        given()
                .when()
                .delete("/k8s-replica-limits/replica-limits?namespace=default")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        verify(k8sReplicaLimitsService, never()).deleteK8sReplicaLimits(anyString(), anyString());
    }
}
