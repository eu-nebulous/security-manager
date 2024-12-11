package eu.nebulouscloud.securitymanager.test.unit;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.dto.mapper.K8sReplicaLimitsMapper;
import eu.nebulouscloud.securitymanager.model.opa.ConstraintStatus;
import eu.nebulouscloud.securitymanager.model.opa.allowed.K8sReplicaLimits;
import eu.nebulouscloud.securitymanager.service.opa.K8sReplicaLimitsService;
import eu.nebulouscloud.securitymanager.util.TestConstants;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class K8sReplicaLimitsServiceTest {

    @Mock
    KubernetesClient kubernetesClient;

    @Mock
    K8sReplicaLimitsMapper k8sReplicaLimitsMapper;

    @InjectMocks
    K8sReplicaLimitsService k8sReplicaLimitsService;

    @Mock
    MixedOperation<K8sReplicaLimits, KubernetesResourceList<K8sReplicaLimits>, Resource<K8sReplicaLimits>> mixedOperation;

    @Mock
    NonNamespaceOperation<K8sReplicaLimits, KubernetesResourceList<K8sReplicaLimits>, Resource<K8sReplicaLimits>> nonNamespaceOperation;

    @Mock
    Resource<K8sReplicaLimits> resource;

    @Mock
    KubernetesResourceList<K8sReplicaLimits> kubernetesResourceList;

    private ConstraintDTO k8sReplicaLimitsDTO;
    private K8sReplicaLimits k8sReplicaLimits;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize Mockito mocks

        k8sReplicaLimitsDTO = new ConstraintDTO();
        k8sReplicaLimitsDTO.setName(TestConstants.TEST_REPO_NAME);
        k8sReplicaLimitsDTO.setNamespace(TestConstants.DEFAULT_NAMESPACE);

        k8sReplicaLimits = new K8sReplicaLimits();
        k8sReplicaLimits.setMetadata(new ObjectMetaBuilder().withName(TestConstants.TEST_REPO_NAME).build());
        k8sReplicaLimits.setStatus(new ConstraintStatus());

        when(k8sReplicaLimitsMapper.dtoToModel(any(ConstraintDTO.class))).thenReturn(k8sReplicaLimits);
        when(kubernetesClient.resources(K8sReplicaLimits.class)).thenReturn(mixedOperation);
        when(mixedOperation.inNamespace(anyString())).thenReturn(nonNamespaceOperation);
        when(nonNamespaceOperation.withName(anyString())).thenReturn(resource);
    }

    @Test
    void testCreateOrUpdateK8sReplicaLimits() {
        when(nonNamespaceOperation.createOrReplace(any(K8sReplicaLimits.class)))
                .thenReturn(k8sReplicaLimits);

        K8sReplicaLimits result = k8sReplicaLimitsService.createOrUpdateK8sReplicaLimits(k8sReplicaLimitsDTO);

        assertNotNull(result);
        assertEquals(TestConstants.TEST_REPO_NAME, result.getMetadata().getName());
        verify(nonNamespaceOperation, times(1)).createOrReplace(any(K8sReplicaLimits.class));
    }

    @Test
    void testCreateOrUpdateK8sReplicaLimitsException() {
        when(nonNamespaceOperation.createOrReplace(any(K8sReplicaLimits.class)))
                .thenThrow(new KubernetesClientException("Error"));

        K8sReplicaLimits result = k8sReplicaLimitsService.createOrUpdateK8sReplicaLimits(k8sReplicaLimitsDTO);

        assertNull(result);
        verify(nonNamespaceOperation, times(1)).createOrReplace(any(K8sReplicaLimits.class));
    }

    @Test
    void testListK8sReplicaLimits() {
        List<K8sReplicaLimits> k8sReplicaLimitsList = List.of(k8sReplicaLimits);

        when(kubernetesResourceList.getItems()).thenReturn(k8sReplicaLimitsList);
        when(nonNamespaceOperation.list()).thenReturn(kubernetesResourceList);

        List<K8sReplicaLimits> result = k8sReplicaLimitsService.listK8sReplicaLimits(TestConstants.DEFAULT_NAMESPACE);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(nonNamespaceOperation, times(1)).list();
    }

    @Test
    void testDeleteK8sReplicaLimits() {
        when(resource.delete()).thenReturn(null);

        assertDoesNotThrow(() -> k8sReplicaLimitsService.deleteK8sReplicaLimits(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE));
        verify(resource, times(1)).delete();
    }

    @Test
    void testDeleteK8sReplicaLimitsException() {
        when(resource.delete()).thenThrow(new KubernetesClientException("Error"));

        assertThrows(RuntimeException.class, () -> k8sReplicaLimitsService.deleteK8sReplicaLimits(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE));
        verify(resource, times(1)).delete();
    }

    @Test
    void testK8sReplicaLimitsExists() {
        when(resource.get()).thenReturn(k8sReplicaLimits);

        boolean result = k8sReplicaLimitsService.k8sReplicaLimitsExists(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE);

        assertTrue(result);
        verify(resource, times(1)).get();
    }

    @Test
    void testK8sReplicaLimitsExistsNotFound() {
        when(resource.get()).thenReturn(null);

        boolean result = k8sReplicaLimitsService.k8sReplicaLimitsExists(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE);

        assertFalse(result);
        verify(resource, times(1)).get();
    }
}
