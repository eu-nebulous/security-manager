/*
 * Copyright (c) 2024     Ubitech LTD.
 */

package eu.nebulouscloud.securitymanager;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class KubernetesConnectionManager {
    private static final Logger LOG = Logger.getLogger(KubernetesConnectionManager.class);

    @ConfigProperty(name = "kubernetes.client.kubeconfig")
    String kubeconfigPath;

    private KubernetesClient kubernetesClient;

    void onStart(@Observes StartupEvent ev) {
        LOG.info("Application is starting...");
        try {
            String kubeconfig = loadKubeconfig();
            Config config = Config.fromKubeconfig(kubeconfig);
            kubernetesClient = new DefaultKubernetesClient(config);

            String version = kubernetesClient.getVersion().toString();
            LOG.info("Connected to Kubernetes cluster, version: " + version);
        } catch (Exception e) {
            LOG.error("Failed to connect to Kubernetes cluster using kubeconfig", e);
        }
    }

    /**
     * Loads kubeconfig as String from file
     * @return kubeconfig as String
     * @throws IOException
     * @throws URISyntaxException
     */
    private String loadKubeconfig() throws IOException{
        if (!Files.exists(Path.of(kubeconfigPath))) {
            throw new IOException("Kubeconfig file not found at path: " + kubeconfigPath);
        }
        return new String(Files.readAllBytes(Path.of(kubeconfigPath)), StandardCharsets.UTF_8);
    }

    /**
     * Produce the KubernetesClient as a CDI bean so it can be injected elsewhere
     */
    @Produces
    @ApplicationScoped
    public KubernetesClient kubernetesClientProducer() {
        return kubernetesClient;
    }
}
