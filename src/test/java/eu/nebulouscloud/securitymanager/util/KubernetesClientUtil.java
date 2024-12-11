package eu.nebulouscloud.securitymanager.util;

import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class KubernetesClientUtil {

    private static final Logger log = LoggerFactory.getLogger(KubernetesClientUtil.class);
    public static KubernetesClient createKubernetesClient() {
        org.eclipse.microprofile.config.Config config = ConfigProvider.getConfig();

        String masterUrl = config.getValue("quarkus.kubernetes-client.master-url", String.class);
        String username = config.getValue("quarkus.kubernetes-client.username", String.class);
        String caCertData = config.getValue("quarkus.kubernetes-client.ca-cert-data", String.class);
        String clientCertData = config.getValue("quarkus.kubernetes-client.client-cert-data", String.class);
        String clientKeyData = config.getValue("quarkus.kubernetes-client.client-key-data", String.class);

//         Making use of k3s.yaml
        String kubeconfig;
        try {
            String kubeconfigPath = config.getValue("kubernetes.client.kubeconfig", String.class);

            var resourceUrl = Thread.currentThread().getContextClassLoader().getResource(kubeconfigPath);

            kubeconfig = new String(Files.readAllBytes(Paths.get(resourceUrl.toURI())), StandardCharsets.UTF_8);

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Error reading kubeconfig file", e);
        }

        Config kubernetesConfig = Config.fromKubeconfig(kubeconfig);
//        kubernetesConfig.setClientKeyAlgo("EC");
//        kubernetesConfig.setMasterUrl("https://10.10.4.37:6443");

//        Config kubernetesConfig = new ConfigBuilder(Config.empty())
//                .withMasterUrl(masterUrl)
//                .withUsername(username)
//                .withCaCertData(caCertData)
//                .withClientCertData(clientCertData)
//                .withClientKeyData(clientKeyData)
//                .withClientKeyAlgo("EC")
//                .build();

        DefaultKubernetesClient client = new DefaultKubernetesClient(kubernetesConfig);

        NamespaceList myNs = client.namespaces().list();

        List<Node> nodes = client.nodes().list().getItems();

            return client;
    }
}
