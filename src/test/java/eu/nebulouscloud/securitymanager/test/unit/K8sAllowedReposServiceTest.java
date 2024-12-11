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
import eu.nebulouscloud.securitymanager.util.TestConstants;
import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.dto.mapper.K8sAllowedReposMapper;
import eu.nebulouscloud.securitymanager.model.opa.ConstraintStatus;
import eu.nebulouscloud.securitymanager.model.opa.allowed.repository.K8sAllowedRepos;
import eu.nebulouscloud.securitymanager.service.opa.K8sAllowedReposService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class K8sAllowedReposServiceTest {

    @Mock
    KubernetesClient kubernetesClient;

    @Mock
    K8sAllowedReposMapper k8sAllowedReposMapper;

    @InjectMocks
    K8sAllowedReposService k8sAllowedReposService;

    @Mock
    MixedOperation<K8sAllowedRepos, KubernetesResourceList<K8sAllowedRepos>, Resource<K8sAllowedRepos>> mixedOperation;

    @Mock
    NonNamespaceOperation<K8sAllowedRepos, KubernetesResourceList<K8sAllowedRepos>, Resource<K8sAllowedRepos>> nonNamespaceOperation;

    @Mock
    Resource<K8sAllowedRepos> resource;

    @Mock
    KubernetesResourceList<K8sAllowedRepos> kubernetesResourceList;

    private ConstraintDTO k8sAllowedReposDTO;
    private K8sAllowedRepos k8sAllowedRepos;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize Mockito mocks

        k8sAllowedReposDTO = new ConstraintDTO();
        k8sAllowedReposDTO.setName(TestConstants.TEST_REPO_NAME);
        k8sAllowedReposDTO.setNamespace(TestConstants.DEFAULT_NAMESPACE);

        k8sAllowedRepos = new K8sAllowedRepos();
        k8sAllowedRepos.setMetadata(new ObjectMetaBuilder().withName(TestConstants.TEST_REPO_NAME).build());
        k8sAllowedRepos.setStatus(new ConstraintStatus());

        when(k8sAllowedReposMapper.dtoToModel(any(ConstraintDTO.class))).thenReturn(k8sAllowedRepos);
        when(kubernetesClient.resources(K8sAllowedRepos.class)).thenReturn(mixedOperation);
        when(mixedOperation.inNamespace(anyString())).thenReturn(nonNamespaceOperation);
        when(nonNamespaceOperation.withName(anyString())).thenReturn(resource);
    }

    @Test
    void testCreateOrUpdateK8sAllowedRepos() {
        when(nonNamespaceOperation.createOrReplace(any(K8sAllowedRepos.class)))
                .thenReturn(k8sAllowedRepos);

        K8sAllowedRepos result = k8sAllowedReposService.createOrUpdateK8sAllowedRepos(k8sAllowedReposDTO);

        assertNotNull(result);
        assertEquals(TestConstants.TEST_REPO_NAME, result.getMetadata().getName());
        verify(nonNamespaceOperation, times(1)).createOrReplace(any(K8sAllowedRepos.class));
    }

    @Test
    void testCreateOrUpdateK8sAllowedReposException() {
        when(nonNamespaceOperation.createOrReplace(any(K8sAllowedRepos.class)))
                .thenThrow(new KubernetesClientException("Error"));

        K8sAllowedRepos result = k8sAllowedReposService.createOrUpdateK8sAllowedRepos(k8sAllowedReposDTO);

        assertNull(result);
        verify(nonNamespaceOperation, times(1)).createOrReplace(any(K8sAllowedRepos.class));
    }

    @Test
    void testListK8sAllowedRepos() {
        List<K8sAllowedRepos> k8sAllowedReposList = List.of(k8sAllowedRepos);

        when(kubernetesResourceList.getItems()).thenReturn(k8sAllowedReposList);
        when(nonNamespaceOperation.list()).thenReturn(kubernetesResourceList);

        List<K8sAllowedRepos> result = k8sAllowedReposService.listK8sAllowedRepos(TestConstants.DEFAULT_NAMESPACE);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(nonNamespaceOperation, times(1)).list();
    }

    @Test
    void testDeleteK8sAllowedRepos() {
        when(resource.delete()).thenReturn(null);

        assertDoesNotThrow(() -> k8sAllowedReposService.deleteK8sAllowedRepos(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE));
        verify(resource, times(1)).delete();
    }

    @Test
    void testDeleteK8sAllowedReposException() {
        when(resource.delete()).thenThrow(new KubernetesClientException("Error"));

        assertThrows(RuntimeException.class, () -> k8sAllowedReposService.deleteK8sAllowedRepos(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE));
        verify(resource, times(1)).delete();
    }

    @Test
    void testK8sAllowedReposExists() {
        when(resource.get()).thenReturn(k8sAllowedRepos);

        boolean result = k8sAllowedReposService.k8sAllowedReposExists(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE);

        assertTrue(result);
        verify(resource, times(1)).get();
    }

    @Test
    void testK8sAllowedReposExistsNotFound() {
        when(resource.get()).thenReturn(null);

        boolean result = k8sAllowedReposService.k8sAllowedReposExists(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE);

        assertFalse(result);
        verify(resource, times(1)).get();
    }
}
