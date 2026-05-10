package com.learning.lab.module30;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 30: Kubernetes Deployment ===");

        podDemo();
        deploymentDemo();
        serviceDemo();
        configMapSecretDemo();
        ingressDemo();
        helmDemo();
    }

    static void podDemo() {
        System.out.println("\n--- Kubernetes Pod ---");
        String pod = "apiVersion: v1\n" +
                "kind: Pod\n" +
                "metadata:\n" +
                "  name: myapp-pod\n" +
                "  labels:\n" +
                "    app: myapp\n" +
                "spec:\n" +
                "  containers:\n" +
                "  - name: myapp\n" +
                "    image: myapp:1.0\n" +
                "    ports:\n" +
                "    - containerPort: 8080\n" +
                "    resources:\n" +
                "      limits:\n" +
                "        memory: \"512Mi\"\n" +
                "        cpu: \"500m\"";
        System.out.println(pod);
    }

    static void deploymentDemo() {
        System.out.println("\n--- Kubernetes Deployment ---");
        String deployment = "apiVersion: apps/v1\n" +
                "kind: Deployment\n" +
                "metadata:\n" +
                "  name: myapp-deployment\n" +
                "spec:\n" +
                "  replicas: 3\n" +
                "  selector:\n" +
                "    matchLabels:\n" +
                "      app: myapp\n" +
                "  template:\n" +
                "    metadata:\n" +
                "      labels:\n" +
                "        app: myapp\n" +
                "    spec:\n" +
                "      containers:\n" +
                "      - name: myapp\n" +
                "        image: myapp:1.0\n" +
                "        readinessProbe:\n" +
                "          httpGet:\n" +
                "            path: /health\n" +
                "            port: 8080\n" +
                "        livenessProbe:\n" +
                "          httpGet:\n" +
                "            path: /health\n" +
                "            port: 8080\n" +
                "          initialDelaySeconds: 30\n" +
                "          periodSeconds: 10";
        System.out.println(deployment);
    }

    static void serviceDemo() {
        System.out.println("\n--- Kubernetes Service ---");
        String service = "apiVersion: v1\n" +
                "kind: Service\n" +
                "metadata:\n" +
                "  name: myapp-service\n" +
                "spec:\n" +
                "  type: ClusterIP\n" +
                "  selector:\n" +
                "    app: myapp\n" +
                "  ports:\n" +
                "  - protocol: TCP\n" +
                "    port: 80\n" +
                "    targetPort: 8080\n" +
                "---\n" +
                "apiVersion: v1\n" +
                "kind: Service\n" +
                "metadata:\n" +
                "  name: myapp-lb\n" +
                "spec:\n" +
                "  type: LoadBalancer\n" +
                "  selector:\n" +
                "    app: myapp\n" +
                "  ports:\n" +
                "  - port: 80";
        System.out.println(service);
    }

    static void configMapSecretDemo() {
        System.out.println("\n--- ConfigMap and Secret ---");
        String configmap = "apiVersion: v1\n" +
                "kind: ConfigMap\n" +
                "metadata:\n" +
                "  name: myapp-config\n" +
                "data:\n" +
                "  database-url: \"postgres://db:5432/app\"\n" +
                "  app-env: \"production\"\n" +
                "---\n" +
                "apiVersion: v1\n" +
                "kind: Secret\n" +
                "metadata:\n" +
                "  name: myapp-secret\n" +
                "type: Opaque\n" +
                "data:\n" +
                "  username: YWRtaW4=\n" +
                "  password: cGFzc3dvcmQ=";
        System.out.println(configmap);
    }

    static void ingressDemo() {
        System.out.println("\n--- Kubernetes Ingress ---");
        String ingress = "apiVersion: networking.k8s.io/v1\n" +
                "kind: Ingress\n" +
                "metadata:\n" +
                "  name: myapp-ingress\n" +
                "spec:\n" +
                "  rules:\n" +
                "  - host: myapp.example.com\n" +
                "    http:\n" +
                "      paths:\n" +
                "      - path: /\n" +
                "        pathType: Prefix\n" +
                "        backend:\n" +
                "          service:\n" +
                "            name: myapp-service\n" +
                "            port:\n" +
                "              number: 80";
        System.out.println(ingress);
    }

    static void helmDemo() {
        System.out.println("\n--- Helm Charts ---");
        System.out.println("Install: helm install myrelease ./chart");
        System.out.println("Upgrade: helm upgrade myrelease ./chart");
        System.out.println("Rollback: helm rollback myrelease 1");
        System.out.println("Template: helm template ./chart --set image.tag=v2.0");
        System.out.println("Values: helm install -f values.yaml myrelease ./chart");
    }
}