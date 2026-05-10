package com.learning.k8s;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class K8sSolutionTest {

    private K8sSolution solution;

    @BeforeEach
    void setUp() {
        solution = new K8sSolution();
    }

    @Test
    void testCreateCoreV1Api() {
        ApiClient mockClient = mock(ApiClient.class);
        CoreV1Api api = solution.createCoreV1Api(mockClient);
        assertNotNull(api);
    }

    @Test
    void testCreateAppsV1Api() {
        ApiClient mockClient = mock(ApiClient.class);
        AppsV1Api api = solution.createAppsV1Api(mockClient);
        assertNotNull(api);
    }

    @Test
    void testCreateNamespace() {
        V1Namespace ns = solution.createNamespace("test-ns");
        assertEquals("test-ns", ns.getMetadata().getName());
    }

    @Test
    void testCreateDeployment() {
        V1Deployment deployment = solution.createDeployment("my-app", "nginx:latest", 3);
        assertEquals("my-app", deployment.getMetadata().getName());
        assertEquals(3, deployment.getSpec().getReplicas());
    }

    @Test
    void testCreateService() {
        V1Service service = solution.createService("my-service", "ClusterIP", 80, 8080);
        assertEquals("my-service", service.getMetadata().getName());
        assertEquals("ClusterIP", service.getSpec().getType());
    }

    @Test
    void testCreateConfigMap() {
        Map<String, String> data = Map.of("key1", "value1");
        V1ConfigMap cm = solution.createConfigMap("my-config", data);
        assertEquals("my-config", cm.getMetadata().getName());
        assertEquals("value1", cm.getData().get("key1"));
    }

    @Test
    void testCreateSecret() {
        Map<String, String> data = Map.of("username", "admin");
        V1Secret secret = solution.createSecret("my-secret", data, "Opaque");
        assertEquals("my-secret", secret.getMetadata().getName());
        assertEquals("Opaque", secret.getType());
    }
}