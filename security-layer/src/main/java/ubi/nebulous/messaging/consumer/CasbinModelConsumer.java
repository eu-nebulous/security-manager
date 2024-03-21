package ubi.nebulous.messaging.consumer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.jms.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import ubi.nebulous.dto.CasbinModelDTO;
import ubi.nebulous.dto.CasbinPolicyDTO;
import ubi.nebulous.messaging.util.JsonUtil;
import ubi.nebulous.model.casbin.model.CasbinModel;
import ubi.nebulous.service.CasbinModelService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class CasbinModelConsumer implements Runnable {

    private static final Logger logger = Logger.getLogger(CasbinModelConsumer.class);

    @Inject
    ConnectionFactory connectionFactory;

    @Inject
    CasbinModelService casbinModelService;

    @ConfigProperty(name = "CASBIN_MODEL_CREATE_TOPIC")
    String casbinModelCreateTopic;

    @ConfigProperty(name = "CASBIN_MODEL_DELETE_TOPIC")
    String casbinModelDeleteTopic;

    @ConfigProperty(name = "CASBIN_MODEL_GET_ALL_TOPIC")
    String casbinModelGetAllTopic;

    @ConfigProperty(name = "RESPONSE_TOPIC")
    String responseTopicName;

    private final ExecutorService scheduler = Executors.newSingleThreadExecutor();

    void onStart(@Observes StartupEvent ev) {
        logger.info("Starting CasbinModel Consumer...");
        scheduler.submit(this);
    }

    void onStop(@Observes ShutdownEvent ev) {
        logger.info("Stopping CasbinModel Consumer...");
        scheduler.shutdown();
    }
    @Override
    public void run() {
        try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            var createConsumer = context.createConsumer(context.createTopic(casbinModelCreateTopic));
            var deleteConsumer = context.createConsumer(context.createTopic(casbinModelDeleteTopic));
            var getAllConsumer = context.createConsumer(context.createTopic(casbinModelGetAllTopic));

            while (!Thread.currentThread().isInterrupted()) {
                processMessages(createConsumer, deleteConsumer, getAllConsumer, context);
            }
        } catch (RuntimeException e) {
            logger.errorf(e, "Error in CasbinModel Consumer.");
        }
    }
    private void processMessages(JMSConsumer createConsumer, JMSConsumer deleteConsumer, JMSConsumer getAllConsumer, JMSContext context) {
        try {
            var createMessage = createConsumer.receiveNoWait();
            if (createMessage != null) {
                processCreateOrUpdateCasbinModel(createMessage.getBody(List.class), context);
            }

            var deleteMessage = deleteConsumer.receiveNoWait();
            if (deleteMessage != null) {
                processDeleteCasbinModel(deleteMessage.getBody(String.class), context);
            }

            var getAllMessage = getAllConsumer.receiveNoWait();
            if (getAllMessage != null) {
                processListCasbinModels(getAllMessage.getBody(String.class), context);
            }
        } catch (JMSException e) {
            logger.errorf(e, "Error processing messages in CasbinModel Consumer.");
        }
    }

    private void processCreateOrUpdateCasbinModel(List<Map<String, String>> messageList, JMSContext context) {
        try {
            Map<String, String> modelMap = messageList.get(0);
            CasbinModelDTO  casbinModelDTO = new CasbinModelDTO();
            casbinModelDTO.setName(modelMap.get("name") != null ? modelMap.get("name") : null);
            casbinModelDTO.setModelText(modelMap.get("model") != null ? modelMap.get("model") : null);
            casbinModelDTO.setNamespace(modelMap.get("namespace") != null ? modelMap.get("namespace") : null);


//            CasbinModelDTO casbinModelDTO = JsonUtil.deserialize(jsonMessage, CasbinModelDTO.class);
            logger.infof("message received for "+ casbinModelDTO.toString());
            CasbinModel model = casbinModelService.createOrUpdateCasbinModel(casbinModelDTO);
            sendResponse(context, "CasbinModel created/updated successfully");
        } catch (Exception e) {
            sendResponse(context, "Error creating/updating CasbinModel: " + e.getMessage());
        }
    }

    private void processDeleteCasbinModel(String jsonMessage, JMSContext context) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonMessage).getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            String namespace = jsonObject.has("namespace") ? jsonObject.get("namespace").getAsString() : "default";

            if (casbinModelService.casbinModelExists(name, namespace)) {
                casbinModelService.deleteCasbinModel(name, namespace);
                sendResponse(context, String.format("CasbinModel '%s' in namespace '%s' deleted successfully", name, namespace));
            } else {
                sendResponse(context, String.format("CasbinModel '%s' in namespace '%s' not found", name, namespace));
            }
        } catch (Exception e) {
            logger.errorf(e, "Error processing delete CasbinModel message.");
            sendResponse(context, "Error deleting CasbinModel: " + e.getMessage());
        }
    }

    private void processListCasbinModels(String namespace, JMSContext context) {
        try {
            List<CasbinModel> models = casbinModelService.listCasbinModels(namespace != null ? namespace : "default");
            sendResponse(context, JsonUtil.serialize(models));
            logger.infof(JsonUtil.serialize(models));
        } catch (Exception e) {
            logger.errorf(e, "Error processing list CasbinModels message.");
            sendResponse(context, "Error retrieving CasbinModels: " + e.getMessage());
        }
    }

    private void sendResponse(JMSContext context, String message) {
        try {
            TextMessage responseMessage = context.createTextMessage(message);
            context.createProducer().send(context.createTopic(responseTopicName), responseMessage);
        } catch (RuntimeException e) {
            logger.errorf(e, "Error sending response message.");
        }
    }
}