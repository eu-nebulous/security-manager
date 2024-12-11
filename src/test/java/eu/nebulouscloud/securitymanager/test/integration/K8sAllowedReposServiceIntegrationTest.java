package eu.nebulouscloud.securitymanager.test.integration;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import eu.nebulouscloud.securitymanager.util.KubernetesClientUtil;
import eu.nebulouscloud.securitymanager.util.TestConstants;
import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.model.opa.allowed.repository.K8sAllowedRepos;
import eu.nebulouscloud.securitymanager.service.opa.K8sAllowedReposService;

import jakarta.inject.Inject;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class K8sAllowedReposServiceIntegrationTest {

    @Inject
    K8sAllowedReposService k8sAllowedReposService;

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
    public void testCreateOrUpdateK8sAllowedRepos() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName(TestConstants.TEST_REPO_NAME);
        dto.setNamespace(TestConstants.DEFAULT_NAMESPACE);

        K8sAllowedRepos k8sAllowedRepos = k8sAllowedReposService.createOrUpdateK8sAllowedRepos(dto);

        assertNotNull(k8sAllowedRepos);
        assertEquals(TestConstants.TEST_REPO_NAME, k8sAllowedRepos.getMetadata().getName());

        kubernetesClient.resources(K8sAllowedRepos.class).inNamespace(TestConstants.DEFAULT_NAMESPACE).withName(TestConstants.TEST_REPO_NAME).delete();
    }

    @Test
    public void testListK8sAllowedRepos() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName(TestConstants.TEST_REPO_NAME);
        dto.setNamespace(TestConstants.DEFAULT_NAMESPACE);
        k8sAllowedReposService.createOrUpdateK8sAllowedRepos(dto);

        List<K8sAllowedRepos> k8sAllowedReposList = k8sAllowedReposService.listK8sAllowedRepos(TestConstants.DEFAULT_NAMESPACE);

        assertNotNull(k8sAllowedReposList);
        assertFalse(k8sAllowedReposList.isEmpty());

        // Clean up the created resource
        kubernetesClient.resources(K8sAllowedRepos.class).inNamespace(TestConstants.DEFAULT_NAMESPACE).withName(TestConstants.TEST_REPO_NAME).delete();
    }

    @Test
    public void testDeleteK8sAllowedRepos() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName(TestConstants.TEST_REPO_NAME);
        dto.setNamespace(TestConstants.DEFAULT_NAMESPACE);
        k8sAllowedReposService.createOrUpdateK8sAllowedRepos(dto);

        assertDoesNotThrow(() -> k8sAllowedReposService.deleteK8sAllowedRepos(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE));

        boolean exists = kubernetesClient.resources(K8sAllowedRepos.class).inNamespace(TestConstants.DEFAULT_NAMESPACE).withName(TestConstants.TEST_REPO_NAME).get() != null;
        assertFalse(exists);
    }

    @Test
    public void testK8sAllowedReposExists() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName(TestConstants.TEST_REPO_NAME);
        dto.setNamespace(TestConstants.DEFAULT_NAMESPACE);
        k8sAllowedReposService.createOrUpdateK8sAllowedRepos(dto);

        boolean exists = k8sAllowedReposService.k8sAllowedReposExists(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE);
        assertTrue(exists);

        // Clean up the created resource
        kubernetesClient.resources(K8sAllowedRepos.class).inNamespace(TestConstants.DEFAULT_NAMESPACE).withName(TestConstants.TEST_REPO_NAME).delete();
    }
}
