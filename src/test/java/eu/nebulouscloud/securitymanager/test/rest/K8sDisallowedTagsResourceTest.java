package eu.nebulouscloud.securitymanager.test.rest;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.model.opa.disallowed.K8sDisallowedTags;
import eu.nebulouscloud.securitymanager.model.opa.match.Kind;
import eu.nebulouscloud.securitymanager.service.opa.K8sDisallowedTagsService;
import eu.nebulouscloud.securitymanager.util.TestConstants;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

@QuarkusTest
public class K8sDisallowedTagsResourceTest {

    @InjectMock
    K8sDisallowedTagsService k8sDisallowedTagsService;

    @Test
    public void testCreateOrUpdateK8sDisallowedTags() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName("container-image-must-not-have-latest-tag");
        Kind kind1 = new Kind();
        kind1.setApiGroups(Collections.singletonList(""));
        kind1.setKinds(Collections.singletonList("Pod"));
        Kind kind2 = new Kind();
        kind2.setApiGroups(Collections.singletonList("apps"));
        kind2.setKinds(Collections.singletonList("Deployment"));
        dto.setKinds(List.of(kind1, kind2));
        dto.setNamespaces(List.of(TestConstants.DEFAULT_NAMESPACE));
        dto.setTags(List.of(TestConstants.LATEST_TAG));
        dto.setExemptImages(List.of(TestConstants.EXEMPT_IMAGE_1, TestConstants.EXEMPT_IMAGE_2));

        K8sDisallowedTags disallowedTags = new K8sDisallowedTags();
        when(k8sDisallowedTagsService.createOrUpdateK8sDisallowedTags(argThat(argument ->
                argument.getName().equals(dto.getName()) &&
                        argument.getNamespaces().equals(dto.getNamespaces()) &&
                        argument.getKinds().equals(dto.getKinds())
        ))).thenReturn(disallowedTags);

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/k8s-disallowed-tags")
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode());

        verify(k8sDisallowedTagsService, times(1)).createOrUpdateK8sDisallowedTags(any(ConstraintDTO.class));
    }

    @Test
    public void testListK8sDisallowedTags() {
        K8sDisallowedTags disallowedTags = new K8sDisallowedTags();
        when(k8sDisallowedTagsService.listK8sDisallowedTags(TestConstants.DEFAULT_NAMESPACE)).thenReturn(Collections.singletonList(disallowedTags));

        given()
                .when()
                .get("/k8s-disallowed-tags/default")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("$.size()", is(1));

        verify(k8sDisallowedTagsService, times(1)).listK8sDisallowedTags(TestConstants.DEFAULT_NAMESPACE);
    }

    @Test
    public void testDeleteK8sDisallowedTags() {
        when(k8sDisallowedTagsService.k8sDisallowedTagsExists("container-image-must-not-have-latest-tag", TestConstants.DEFAULT_NAMESPACE)).thenReturn(true);

        given()
                .when()
                .delete("/k8s-disallowed-tags/container-image-must-not-have-latest-tag?namespace=default")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        verify(k8sDisallowedTagsService, times(1)).deleteK8sDisallowedTags("container-image-must-not-have-latest-tag", TestConstants.DEFAULT_NAMESPACE);
    }

    @Test
    public void testDeleteK8sDisallowedTagsNotFound() {
        when(k8sDisallowedTagsService.k8sDisallowedTagsExists("container-image-must-not-have-latest-tag", TestConstants.DEFAULT_NAMESPACE)).thenReturn(false);

        given()
                .when()
                .delete("/k8s-disallowed-tags/container-image-must-not-have-latest-tag?namespace=default")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        verify(k8sDisallowedTagsService, never()).deleteK8sDisallowedTags(anyString(), anyString());
    }
}
