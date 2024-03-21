package ubi.nebulous.service;

import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import ubi.nebulous.dto.CasbinModelDTO;
import ubi.nebulous.model.casbin.model.CasbinModel;
import ubi.nebulous.model.casbin.model.CasbinModelSpec;
import ubi.nebulous.model.casbin.model.CasbinModelStatus;

import java.util.List;

@ApplicationScoped
public class CasbinModelService {

    private static final Logger logger = Logger.getLogger(CasbinModelService.class);

    @Inject
    KubernetesClient kubernetesClient;

    public CasbinModel createOrUpdateCasbinModel(CasbinModelDTO dto) {
        try {
            String effectiveNamespace = (dto.getNamespace() != null && !dto.getNamespace().isEmpty()) ? dto.getNamespace() : "default";

            CasbinModel casbinModel = new CasbinModel();
            casbinModel.setMetadata(new ObjectMetaBuilder()
                    .withName(dto.getName())
                    .build());
            CasbinModelSpec spec = new CasbinModelSpec();
            spec.setEnabled(dto.isEnabled());
            spec.setModelText(dto.getModelText());

            casbinModel.setSpec(spec);
            casbinModel.setStatus(new CasbinModelStatus());

            kubernetesClient.resources(CasbinModel.class)
                    .inNamespace(effectiveNamespace)
                    .createOrReplace(casbinModel);

            logger.infof("CasbinModel created/updated successfully: %s", casbinModel);

            return casbinModel;
        }catch (KubernetesClientException e){
            logger.errorf(e, "Error processing create/update CasbinModel message.");
            return null;
        }
    }


    public List<CasbinModel> listCasbinModels(String namespace) {
        try {
            List<CasbinModel> casbinModelLists = kubernetesClient.resources(CasbinModel.class)
                    .inNamespace(namespace)
                    .list()
                    .getItems();
            logger.infof("Received List CasbinModels Message for Namespace: %s", namespace);
            return casbinModelLists;
        } catch (KubernetesClientException e) {
            throw new KubernetesClientException("Error listing CasbinModels: " + e.getMessage(), e);
        }
    }


    public void deleteCasbinModel(String name, String namespace) {
        String effectiveNamespace = (namespace != null && !namespace.isEmpty()) ? namespace : "default";
        try {
            kubernetesClient.resources(CasbinModel.class)
                    .inNamespace(effectiveNamespace)
                    .withName(name)
                    .delete();
            logger.infof("CasbinModel '%s' in namespace '%s' deleted successfully", name, namespace);
        } catch (KubernetesClientException e) {
            logger.errorf(e, "Error processing deleting CasbinModel.");
            throw new RuntimeException("Error deleting CasbinModel: " + e.getMessage(), e);
        }
    }




    public boolean casbinModelExists(String name, String namespace) {
        String effectiveNamespace = (namespace != null && !namespace.isEmpty()) ? namespace : "default";
        try {
            return kubernetesClient.resources(CasbinModel.class)
                    .inNamespace(effectiveNamespace)
                    .withName(name)
                    .get() != null;
        } catch (KubernetesClientException e) {
            throw new KubernetesClientException("Error checking CasbinModel existence: " + e.getMessage(), e);
        }
    }
}
