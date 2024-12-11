package eu.nebulouscloud.securitymanager.test.integration;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import eu.nebulouscloud.securitymanager.util.KubernetesClientUtil;
import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;

import jakarta.inject.Inject;
import eu.nebulouscloud.securitymanager.model.opa.allowed.K8sReplicaLimits;
import eu.nebulouscloud.securitymanager.service.opa.K8sReplicaLimitsService;
import eu.nebulouscloud.securitymanager.util.TestConstants;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class K8sReplicaLimitsServiceIntegrationTest {

    @Inject
    K8sReplicaLimitsService k8sReplicaLimitsService;

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
    public void testCreateOrUpdateK8sReplicaLimits() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName(TestConstants.TEST_REPO_NAME);
        dto.setNamespace(TestConstants.DEFAULT_NAMESPACE);

        K8sReplicaLimits k8sReplicaLimits = k8sReplicaLimitsService.createOrUpdateK8sReplicaLimits(dto);

        assertNotNull(k8sReplicaLimits);
        assertEquals(TestConstants.TEST_REPO_NAME, k8sReplicaLimits.getMetadata().getName());

        kubernetesClient.resources(K8sReplicaLimits.class).inNamespace(TestConstants.DEFAULT_NAMESPACE).withName(TestConstants.TEST_REPO_NAME).delete();
    }

    @Test
    public void testListK8sReplicaLimits() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName(TestConstants.TEST_REPO_NAME);
        dto.setNamespace(TestConstants.DEFAULT_NAMESPACE);
        k8sReplicaLimitsService.createOrUpdateK8sReplicaLimits(dto);

        List<K8sReplicaLimits> k8sReplicaLimitsList = k8sReplicaLimitsService.listK8sReplicaLimits(TestConstants.DEFAULT_NAMESPACE);

        assertNotNull(k8sReplicaLimitsList);
        assertFalse(k8sReplicaLimitsList.isEmpty());

        // Clean up the created resource
        kubernetesClient.resources(K8sReplicaLimits.class).inNamespace(TestConstants.DEFAULT_NAMESPACE).withName(TestConstants.TEST_REPO_NAME).delete();
    }

    @Test
    public void testDeleteK8sReplicaLimits() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName(TestConstants.TEST_REPO_NAME);
        dto.setNamespace(TestConstants.DEFAULT_NAMESPACE);
        k8sReplicaLimitsService.createOrUpdateK8sReplicaLimits(dto);

        assertDoesNotThrow(() -> k8sReplicaLimitsService.deleteK8sReplicaLimits(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE));

        boolean exists = kubernetesClient.resources(K8sReplicaLimits.class).inNamespace(TestConstants.DEFAULT_NAMESPACE).withName(TestConstants.TEST_REPO_NAME).get() != null;
        assertFalse(exists);
    }

    @Test
    public void testK8sReplicaLimitsExists() {
        ConstraintDTO dto = new ConstraintDTO();
        dto.setName(TestConstants.TEST_REPO_NAME);
        dto.setNamespace(TestConstants.DEFAULT_NAMESPACE);
        k8sReplicaLimitsService.createOrUpdateK8sReplicaLimits(dto);

        boolean exists = k8sReplicaLimitsService.k8sReplicaLimitsExists(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE);
        assertTrue(exists);

        // Clean up the created resource
        kubernetesClient.resources(K8sReplicaLimits.class).inNamespace(TestConstants.DEFAULT_NAMESPACE).withName(TestConstants.TEST_REPO_NAME).delete();
    }
}
