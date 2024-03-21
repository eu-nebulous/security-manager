# Security Manager

If not already installed, set up k8s-gatekeeper in your Kubernetes cluster. Follow the instructions provided in the [k8s-gatekeeper repository](https://github.com/npapageorgopoulos12/k8s-gatekeeper).

### Prerequisites

**Kubeconfig:** File used by Kubernetes clients

**Access to Kubernetes Cluster:** Ensure you have access to the Kubernetes cluster and the necessary permissions to interact with the resources your application will manage

### Configuring Environment Variables

Based on kubeconfig file, set the following environment variables:

1. KUBERNETES_CLIENT_MASTER_URL: The URL of the Kubernetes API server.
2. KUBERNETES_CLIENT_USERNAME: The username for Kubernetes cluster authentication
3. KUBERNETES_CLIENT_CA_CERT_DATA: The CA certificate data for the Kubernetes cluster.
4. KUBERNETES_CLIENT_CLIENT_CERT_DATA: The client certificate data for authentication
5. KUBERNETES_CLIENT_CLIENT_KEY_DATA: The client key data for authentication
6. KUBERNETES_CLIENT_NAMESPACE: The default namespace for your Kubernetes operations



### Accessing The Client
The application and DevUI are on _port 8080_