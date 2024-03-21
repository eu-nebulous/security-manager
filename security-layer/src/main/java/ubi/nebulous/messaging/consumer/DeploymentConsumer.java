package ubi.nebulous.messaging.consumer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.jms.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.jboss.logging.Logger;
import ubi.nebulous.messaging.util.JsonUtil;
import ubi.nebulous.dto.DeploymentDTO;
import ubi.nebulous.service.DeploymentService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class DeploymentConsumer implements Runnable {

    private static final Logger logger = Logger.getLogger(DeploymentConsumer.class);

    @Inject
    ConnectionFactory connectionFactory;

    @Inject
    DeploymentService k8sDeploymentService;

    @ConfigProperty(name = "DEPLOYMENT_CREATE_TOPIC")
    String deploymentCreateTopic;

    @ConfigProperty(name = "DEPLOYMENT_DELETE_TOPIC")
    String deploymentDeleteTopic;

    @ConfigProperty(name = "DEPLOYMENT_GET_ALL_TOPIC")
    String deploymentGetAllTopic;

    @ConfigProperty(name = "RESPONSE_TOPIC")
    String responseTopicName;

    private final ExecutorService scheduler = Executors.newSingleThreadExecutor();

    void onStart(@Observes StartupEvent ev) {
        logger.info("Starting Deployment Consumer...");
        scheduler.submit(this);
    }

    void onStop(@Observes ShutdownEvent ev) {
        logger.info("Stopping Deployment Consumer...");
        scheduler.shutdown();
    }

    @Override
    public void run() {
        try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            var createConsumer = context.createConsumer(context.createTopic(deploymentCreateTopic));
            var deleteConsumer = context.createConsumer(context.createTopic(deploymentDeleteTopic));
            var getAllConsumer = context.createConsumer(context.createTopic(deploymentGetAllTopic));

            while (!Thread.currentThread().isInterrupted()) {
                processMessages(createConsumer, deleteConsumer, getAllConsumer, context);
            }
        }
    }

    private void processMessages(JMSConsumer createConsumer, JMSConsumer deleteConsumer, JMSConsumer getAllConsumer, JMSContext context) {
        var createMessage = createConsumer.receiveNoWait();
        if (createMessage != null) {
            try {
                processCreateDeployment(createMessage.getBody(String.class), context);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }

        var deleteMessage = deleteConsumer.receiveNoWait();
        if (deleteMessage != null) {
            try {
                processDeleteDeployment(deleteMessage.getBody(String.class), context);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }

        var getAllMessage = getAllConsumer.receiveNoWait();
        if (getAllMessage != null) {
            try {
                processGetAllDeployments(getAllMessage.getBody(String.class), context);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void processCreateDeployment(String jsonMessage, JMSContext context) {
        try {
            DeploymentDTO deploymentDTO = JsonUtil.deserialize(jsonMessage, DeploymentDTO.class);
            logger.infof("Received Create Deployment Message: %s", deploymentDTO);
            k8sDeploymentService.createDeployment(deploymentDTO);
            sendResponse(context, "Deployment created successfully");
        } catch (Exception e) {
            sendResponse(context, "Error creating deployment: " + e.getMessage());
        }
    }

    private void processDeleteDeployment(String jsonMessage, JMSContext context) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonMessage).getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            String namespace = jsonObject.has("namespace") ? jsonObject.get("namespace").getAsString() : "default";

            if (k8sDeploymentService.deploymentExists(name, namespace)) {
                k8sDeploymentService.deleteDeployment(name, namespace);
                sendResponse(context, String.format("Deployment '%s' in namespace '%s' deleted successfully", name, namespace));
            } else {
                logger.infof("Deployment '%s' in namespace '%s' not found", name, namespace);
                sendResponse(context, String.format("Deployment '%s' in namespace '%s' not found", name, namespace));
            }
        } catch (Exception e) {
            sendResponse(context, "Error deleting deployment: " + e.getMessage());
        }
    }

    private void processGetAllDeployments(String namespace, JMSContext context) {
        try {
            List<Deployment> deployments = k8sDeploymentService.getDeployments(namespace != null ? namespace : "default");
            logger.infof("Received Get All Deployments Message for Namespace: %s", namespace);
            // send back list of deployments
            logger.infof(JsonUtil.serialize(deployments));
            sendResponse(context, JsonUtil.serialize(deployments));
        } catch (Exception e) {
            sendResponse(context, "Error retrieving deployments: " + e.getMessage());
        }
    }

    private void sendResponse(JMSContext context, String message) {
        TextMessage responseMessage = context.createTextMessage(message);
        context.createProducer().send(context.createTopic(responseTopicName), responseMessage);
    }
}
