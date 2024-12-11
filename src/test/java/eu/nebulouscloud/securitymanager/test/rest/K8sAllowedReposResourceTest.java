package eu.nebulouscloud.securitymanager.test.rest;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.model.opa.match.Kind;
import eu.nebulouscloud.securitymanager.model.opa.allowed.repository.K8sAllowedRepos;
import eu.nebulouscloud.securitymanager.service.opa.K8sAllowedReposService;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;



@QuarkusTest
public class K8sAllowedReposResourceTest {

    @InjectMock
    K8sAllowedReposService k8sAllowedReposService;

    @Test
    public void testCreateOrUpdateK8sAllowedRepos() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName("repo-is-openpolicyagent");
        Kind kind = new Kind();
        kind.setApiGroups(Collections.singletonList(""));
        kind.setKinds(Collections.singletonList("Pod"));
        dto.setKinds(Collections.singletonList(kind));
        dto.setNamespaces(Collections.singletonList("default"));
        dto.setRepos(Collections.singletonList("docker.io/"));
        dto.setNamespace("default");

        K8sAllowedRepos allowedRepos = new K8sAllowedRepos();
        when(k8sAllowedReposService.createOrUpdateK8sAllowedRepos(argThat(argument ->
                argument.getName().equals(dto.getName()) &&
                        argument.getNamespace().equals(dto.getNamespace()) &&
                        argument.getKinds().equals(dto.getKinds()) &&
                        argument.getNamespaces().equals(dto.getNamespaces()) &&
                        argument.getRepos().equals(dto.getRepos())
        ))).thenReturn(allowedRepos);

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/k8s-allowed-repos")
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode());

        verify(k8sAllowedReposService, times(1)).createOrUpdateK8sAllowedRepos(any(ConstraintDTO.class));
    }

    @Test
    public void testListK8sAllowedRepos() {
        K8sAllowedRepos allowedRepos = new K8sAllowedRepos();
        when(k8sAllowedReposService.listK8sAllowedRepos("default")).thenReturn(Collections.singletonList(allowedRepos));

        given()
                .when()
                .get("/k8s-allowed-repos/default")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("$.size()", is(1));

        verify(k8sAllowedReposService, times(1)).listK8sAllowedRepos("default");
    }

    @Test
    public void testDeleteK8sAllowedRepos() {
        when(k8sAllowedReposService.k8sAllowedReposExists("repo-is-openpolicyagent", "default")).thenReturn(true);

        given()
                .when()
                .delete("/k8s-allowed-repos/repo-is-openpolicyagent?namespace=default")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        verify(k8sAllowedReposService, times(1)).deleteK8sAllowedRepos("repo-is-openpolicyagent", "default");
    }

    @Test
    public void testDeleteK8sAllowedReposNotFound() {
        when(k8sAllowedReposService.k8sAllowedReposExists("repo-is-openpolicyagent", "default")).thenReturn(false);

        given()
                .when()
                .delete("/k8s-allowed-repos/repo-is-openpolicyagent?namespace=default")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        verify(k8sAllowedReposService, never()).deleteK8sAllowedRepos(anyString(), anyString());
    }
}