package eu.nebulouscloud.securitymanager.test.integration;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import eu.nebulouscloud.securitymanager.util.KubernetesClientUtil;
import eu.nebulouscloud.securitymanager.util.TestConstants;
import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.model.opa.disallowed.repository.K8sDisallowedRepos;
import eu.nebulouscloud.securitymanager.service.opa.K8sDisallowedReposService;

import jakarta.inject.Inject;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class K8sDisallowedReposServiceIntegrationTest {

    @Inject
    K8sDisallowedReposService k8sDisallowedReposService;

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
    public void testCreateOrUpdateK8sDisallowedRepos() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName(TestConstants.TEST_REPO_NAME);
        dto.setNamespace(TestConstants.DEFAULT_NAMESPACE);

        K8sDisallowedRepos k8sDisallowedRepos = k8sDisallowedReposService.createOrUpdateK8sDisallowedRepos(dto);

        assertNotNull(k8sDisallowedRepos);
        assertEquals(TestConstants.TEST_REPO_NAME, k8sDisallowedRepos.getMetadata().getName());

        kubernetesClient.resources(K8sDisallowedRepos.class).inNamespace(TestConstants.DEFAULT_NAMESPACE).withName(TestConstants.TEST_REPO_NAME).delete();
    }

    @Test
    public void testListK8sDisallowedRepos() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName(TestConstants.TEST_REPO_NAME);
        dto.setNamespace(TestConstants.DEFAULT_NAMESPACE);
        k8sDisallowedReposService.createOrUpdateK8sDisallowedRepos(dto);

        List<K8sDisallowedRepos> k8sDisallowedReposList = k8sDisallowedReposService.listK8sDisallowedRepos(TestConstants.DEFAULT_NAMESPACE);

        assertNotNull(k8sDisallowedReposList);
        assertFalse(k8sDisallowedReposList.isEmpty());

        // Clean up the created resource
        kubernetesClient.resources(K8sDisallowedRepos.class).inNamespace(TestConstants.DEFAULT_NAMESPACE).withName(TestConstants.TEST_REPO_NAME).delete();
    }

    @Test
    public void testDeleteK8sDisallowedRepos() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName(TestConstants.TEST_REPO_NAME);
        dto.setNamespace(TestConstants.DEFAULT_NAMESPACE);
        k8sDisallowedReposService.createOrUpdateK8sDisallowedRepos(dto);

        assertDoesNotThrow(() -> k8sDisallowedReposService.deleteK8sDisallowedRepos(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE));

        boolean exists = kubernetesClient.resources(K8sDisallowedRepos.class).inNamespace(TestConstants.DEFAULT_NAMESPACE).withName(TestConstants.TEST_REPO_NAME).get() != null;
        assertFalse(exists);
    }

    @Test
    public void testK8sDisallowedReposExists() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName(TestConstants.TEST_REPO_NAME);
        dto.setNamespace(TestConstants.DEFAULT_NAMESPACE);
        k8sDisallowedReposService.createOrUpdateK8sDisallowedRepos(dto);

        boolean exists = k8sDisallowedReposService.k8sDisallowedReposExists(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE);
        assertTrue(exists);

        // Clean up the created resource
        kubernetesClient.resources(K8sDisallowedRepos.class).inNamespace(TestConstants.DEFAULT_NAMESPACE).withName(TestConstants.TEST_REPO_NAME).delete();
    }
}
