package eu.nebulouscloud.securitymanager.service.opa;

import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.dto.mapper.K8sAllowedReposMapper;
import eu.nebulouscloud.securitymanager.model.opa.ConstraintStatus;
import eu.nebulouscloud.securitymanager.model.opa.allowed.repository.K8sAllowedRepos;

import java.util.List;


@ApplicationScoped
public class K8sAllowedReposService {

    private static final Logger logger = Logger.getLogger(K8sAllowedReposService.class);

    @Inject
    KubernetesClient kubernetesClient;

    @Inject
    K8sAllowedReposMapper k8sAllowedReposMapper;

    public K8sAllowedRepos createOrUpdateK8sAllowedRepos(ConstraintDTO dto) {
        try {
            String effectiveNamespace = (dto.getNamespace() != null && !dto.getNamespace().isEmpty()) ? dto.getNamespace() : "default";

            K8sAllowedRepos k8sAllowedRepos = k8sAllowedReposMapper.dtoToModel(dto);
            k8sAllowedRepos.setMetadata(new ObjectMetaBuilder()
                    .withName(dto.getName())
                    .build());
            k8sAllowedRepos.setStatus(new ConstraintStatus());

            kubernetesClient.resources(K8sAllowedRepos.class)
                    .inNamespace(effectiveNamespace)
                    .createOrReplace(k8sAllowedRepos);

            logger.infof("K8sAllowedRepos created/updated successfully: %s", k8sAllowedRepos);

            return k8sAllowedRepos;
        } catch (KubernetesClientException e) {
            logger.errorf(e, "Error processing create/update K8sAllowedRepos message.");
            return null;
        }
    }

    public List<K8sAllowedRepos> listK8sAllowedRepos(String namespace) {
        try {
            List<K8sAllowedRepos> k8sAllowedReposList = kubernetesClient.resources(K8sAllowedRepos.class)
                    .inNamespace(namespace)
                    .list()
                    .getItems();
            logger.infof("Received List K8sAllowedRepos Message for Namespace: %s", namespace);
            return k8sAllowedReposList;
        } catch (KubernetesClientException e) {
            throw new KubernetesClientException("Error listing K8sAllowedRepos: " + e.getMessage(), e);
        }
    }

    public void deleteK8sAllowedRepos(String name, String namespace) {
        String effectiveNamespace = (namespace != null && !namespace.isEmpty()) ? namespace : "default";
        try {
            kubernetesClient.resources(K8sAllowedRepos.class)
                    .inNamespace(effectiveNamespace)
                    .withName(name)
                    .delete();
            logger.infof("K8sAllowedRepos '%s' in namespace '%s' deleted successfully", name, namespace);
        } catch (KubernetesClientException e) {
            logger.errorf(e, "Error processing deleting K8sAllowedRepos.");
            throw new RuntimeException("Error deleting K8sAllowedRepos: " + e.getMessage(), e);
        }
    }

    public boolean k8sAllowedReposExists(String name, String namespace) {
        String effectiveNamespace = (namespace != null && !namespace.isEmpty()) ? namespace : "default";
        try {
            return kubernetesClient.resources(K8sAllowedRepos.class)
                    .inNamespace(effectiveNamespace)
                    .withName(name)
                    .get() != null;
        } catch (KubernetesClientException e) {
            throw new KubernetesClientException("Error checking K8sAllowedRepos existence: " + e.getMessage(), e);
        }
    }
}