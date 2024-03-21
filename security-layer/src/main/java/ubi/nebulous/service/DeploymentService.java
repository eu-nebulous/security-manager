package ubi.nebulous.service;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import ubi.nebulous.dto.DeploymentDTO;
import ubi.nebulous.dto.mapper.DeploymentMapper;
import ubi.nebulous.messaging.consumer.DeploymentConsumer;
import ubi.nebulous.model.DeploymentModel;

import java.util.List;

@ApplicationScoped
public class DeploymentService {

    private static final Logger logger = Logger.getLogger(DeploymentService.class);

    @Inject
    KubernetesClient kubernetesClient;

    public Deployment createDeployment(DeploymentDTO deploymentDTO){
        DeploymentModel deploymentModel = DeploymentMapper.INSTANCE.dtoToModel(deploymentDTO);

        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                .withName(deploymentDTO.getName())
                .endMetadata()
                .withNewSpec()
                .withReplicas(deploymentDTO.getReplicas())
                .withNewSelector()
                .addToMatchLabels("app", deploymentDTO.getName())
                .endSelector()
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels("app", deploymentDTO.getName())
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName(deploymentDTO.getName())
                .withImage(deploymentDTO.getImage())
                .addNewPort()
                .withContainerPort(deploymentDTO.getContainerPort())
                .endPort()
                .endContainer()
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();
        try {
            deployment = kubernetesClient.apps().deployments()
                    .inNamespace(deploymentModel.getNamespace())
                    .createOrReplace(deployment);
            logger.infof("Deployment created successfully");
        } catch (KubernetesClientException e) {
            logger.errorf(e, "Error processing Create Deployment message.");
            e.printStackTrace();
        }
        return deployment;
    }

    public void deleteDeployment(String deploymentName, String namespace) {
        try {
            String effectiveNamespace = (namespace != null && !namespace.isEmpty()) ? namespace : "default";
            kubernetesClient.apps().deployments().inNamespace(effectiveNamespace)
                    .withName(deploymentName).delete();
            logger.infof("Deployment '%s' in namespace '%s' deleted successfully", deploymentName, namespace);

        }catch (RuntimeException e){
            logger.errorf(e, "Error processing Delete Deployment message.");
        }

    }

    public boolean deploymentExists(String deploymentName, String namespace) {
        String effectiveNamespace = (namespace != null && !namespace.isEmpty()) ? namespace : "default";
        return kubernetesClient.apps().deployments().inNamespace(effectiveNamespace)
                .withName(deploymentName).get() != null;
    }

    public List<Deployment> getDeployments(String namespace) {
        try {
            return kubernetesClient.apps().deployments().inNamespace(namespace).list().getItems();
        }catch (RuntimeException e){
            logger.errorf(e, "Error processing Get All Deployments message.");
            return null;
        }
    }
}
