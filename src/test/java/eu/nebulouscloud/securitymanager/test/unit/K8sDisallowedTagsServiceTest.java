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
import eu.nebulouscloud.securitymanager.dto.mapper.K8sDisallowedTagsMapper;
import eu.nebulouscloud.securitymanager.model.opa.ConstraintStatus;
import eu.nebulouscloud.securitymanager.model.opa.disallowed.K8sDisallowedTags;
import eu.nebulouscloud.securitymanager.service.opa.K8sDisallowedTagsService;
import eu.nebulouscloud.securitymanager.util.TestConstants;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class K8sDisallowedTagsServiceTest {

    @Mock
    KubernetesClient kubernetesClient;

    @Mock
    K8sDisallowedTagsMapper k8sDisallowedTagsMapper;

    @InjectMocks
    K8sDisallowedTagsService k8sDisallowedTagsService;

    @Mock
    MixedOperation<K8sDisallowedTags, KubernetesResourceList<K8sDisallowedTags>, Resource<K8sDisallowedTags>> mixedOperation;

    @Mock
    NonNamespaceOperation<K8sDisallowedTags, KubernetesResourceList<K8sDisallowedTags>, Resource<K8sDisallowedTags>> nonNamespaceOperation;

    @Mock
    Resource<K8sDisallowedTags> resource;

    @Mock
    KubernetesResourceList<K8sDisallowedTags> kubernetesResourceList;

    private ConstraintDTO k8sDisallowedTagsDTO;
    private K8sDisallowedTags k8sDisallowedTags;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize Mockito mocks

        k8sDisallowedTagsDTO = new ConstraintDTO();
        k8sDisallowedTagsDTO.setName(TestConstants.TEST_REPO_NAME);
        k8sDisallowedTagsDTO.setNamespace(TestConstants.DEFAULT_NAMESPACE);
        k8sDisallowedTagsDTO.setTags(List.of(TestConstants.LATEST_TAG));
        k8sDisallowedTagsDTO.setExemptImages(List.of(TestConstants.EXEMPT_IMAGE_1, TestConstants.EXEMPT_IMAGE_2));

        k8sDisallowedTags = new K8sDisallowedTags();
        k8sDisallowedTags.setMetadata(new ObjectMetaBuilder().withName(TestConstants.TEST_REPO_NAME).build());
        k8sDisallowedTags.setStatus(new ConstraintStatus());

        when(k8sDisallowedTagsMapper.dtoToModel(any(ConstraintDTO.class))).thenReturn(k8sDisallowedTags);
        when(kubernetesClient.resources(K8sDisallowedTags.class)).thenReturn(mixedOperation);
        when(mixedOperation.inNamespace(anyString())).thenReturn(nonNamespaceOperation);
        when(nonNamespaceOperation.withName(anyString())).thenReturn(resource);
    }

    @Test
    void testCreateOrUpdateK8sDisallowedTags() {
        when(nonNamespaceOperation.createOrReplace(any(K8sDisallowedTags.class)))
                .thenReturn(k8sDisallowedTags);

        K8sDisallowedTags result = k8sDisallowedTagsService.createOrUpdateK8sDisallowedTags(k8sDisallowedTagsDTO);

        assertNotNull(result);
        assertEquals(TestConstants.TEST_REPO_NAME, result.getMetadata().getName());
        verify(nonNamespaceOperation, times(1)).createOrReplace(any(K8sDisallowedTags.class));
    }

    @Test
    void testDeleteK8sDisallowedTagsException() {
        when(resource.delete()).thenThrow(new KubernetesClientException("Error"));

        assertThrows(RuntimeException.class, () -> k8sDisallowedTagsService.deleteK8sDisallowedTags(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE));
        verify(resource, times(1)).delete();
    }

    @Test
    void testK8sDisallowedTagsExists() {
        when(resource.get()).thenReturn(k8sDisallowedTags);

        boolean result = k8sDisallowedTagsService.k8sDisallowedTagsExists(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE);

        assertTrue(result);
        verify(resource, times(1)).get();
    }

    @Test
    void testK8sDisallowedTagsExistsNotFound() {
        when(resource.get()).thenReturn(null);

        boolean result = k8sDisallowedTagsService.k8sDisallowedTagsExists(TestConstants.TEST_REPO_NAME, TestConstants.DEFAULT_NAMESPACE);

        assertFalse(result);
        verify(resource, times(1)).get();
    }
}