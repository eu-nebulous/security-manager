package eu.nebulouscloud.securitymanager.test.rest;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.model.opa.match.Kind;
import eu.nebulouscloud.securitymanager.model.opa.disallowed.repository.K8sDisallowedRepos;
import eu.nebulouscloud.securitymanager.service.opa.K8sDisallowedReposService;


import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

@QuarkusTest
public class K8sDisallowedReposResourceTest {

    @InjectMock
    K8sDisallowedReposService k8sDisallowedReposService;

    @Test
    public void testCreateOrUpdateK8sDisallowedRepos() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName("repo-must-not-be-k8s-gcr-io");
        Kind kind = new Kind();
        kind.setApiGroups(Collections.singletonList(""));
        kind.setKinds(Collections.singletonList("Pod"));
        dto.setKinds(Collections.singletonList(kind));
        dto.setNamespaces(Collections.singletonList("default"));
        dto.setRepos(Collections.singletonList("k8s.gcr.io/"));
        dto.setNamespace("default");

        K8sDisallowedRepos disallowedRepos = new K8sDisallowedRepos();
        when(k8sDisallowedReposService.createOrUpdateK8sDisallowedRepos(argThat(argument ->
                argument.getName().equals(dto.getName()) &&
                        argument.getNamespace().equals(dto.getNamespace()) &&
                        argument.getKinds().equals(dto.getKinds()) &&
                        argument.getNamespaces().equals(dto.getNamespaces()) &&
                        argument.getRepos().equals(dto.getRepos())
        ))).thenReturn(disallowedRepos);

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/k8s-disallowed-repos")
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode());

        verify(k8sDisallowedReposService, times(1)).createOrUpdateK8sDisallowedRepos(any(ConstraintDTO.class));
    }

    @Test
    public void testListK8sDisallowedRepos() {
        K8sDisallowedRepos disallowedRepos = new K8sDisallowedRepos();
        when(k8sDisallowedReposService.listK8sDisallowedRepos("default")).thenReturn(Collections.singletonList(disallowedRepos));

        given()
                .when()
                .get("/k8s-disallowed-repos/default")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("$.size()", is(1));

        verify(k8sDisallowedReposService, times(1)).listK8sDisallowedRepos("default");
    }

    @Test
    public void testDeleteK8sDisallowedRepos() {
        when(k8sDisallowedReposService.k8sDisallowedReposExists("repo-must-not-be-k8s-gcr-io", "default")).thenReturn(true);

        given()
                .when()
                .delete("/k8s-disallowed-repos/repo-must-not-be-k8s-gcr-io?namespace=default")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        verify(k8sDisallowedReposService, times(1)).deleteK8sDisallowedRepos("repo-must-not-be-k8s-gcr-io", "default");
    }

    @Test
    public void testDeleteK8sDisallowedReposNotFound() {
        when(k8sDisallowedReposService.k8sDisallowedReposExists("repo-must-not-be-k8s-gcr-io", "default")).thenReturn(false);

        given()
                .when()
                .delete("/k8s-disallowed-repos/repo-must-not-be-k8s-gcr-io?namespace=default")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        verify(k8sDisallowedReposService, never()).deleteK8sDisallowedRepos(anyString(), anyString());
    }
}