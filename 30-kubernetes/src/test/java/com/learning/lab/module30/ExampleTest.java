package com.learning.lab.module30;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ExampleTest {

    @Test
    @DisplayName("Kubernetes pod can be defined")
    void testPodDefinition() {
        K8sPod pod = new K8sPod("web-pod");
        pod.setContainer("nginx", "nginx:latest");
        assertEquals("web-pod", pod.getName());
    }

    @Test
    @DisplayName("Kubernetes deployment can manage replicas")
    void testDeployment() {
        K8sDeployment deployment = new K8sDeployment("app-deployment");
        deployment.setReplicas(3);
        deployment.setImage("myapp:v1.0");
        assertEquals(3, deployment.getReplicas());
    }

    @Test
    @DisplayName("Kubernetes service can expose pods")
    void testService() {
        K8sService service = new K8sService("web-service");
        service.setType("LoadBalancer");
        service.setPort(80);
        assertEquals("LoadBalancer", service.getType());
    }

    @Test
    @DisplayName("Kubernetes configmap can store configuration")
    void testConfigMap() {
        K8sConfigMap configMap = new K8sConfigMap("app-config");
        configMap.addData("database.url", "postgres://db:5432");
        configMap.addData("app.mode", "production");
        assertEquals("postgres://db:5432", configMap.getData("database.url"));
    }

    @Test
    @DisplayName("Kubernetes secret can store sensitive data")
    void testSecret() {
        K8sSecret secret = new K8sSecret("db-credentials");
        secret.addData("username", "admin");
        secret.addData("password", "secret123");
        assertEquals("admin", secret.getData("username"));
    }

    @Test
    @DisplayName("Kubernetes namespace can isolate resources")
    void testNamespace() {
        K8sNamespace namespace = new K8sNamespace("production");
        assertEquals("production", namespace.getName());
    }

    @Test
    @DisplayName("Kubernetes ingress can route traffic")
    void testIngress() {
        K8sIngress ingress = new K8sIngress("web-ingress");
        ingress.setHost("example.com");
        ingress.setBackendService("web-service");
        assertEquals("example.com", ingress.getHost());
    }

    @Test
    @DisplayName("Kubernetes persistent volume can store data")
    void testPersistentVolume() {
        K8sPersistentVolume pv = new K8sPersistentVolume("data-pv");
        pv.setCapacity("10Gi");
        pv.setStorageClass("standard");
        assertEquals("10Gi", pv.getCapacity());
    }

    @Test
    @DisplayName("Kubernetes statefulset can manage stateful apps")
    void testStatefulSet() {
        K8sStatefulSet statefulSet = new K8sStatefulSet("mysql");
        statefulSet.setReplicas(1);
        statefulSet.setStorage("50Gi");
        assertEquals(1, statefulSet.getReplicas());
    }
}

class K8sPod {
    private final String name;
    private String containerName;
    private String containerImage;

    public K8sPod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setContainer(String name, String image) {
        this.containerName = name;
        this.containerImage = image;
    }
}

class K8sDeployment {
    private final String name;
    private int replicas;
    private String image;

    public K8sDeployment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getReplicas() {
        return replicas;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

class K8sService {
    private final String name;
    private String type;
    private int port;

    public K8sService(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

class K8sConfigMap {
    private final String name;
    private java.util.Map<String, String> data = new java.util.HashMap<>();

    public K8sConfigMap(String name) {
        this.name = name;
    }

    public void addData(String key, String value) {
        data.put(key, value);
    }

    public String getData(String key) {
        return data.get(key);
    }
}

class K8sSecret {
    private final String name;
    private java.util.Map<String, String> data = new java.util.HashMap<>();

    public K8sSecret(String name) {
        this.name = name;
    }

    public void addData(String key, String value) {
        data.put(key, value);
    }

    public String getData(String key) {
        return data.get(key);
    }
}

class K8sNamespace {
    private final String name;

    public K8sNamespace(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class K8sIngress {
    private final String name;
    private String host;
    private String backendService;

    public K8sIngress(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setBackendService(String service) {
        this.backendService = service;
    }
}

class K8sPersistentVolume {
    private final String name;
    private String capacity;
    private String storageClass;

    public K8sPersistentVolume(String name) {
        this.name = name;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }
}

class K8sStatefulSet {
    private final String name;
    private int replicas;
    private String storage;

    public K8sStatefulSet(String name) {
        this.name = name;
    }

    public int getReplicas() {
        return replicas;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }
}