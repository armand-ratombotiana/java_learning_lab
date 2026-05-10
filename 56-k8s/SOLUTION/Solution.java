package com.learning.k8s;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;

import java.util.HashMap;
import java.util.Map;

public class K8sSolution {

    public ApiClient createClient() throws Exception {
        return Config.fromConfig();
    }

    public CoreV1Api createCoreV1Api(ApiClient client) {
        return new CoreV1Api(client);
    }

    public AppsV1Api createAppsV1Api(ApiClient client) {
        return new AppsV1Api(client);
    }

    public V1Namespace createNamespace(String name) {
        return new V1Namespace()
                .metadata(new V1ObjectMeta().name(name));
    }

    public V1Deployment createDeployment(String name, String image, int replicas) {
        Map<String, String> labels = new HashMap<>();
        labels.put("app", name);

        V1Deployment deployment = new V1Deployment();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name);
        metadata.setLabels(labels);
        deployment.setMetadata(metadata);

        V1DeploymentSpec spec = new V1DeploymentSpec();
        spec.setReplicas(replicas);

        V1LabelSelector selector = new V1LabelSelector();
        selector.setMatchLabels(labels);
        spec.setSelector(selector);

        V1PodTemplateSpec template = new V1PodTemplateSpec();
        V1ObjectMeta templateMetadata = new V1ObjectMeta();
        templateMetadata.setLabels(labels);
        template.setMetadata(templateMetadata);

        V1Container container = new V1Container();
        container.setName(name);
        container.setImage(image);
        template.setSpec(new V1PodSpec().addContainersItem(container));

        spec.setTemplate(template);
        deployment.setSpec(spec);

        return deployment;
    }

    public V1Service createService(String name, String serviceType, int port, int targetPort) {
        Map<String, String> labels = new HashMap<>();
        labels.put("app", name);

        V1Service service = new V1Service();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name);
        service.setMetadata(metadata);

        V1ServiceSpec spec = new V1ServiceSpec();
        spec.setType(serviceType);
        spec.setSelector(labels);

        V1ServicePort servicePort = new V1ServicePort();
        servicePort.setPort(port);
        servicePort.setTargetPort(new io.kubernetes.client.openapi.models.V1IntOrString(targetPort));
        spec.setPorts(java.util.List.of(servicePort));

        service.setSpec(spec);

        return service;
    }

    public V1ConfigMap createConfigMap(String name, Map<String, String> data) {
        V1ConfigMap configMap = new V1ConfigMap();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name);
        configMap.setMetadata(metadata);
        configMap.setData(data);
        return configMap;
    }

    public V1Secret createSecret(String name, Map<String, String> data, String type) {
        V1Secret secret = new V1Secret();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name);
        secret.setMetadata(metadata);
        secret.setType(type);
        secret.setData(data);
        return secret;
    }
}