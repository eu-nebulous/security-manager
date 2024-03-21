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
import ubi.nebulous.model.casbin.policy.CasbinPolicy;
import ubi.nebulous.service.CasbinPolicyService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class CasbinPolicyConsumer implements Runnable {

    private static final Logger logger = Logger.getLogger(CasbinPolicyConsumer.class);

    @Inject
    ConnectionFactory connectionFactory;

    @Inject
    CasbinPolicyService casbinPolicyService;

    @ConfigProperty(name = "CASBIN_POLICY_CREATE_TOPIC")
    String casbinPolicyCreateTopic;

    @ConfigProperty(name = "CASBIN_POLICY_DELETE_TOPIC")
    String casbinPolicyDeleteTopic;

    @ConfigProperty(name = "CASBIN_POLICY_GET_ALL_TOPIC")
    String casbinPolicyGetAllTopic;

    @ConfigProperty(name = "RESPONSE_TOPIC")
    String responseTopicName;

    private final ExecutorService scheduler = Executors.newSingleThreadExecutor();

    void onStart(@Observes StartupEvent ev) {
        logger.info("Starting CasbinPolicy Consumer...");
        scheduler.submit(this);
    }

    void onStop(@Observes ShutdownEvent ev) {
        logger.info("Stopping CasbinPolicy Consumer...");
        scheduler.shutdown();
    }

    @Override
    public void run() {
        try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            var createConsumer = context.createConsumer(context.createTopic(casbinPolicyCreateTopic));
            var deleteConsumer = context.createConsumer(context.createTopic(casbinPolicyDeleteTopic));
            var getAllConsumer = context.createConsumer(context.createTopic(casbinPolicyGetAllTopic));

            while (!Thread.currentThread().isInterrupted()) {
                processMessages(createConsumer, deleteConsumer, getAllConsumer, context);
            }
        } catch (RuntimeException e) {
            logger.errorf(e, "Error in CasbinPolicy Consumer.");
        }
    }

    private void processMessages(JMSConsumer createConsumer, JMSConsumer deleteConsumer, JMSConsumer getAllConsumer, JMSContext context) {
        try {
            var createMessage = createConsumer.receiveNoWait();
            if (createMessage != null) {
//                processCreateOrUpdateCasbinPolicy(createMessage.getBody(String.class), context);
                processCreateOrUpdateCasbinPolicy(createMessage.getBody(List.class), context);
            }

            var deleteMessage = deleteConsumer.receiveNoWait();
            if (deleteMessage != null) {
                processDeleteCasbinPolicy(deleteMessage.getBody(String.class), context);
            }

            var getAllMessage = getAllConsumer.receiveNoWait();
            if (getAllMessage != null) {
                processListCasbinPolicies(getAllMessage.getBody(String.class), context);
            }
        } catch (JMSException e) {
            logger.errorf(e, "Error processing messages in CasbinPolicy Consumer.");
        }
    }

    private void processCreateOrUpdateCasbinPolicy(List<Map<String, String>> messageList, JMSContext context) {
        try {
            Map<String, String> policyMap = messageList.get(0);
            CasbinPolicyDTO casbinPolicyDTO = new CasbinPolicyDTO();
            casbinPolicyDTO.setName(policyMap.get("name") != null ? policyMap.get("name") : null);
            casbinPolicyDTO.setPolicyItem(policyMap.get("policyItem") != null ? policyMap.get("policyItem") : null);
            casbinPolicyDTO.setNamespace(policyMap.get("namespace") != null ? policyMap.get("namespace") : null);

//            CasbinPolicyDTO dto = JsonUtil.deserialize(jsonMessage, CasbinPolicyDTO.class);
            logger.infof("message received for "+ casbinPolicyDTO.toString());
            CasbinPolicy policy = casbinPolicyService.createOrUpdateCasbinPolicy(casbinPolicyDTO);
            sendResponse(context, "CasbinPolicy created/updated successfully");
        } catch (Exception e) {
            sendResponse(context, "Error creating/updating CasbinPolicy: " + e.getMessage());
        }
    }

    private void processDeleteCasbinPolicy(String jsonMessage, JMSContext context) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonMessage).getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            String namespace = jsonObject.has("namespace") ? jsonObject.get("namespace").getAsString() : "default";

            if (casbinPolicyService.casbinPolicyExists(name, namespace)) {
                casbinPolicyService.deleteCasbinPolicy(name, namespace);
                sendResponse(context, String.format("CasbinPolicy '%s' deleted successfully", name));
            } else {
                sendResponse(context, String.format("CasbinPolicy '%s' not found", name));
                logger.infof("CasbinPolicy '%s' not found", name);
            }
        } catch (Exception e) {
            sendResponse(context, "Error deleting CasbinPolicy: " + e.getMessage());
        }
    }

    private void processListCasbinPolicies(String namespace, JMSContext context) {
        try {
            List<CasbinPolicy> policies = casbinPolicyService.listCasbinPolicies(namespace);
            sendResponse(context, JsonUtil.serialize(policies));
            logger.infof("Listed CasbinPolicies for namespace: %s", namespace);
        } catch (Exception e) {
            sendResponse(context, "Error listing CasbinPolicies: " + e.getMessage());
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