/*
 * Copyright (c) 2025     Ubitech LTD.
 */

package eu.nebulouscloud.securitymanager.service.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.nebulouscloud.exn.Connector;
import eu.nebulouscloud.exn.core.Consumer;
import eu.nebulouscloud.exn.core.Context;
import eu.nebulouscloud.exn.core.Handler;
import eu.nebulouscloud.exn.handlers.ConnectorHandler;
import eu.nebulouscloud.exn.settings.StaticExnConfig;
import eu.nebulouscloud.securitymanager.dto.ConstraintGroupDTO;
import eu.nebulouscloud.securitymanager.service.opa.ConstraintApplierService;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.qpid.protonj2.client.Message;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.*;

@Startup
@ApplicationScoped
public class ConstraintMessageListener {

    private static final Logger LOGGER = Logger.getLogger(ConstraintMessageListener.class);

    @Inject
    ConstraintApplierService constraintApplierService;

    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "deployment.create.topic")
    String topic;

    @ConfigProperty(name = "quarkus.qpid-jms.address")
    String brokerHost;

    @ConfigProperty(name = "quarkus.qpid-jms.port")
    int brokerPort;

    @ConfigProperty(name = "quarkus.qpid-jms.username")
    String brokerUser;

    @ConfigProperty(name = "quarkus.qpid-jms.password")
    String brokerPassword;

    private Connector connector;

    @PostConstruct
    void init() {
        LOGGER.infof("Initializing ConstraintMessageListener on topic: %s", topic);

        Consumer consumer = new Consumer(
                "constraint-listener",
                topic,
                new AmqpHandler(),
                true,
                true
        );

        connector = new Connector(
                "quarkus-constraint-connector",
                new ConnectorReadyHandler(),
                List.of(),
                List.of(consumer),
                true,
                true,
                new StaticExnConfig(brokerHost, brokerPort, brokerUser, brokerPassword)
        );

        connector.start();
    }

    @PreDestroy
    void shutdown() {
        if (connector != null) {
            LOGGER.info("Shutting down ConstraintMessageListener connector.");
            connector.stop();
        }
    }

    private class AmqpHandler extends Handler {
        @Override
        public void onMessage(String key, String address, Map body, Message message, Context context) {
            LOGGER.infof("Message body: %s", body);
            try {
                String json = objectMapper.writeValueAsString(body);
                ConstraintGroupDTO dto = objectMapper.readValue(json, ConstraintGroupDTO.class);
                constraintApplierService.applyGroup(dto);
                LOGGER.infof("Applied constraint group from message on %s", address);
            } catch (Exception e) {
                LOGGER.error("Failed to process constraint message", e);
            }
        }
    }

    private static class ConnectorReadyHandler extends ConnectorHandler {
        @Override
        public void onReady(Context context) {
            LOGGER.info("AMQP connector ready and listening for constraint messages.");
        }
    }
}