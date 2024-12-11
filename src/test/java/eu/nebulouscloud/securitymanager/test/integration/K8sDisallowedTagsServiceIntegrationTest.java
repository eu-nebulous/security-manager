package eu.nebulouscloud.securitymanager.test.integration;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import eu.nebulouscloud.securitymanager.util.KubernetesClientUtil;
import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.model.opa.disallowed.K8sDisallowedTags;
import eu.nebulouscloud.securitymanager.service.opa.K8sDisallowedTagsService;

import jakarta.inject.Inject;
import eu.nebulouscloud.securitymanager.util.TestConstants;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class K8sDisallowedTagsServiceIntegrationTest {

    @Inject
    K8sDisallowedTagsService k8sDisallowedTagsService;

    private static KubernetesClient kubernetesClient;

    @BeforeAll
    public static void setUp() {
        kubernetesClient = KubernetesClientUtil.createKubernetesClient();
    }

    @AfterAll
    public static void tearDown() {
        kubernetesClient.close();
    }

    @Test
    public void testCreateOrUpdateK8sDisallowedTags() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName(TestConstants.TEST_REPO_NAME);
        dto.setNamespace(TestConstants.DEFAULT_NAMESPACE);
        dto.setTags(List.of(TestConstants.LATEST_TAG));
        dto.setExemptImages(List.of(TestConstants.EXEMPT_IMAGE_1, TestConstants.EXEMPT_IMAGE_2));

        K8sDisallowedTags k8sDisallowedTags = k8sDisallowedTagsService.createOrUpdateK8sDisallowedTags(dto);

        assertNotNull(k8sDisallowedTags);
        assertEquals(TestConstants.TEST_REPO_NAME, k8sDisallowedTags.getMetadata().getName());

        kubernetesClient.resources(K8sDisallowedTags.class).inNamespace(TestConstants.DEFAULT_NAMESPACE).withName(TestConstants.TEST_REPO_NAME).delete();
    }

    @Test
    public void testListK8sDisallowedTags() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName(TestConstants.TEST_REPO_NAME);
        dto.setNamespace(TestConstants.DEFAULT_NAMESPACE);
        dto.setTags(List.of(TestConstants.LATEST_TAG));
        dto.setExemptImages(List.of(TestConstants.EXEMPT_IMAGE_1, TestConstants.EXEMPT_IMAGE_2));
        k8sDisallowedTagsService.createOrUpdateK8sDisallowedTags(dto);

        List<K8sDisallowedTags> k8sDisallowedTagsList = k8sDisallowedTagsService.listK8sDisallowedTags(TestConstants.DEFAULT_NAMESPACE);

        assertNotNull(k8sDisallowedTagsList);
        assertFalse(k8sDisallowedTagsList.isEmpty());

        // Clean up the created resource
        kubernetesClient.resources(K8sDisallowedTags.class).inNamespace(TestConstants.DEFAULT_NAMESPACE).withName(TestConstants.TEST_REPO_NAME).delete();
    }

    @Test
    public void testDeleteK8sDisallowedTags() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName(TestConstants.TEST_REPO_NAME);
        dto.setNamespace(TestConstants.DEFAULT_NAMESPACE);
        dto.setTags(List.of(TestConstants.LATEST_TAG));
        dto.setExemptImages(List.of(TestConstants.EXEMPT_IMAGE_1, TestConstants.EXEMPT_IMAGE_2));
        k8sDisallowedTagsService.createOrUpdateK8sDisallowedTags(dto);

        assertDoesNotThrow(() -> k8sDisallowedTagsService.deleteK8sDisallowedTags(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE));

        boolean exists = kubernetesClient.resources(K8sDisallowedTags.class).inNamespace(TestConstants.DEFAULT_NAMESPACE).withName(TestConstants.TEST_REPO_NAME).get() != null;
        assertFalse(exists);
    }

    @Test
    public void testK8sDisallowedTagsExists() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName(TestConstants.TEST_REPO_NAME);
        dto.setNamespace(TestConstants.DEFAULT_NAMESPACE);
        dto.setTags(List.of(TestConstants.LATEST_TAG));
        dto.setExemptImages(List.of(TestConstants.EXEMPT_IMAGE_1, TestConstants.EXEMPT_IMAGE_2));
        k8sDisallowedTagsService.createOrUpdateK8sDisallowedTags(dto);

        boolean exists = k8sDisallowedTagsService.k8sDisallowedTagsExists(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE);
        assertTrue(exists);

        // Clean up the created resource
        kubernetesClient.resources(K8sDisallowedTags.class).inNamespace(TestConstants.DEFAULT_NAMESPACE).withName(TestConstants.TEST_REPO_NAME).delete();
    }
}
