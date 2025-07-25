package eu.nebulouscloud.securitymanager.service.opa;

import eu.nebulouscloud.securitymanager.model.opa.ConstraintType;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.dto.mapper.K8sDisallowedReposMapper;
import eu.nebulouscloud.securitymanager.model.opa.ConstraintStatus;
import eu.nebulouscloud.securitymanager.model.opa.disallowed.repository.K8sDisallowedRepos;

import java.util.List;

@ApplicationScoped
public class K8sDisallowedReposService implements ConstraintHandler{


    private static final Logger logger = Logger.getLogger(K8sDisallowedReposService.class);

    @Inject
    KubernetesClient kubernetesClient;

    @Inject
    K8sDisallowedReposMapper k8sDisallowedReposMapper;

    public K8sDisallowedRepos createOrUpdateK8sDisallowedRepos(ConstraintDTO dto){
        try {
            String effectiveNamespace = (dto.getNamespace() != null && !dto.getNamespace().isEmpty()) ? dto.getNamespace() : "default";

            K8sDisallowedRepos k8sDisallowedRepos = k8sDisallowedReposMapper.dtoToModel(dto);
            k8sDisallowedRepos.setMetadata(new ObjectMetaBuilder()
                    .withName(dto.getName())
                    .build());
            k8sDisallowedRepos.setStatus(new ConstraintStatus());

            kubernetesClient.resources(K8sDisallowedRepos.class)
                    .inNamespace(effectiveNamespace)
                    .createOrReplace(k8sDisallowedRepos);

            logger.infof("K8sDisallowedRepos created/updated successfully: %s", k8sDisallowedRepos);

            return k8sDisallowedRepos;
        } catch (KubernetesClientException e) {
            logger.errorf(e, "Error processing create/update K8sDisallowedRepos message.");
            return null;
        }
    }

    public List<K8sDisallowedRepos> listK8sDisallowedRepos(String namespace) {
        try {
            List<K8sDisallowedRepos> k8sDisallowedReposList = kubernetesClient.resources(K8sDisallowedRepos.class)
                    .inNamespace(namespace)
                    .list()
                    .getItems();
            logger.infof("Received List K8sDisallowedRepos Message for Namespace: %s", namespace);
            return k8sDisallowedReposList;
        } catch (KubernetesClientException e) {
            throw new KubernetesClientException("Error listing K8sDisallowedRepos: " + e.getMessage(), e);
        }
    }

    public void deleteK8sDisallowedRepos(String name, String namespace) {
        String effectiveNamespace = (namespace != null && !namespace.isEmpty()) ? namespace : "default";
        try {
            kubernetesClient.resources(K8sDisallowedRepos.class)
                    .inNamespace(effectiveNamespace)
                    .withName(name)
                    .delete();
            logger.infof("K8sDisallowedRepos '%s' in namespace '%s' deleted successfully", name, namespace);
        } catch (KubernetesClientException e) {
            logger.errorf(e, "Error processing deleting K8sDisallowedRepos.");
            throw new RuntimeException("Error deleting K8sDisallowedRepos: " + e.getMessage(), e);
        }
    }

    public boolean k8sDisallowedReposExists(String name, String namespace) {
        String effectiveNamespace = (namespace != null && !namespace.isEmpty()) ? namespace : "default";
        try {
            return kubernetesClient.resources(K8sDisallowedRepos.class)
                    .inNamespace(effectiveNamespace)
                    .withName(name)
                    .get() != null;
        } catch (KubernetesClientException e) {
            throw new KubernetesClientException("Error checking K8sDisallowedRepos existence: " + e.getMessage(), e);
        }
    }
    @Override
    public void createOrUpdate(ConstraintDTO dto) {
        createOrUpdateK8sDisallowedRepos(dto);
    }

    @Override
    public boolean supports(ConstraintDTO dto) {
        return ConstraintType.DISALLOWED_REPOS.equals(dto.getType());
    }
}