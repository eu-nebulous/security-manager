package eu.nebulouscloud.securitymanager.service.opa;

import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.dto.mapper.K8sDisallowedTagsMapper;
import eu.nebulouscloud.securitymanager.model.opa.ConstraintStatus;
import eu.nebulouscloud.securitymanager.model.opa.disallowed.K8sDisallowedTags;

import java.util.List;

@ApplicationScoped
public class K8sDisallowedTagsService {

    private static final Logger logger = Logger.getLogger(K8sDisallowedTagsService.class);

    @Inject
    KubernetesClient kubernetesClient;

    @Inject
    K8sDisallowedTagsMapper k8sDisallowedTagsMapper;

    public K8sDisallowedTags createOrUpdateK8sDisallowedTags(ConstraintDTO dto) {
        try {
            String effectiveNamespace = (dto.getNamespace() != null && !dto.getNamespace().isEmpty()) ? dto.getNamespace() : "default";

            K8sDisallowedTags k8sDisallowedTags = k8sDisallowedTagsMapper.dtoToModel(dto);
            k8sDisallowedTags.setMetadata(new ObjectMetaBuilder()
                    .withName(dto.getName())
                    .build());
            k8sDisallowedTags.setStatus(new ConstraintStatus());

            kubernetesClient.resources(K8sDisallowedTags.class)
                    .inNamespace(effectiveNamespace)
                    .createOrReplace(k8sDisallowedTags);

            logger.infof("K8sDisallowedTags created/updated successfully: %s", k8sDisallowedTags);

            return k8sDisallowedTags;
        } catch (KubernetesClientException e) {
            logger.errorf(e, "Error processing create/update K8sDisallowedTags message.");
            return null;
        }
    }

    public List<K8sDisallowedTags> listK8sDisallowedTags(String namespace) {
        try {
            List<K8sDisallowedTags> k8sDisallowedTagsList = kubernetesClient.resources(K8sDisallowedTags.class)
                    .inNamespace(namespace)
                    .list()
                    .getItems();
            logger.infof("Received List K8sDisallowedTags Message for Namespace: %s", namespace);
            return k8sDisallowedTagsList;
        } catch (KubernetesClientException e) {
            throw new KubernetesClientException("Error listing K8sDisallowedTags: " + e.getMessage(), e);
        }
    }

    public void deleteK8sDisallowedTags(String name, String namespace) {
        String effectiveNamespace = (namespace != null && !namespace.isEmpty()) ? namespace : "default";
        try {
            kubernetesClient.resources(K8sDisallowedTags.class)
                    .inNamespace(effectiveNamespace)
                    .withName(name)
                    .delete();
            logger.infof("K8sDisallowedTags '%s' in namespace '%s' deleted successfully", name, namespace);
        } catch (KubernetesClientException e) {
            logger.errorf(e, "Error processing deleting K8sDisallowedTags.");
            throw new RuntimeException("Error deleting K8sDisallowedTags: " + e.getMessage(), e);
        }
    }

    public boolean k8sDisallowedTagsExists(String name, String namespace) {
        String effectiveNamespace = (namespace != null && !namespace.isEmpty()) ? namespace : "default";
        try {
            return kubernetesClient.resources(K8sDisallowedTags.class)
                    .inNamespace(effectiveNamespace)
                    .withName(name)
                    .get() != null;
        } catch (KubernetesClientException e) {
            throw new KubernetesClientException("Error checking K8sDisallowedTags existence: " + e.getMessage(), e);
        }
    }
}
