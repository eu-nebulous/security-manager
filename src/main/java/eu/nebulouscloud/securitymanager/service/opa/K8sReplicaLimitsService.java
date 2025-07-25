package eu.nebulouscloud.securitymanager.service.opa;

import eu.nebulouscloud.securitymanager.model.opa.ConstraintType;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import eu.nebulouscloud.securitymanager.dto.ConstraintDTO;
import eu.nebulouscloud.securitymanager.dto.mapper.K8sReplicaLimitsMapper;
import eu.nebulouscloud.securitymanager.model.opa.ConstraintStatus;
import eu.nebulouscloud.securitymanager.model.opa.allowed.K8sReplicaLimits;

import java.util.List;

@ApplicationScoped
public class K8sReplicaLimitsService implements ConstraintHandler{

    private static final Logger logger = Logger.getLogger(K8sReplicaLimitsService.class);

    @Inject
    KubernetesClient kubernetesClient;

    @Inject
    K8sReplicaLimitsMapper k8sReplicaLimitsMapper;

    public K8sReplicaLimits createOrUpdateK8sReplicaLimits(ConstraintDTO dto) {
        try {
            String effectiveNamespace = (dto.getNamespace() != null && !dto.getNamespace().isEmpty()) ? dto.getNamespace() : "default";

            K8sReplicaLimits k8sReplicaLimits = k8sReplicaLimitsMapper.dtoToModel(dto);
            k8sReplicaLimits.setMetadata(new ObjectMetaBuilder()
                    .withName(dto.getName())
                    .build());
            k8sReplicaLimits.setStatus(new ConstraintStatus());

            kubernetesClient.resources(K8sReplicaLimits.class)
                    .inNamespace(effectiveNamespace)
                    .createOrReplace(k8sReplicaLimits);

            logger.infof("K8sReplicaLimits created/updated successfully: %s", k8sReplicaLimits);

            return k8sReplicaLimits;
        } catch (KubernetesClientException e) {
            logger.errorf(e, "Error processing create/update K8sReplicaLimits message.");
            return null;
        }
    }

    public List<K8sReplicaLimits> listK8sReplicaLimits(String namespace) {
        try {
            List<K8sReplicaLimits> k8sReplicaLimitsList = kubernetesClient.resources(K8sReplicaLimits.class)
                    .inNamespace(namespace)
                    .list()
                    .getItems();
            logger.infof("Received List K8sReplicaLimits Message for Namespace: %s", namespace);
            return k8sReplicaLimitsList;
        } catch (KubernetesClientException e) {
            throw new KubernetesClientException("Error listing K8sReplicaLimits: " + e.getMessage(), e);
        }
    }

    public void deleteK8sReplicaLimits(String name, String namespace) {
        String effectiveNamespace = (namespace != null && !namespace.isEmpty()) ? namespace : "default";
        try {
            kubernetesClient.resources(K8sReplicaLimits.class)
                    .inNamespace(effectiveNamespace)
                    .withName(name)
                    .delete();
            logger.infof("K8sReplicaLimits '%s' in namespace '%s' deleted successfully", name, namespace);
        } catch (KubernetesClientException e) {
            logger.errorf(e, "Error processing deleting K8sReplicaLimits.");
            throw new RuntimeException("Error deleting K8sReplicaLimits: " + e.getMessage(), e);
        }
    }

    public boolean k8sReplicaLimitsExists(String name, String namespace) {
        String effectiveNamespace = (namespace != null && !namespace.isEmpty()) ? namespace : "default";
        try {
            return kubernetesClient.resources(K8sReplicaLimits.class)
                    .inNamespace(effectiveNamespace)
                    .withName(name)
                    .get() != null;
        } catch (KubernetesClientException e) {
            throw new KubernetesClientException("Error checking K8sReplicaLimits existence: " + e.getMessage(), e);
        }
    }
    @Override
    public void createOrUpdate(ConstraintDTO dto) {
        createOrUpdateK8sReplicaLimits(dto);
    }

    @Override
    public boolean supports(ConstraintDTO dto) {
        return ConstraintType.REPLICA_LIMITS.equals(dto.getType());
    }
}