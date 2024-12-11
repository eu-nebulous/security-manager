package eu.nebulouscloud.securitymanager.service;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Custom Kubernetes Client Class forming SSH connection
 *
 **/
@Deprecated
@ApplicationScoped
public class KubernetesClientService {

    private static final Logger logger = Logger.getLogger(KubernetesClientService.class);

    @Inject
    SshConnectionService sshConnectionService;

    @ConfigProperty(name = "kubernetes.client.kubeconfig")
    String kubeconfigPath;

    private KubernetesClient kubernetesClient;

    public KubernetesClient getKubernetesClient() {
        // Ensure SSH connection is established
        if (kubernetesClient == null) {
            sshConnectionService.connect();
            initializeKubernetesClient();
        }
        return kubernetesClient;
    }

    private void initializeKubernetesClient() {
        if (kubernetesClient == null) {
            try {
                String result = sshConnectionService.executeCommand("kubectl get nodes -o wide");
                String kubeconfig = loadKubeconfig();

                Config kubernetesConfig = Config.fromKubeconfig(kubeconfig);
//

                kubernetesClient = new KubernetesClientBuilder().withConfig(kubernetesConfig).build();
                logger.infof("Kubernetes client initialized with custom kubeconfig.");

            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException("Error reading kubeconfig file", e);
            }
        }
    }

    private String loadKubeconfig() throws IOException, URISyntaxException {
        var resourceUrl = Thread.currentThread().getContextClassLoader().getResource(kubeconfigPath);
        if (resourceUrl == null) {
            throw new IOException("Kubeconfig file not found in the classpath: " + kubeconfigPath);
        }
        return new String(Files.readAllBytes(Paths.get(resourceUrl.toURI())), StandardCharsets.UTF_8);
    }

    public void cleanup() {
        // Clean up Kubernetes client and close SSH connection
        if (kubernetesClient != null) {
            kubernetesClient.close();
            logger.infof("Kubernetes client closed.");
            kubernetesClient = null;
        }
        sshConnectionService.disconnect();
    }
}
