package ubi.nebulous.service;

import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import ubi.nebulous.dto.CasbinPolicyDTO;
import ubi.nebulous.model.casbin.policy.CasbinPolicy;
import ubi.nebulous.model.casbin.policy.CasbinPolicySpec;

import java.util.List;

@ApplicationScoped
public class CasbinPolicyService {

    private static final Logger logger = Logger.getLogger(CasbinPolicyService.class);

    @Inject
    KubernetesClient kubernetesClient;

    public CasbinPolicy createOrUpdateCasbinPolicy(CasbinPolicyDTO dto) {
        String effectiveNamespace = (dto.getNamespace() != null && !dto.getNamespace().isEmpty()) ? dto.getNamespace() : "default";
        try {
            CasbinPolicy casbinPolicy = new CasbinPolicy();
            casbinPolicy.setMetadata(new ObjectMetaBuilder().withName(dto.getName()).build());
            CasbinPolicySpec spec = new CasbinPolicySpec();
            spec.setPolicyItem(dto.getPolicyItem());
            casbinPolicy.setSpec(spec);

            kubernetesClient.resources(CasbinPolicy.class)
                    .inNamespace(effectiveNamespace)
                    .createOrReplace(casbinPolicy);
            logger.infof("CasbinPolicy created/updated successfully: %s", casbinPolicy);
            return casbinPolicy;
        } catch (KubernetesClientException e) {
            logger.errorf(e, "Error creating/updating CasbinPolicy.");
            throw new RuntimeException("Error creating/updating CasbinPolicy: " + e.getMessage(), e);
        }
    }



    public List<CasbinPolicy> listCasbinPolicies(String namespace) {
        try {
            List<CasbinPolicy> casbinPolicyList = kubernetesClient.resources(CasbinPolicy.class)
                    .inNamespace(namespace)
                    .list()
                    .getItems();
            logger.infof("Listed CasbinPolicies for namespace: %s", namespace);
            return casbinPolicyList;
        } catch (KubernetesClientException e) {
            logger.errorf(e, "Error listing CasbinPolicies.");
            throw new RuntimeException("Error listing CasbinPolicies: " + e.getMessage(), e);
        }
    }

    public void deleteCasbinPolicy(String name, String namespace) {
        String effectiveNamespace = (namespace != null && !namespace.isEmpty()) ? namespace : "default";
        try {
            kubernetesClient.resources(CasbinPolicy.class)
                    .inNamespace(effectiveNamespace)
                    .withName(name)
                    .delete();
            logger.infof("CasbinPolicy '%s' deleted successfully", name);
        } catch (KubernetesClientException e) {
            logger.errorf(e, "Error deleting CasbinPolicy.");
            throw new RuntimeException("Error deleting CasbinPolicy: " + e.getMessage(), e);
        }
    }


    public boolean casbinPolicyExists(String name, String namespace) {
        String effectiveNamespace = (namespace != null && !namespace.isEmpty()) ? namespace : "default";
        try {
            return kubernetesClient.resources(CasbinPolicy.class)
                    .inNamespace(effectiveNamespace)
                    .withName(name)
                    .get() != null;
        } catch (KubernetesClientException e) {
            throw new RuntimeException("Error checking CasbinPolicy existence: " + e.getMessage(), e);
        }
    }
}