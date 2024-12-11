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
import eu.nebulouscloud.securitymanager.dto.mapper.K8sDisallowedReposMapper;
import eu.nebulouscloud.securitymanager.model.opa.ConstraintStatus;
import eu.nebulouscloud.securitymanager.model.opa.disallowed.repository.K8sDisallowedRepos;
import eu.nebulouscloud.securitymanager.service.opa.K8sDisallowedReposService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class K8sDisallowedReposServiceTest {

    @Mock
    KubernetesClient kubernetesClient;

    @Mock
    K8sDisallowedReposMapper k8sDisallowedReposMapper;

    @InjectMocks
    K8sDisallowedReposService k8sDisallowedReposService;

    @Mock
    MixedOperation<K8sDisallowedRepos, KubernetesResourceList<K8sDisallowedRepos>, Resource<K8sDisallowedRepos>> mixedOperation;

    @Mock
    NonNamespaceOperation<K8sDisallowedRepos, KubernetesResourceList<K8sDisallowedRepos>, Resource<K8sDisallowedRepos>> nonNamespaceOperation;

    @Mock
    Resource<K8sDisallowedRepos> resource;

    @Mock
    KubernetesResourceList<K8sDisallowedRepos> kubernetesResourceList;

    private ConstraintDTO k8sDisallowedReposDTO;
    private K8sDisallowedRepos k8sDisallowedRepos;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize Mockito mocks

        k8sDisallowedReposDTO = new ConstraintDTO();
        k8sDisallowedReposDTO.setName(TestConstants.TEST_REPO_NAME);
        k8sDisallowedReposDTO.setNamespace(TestConstants.DEFAULT_NAMESPACE);

        k8sDisallowedRepos = new K8sDisallowedRepos();
        k8sDisallowedRepos.setMetadata(new ObjectMetaBuilder().withName(TestConstants.TEST_REPO_NAME).build());
        k8sDisallowedRepos.setStatus(new ConstraintStatus());

        when(k8sDisallowedReposMapper.dtoToModel(any(ConstraintDTO.class))).thenReturn(k8sDisallowedRepos);
        when(kubernetesClient.resources(K8sDisallowedRepos.class)).thenReturn(mixedOperation);
        when(mixedOperation.inNamespace(anyString())).thenReturn(nonNamespaceOperation);
        when(nonNamespaceOperation.withName(anyString())).thenReturn(resource);
    }

    @Test
    void testCreateOrUpdateK8sDisallowedRepos() {
        when(nonNamespaceOperation.createOrReplace(any(K8sDisallowedRepos.class)))
                .thenReturn(k8sDisallowedRepos);

        K8sDisallowedRepos result = k8sDisallowedReposService.createOrUpdateK8sDisallowedRepos(k8sDisallowedReposDTO);

        assertNotNull(result);
        assertEquals(TestConstants.TEST_REPO_NAME, result.getMetadata().getName());
        verify(nonNamespaceOperation, times(1)).createOrReplace(any(K8sDisallowedRepos.class));
    }

    @Test
    void testCreateOrUpdateK8sDisallowedReposException() {
        when(nonNamespaceOperation.createOrReplace(any(K8sDisallowedRepos.class)))
                .thenThrow(new KubernetesClientException("Error"));

        K8sDisallowedRepos result = k8sDisallowedReposService.createOrUpdateK8sDisallowedRepos(k8sDisallowedReposDTO);

        assertNull(result);
        verify(nonNamespaceOperation, times(1)).createOrReplace(any(K8sDisallowedRepos.class));
    }

    @Test
    void testListK8sDisallowedRepos() {
        List<K8sDisallowedRepos> k8sDisallowedReposList = List.of(k8sDisallowedRepos);

        when(kubernetesResourceList.getItems()).thenReturn(k8sDisallowedReposList);
        when(nonNamespaceOperation.list()).thenReturn(kubernetesResourceList);

        List<K8sDisallowedRepos> result = k8sDisallowedReposService.listK8sDisallowedRepos(TestConstants.DEFAULT_NAMESPACE);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(nonNamespaceOperation, times(1)).list();
    }

    @Test
    void testDeleteK8sDisallowedRepos() {
        when(resource.delete()).thenReturn(null);

        assertDoesNotThrow(() -> k8sDisallowedReposService.deleteK8sDisallowedRepos(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE));
        verify(resource, times(1)).delete();
    }

    @Test
    void testDeleteK8sDisallowedReposException() {
        when(resource.delete()).thenThrow(new KubernetesClientException("Error"));

        assertThrows(RuntimeException.class, () -> k8sDisallowedReposService.deleteK8sDisallowedRepos(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE));
        verify(resource, times(1)).delete();
    }

    @Test
    void testK8sDisallowedReposExists() {
        when(resource.get()).thenReturn(k8sDisallowedRepos);

        boolean result = k8sDisallowedReposService.k8sDisallowedReposExists(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE);

        assertTrue(result);
        verify(resource, times(1)).get();
    }

    @Test
    void testK8sDisallowedReposExistsNotFound() {
        when(resource.get()).thenReturn(null);

        boolean result = k8sDisallowedReposService.k8sDisallowedReposExists(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE);

        assertFalse(result);
        verify(resource, times(1)).get();
    }
}
