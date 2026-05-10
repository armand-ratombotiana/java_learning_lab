package solution;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KubernetesSolutionTest {

    @Test
    void testDeployment() {
        KubernetesSolution solution = new KubernetesSolution();
        String deployment = solution.generateDeployment();
        assertNotNull(deployment);
        assertTrue(deployment.contains("kind: Deployment"));
        assertTrue(deployment.contains("replicas: 3"));
        assertTrue(deployment.contains("livenessProbe"));
        assertTrue(deployment.contains("readinessProbe"));
    }

    @Test
    void testService() {
        KubernetesSolution solution = new KubernetesSolution();
        String service = solution.generateService();
        assertNotNull(service);
        assertTrue(service.contains("kind: Service"));
        assertTrue(service.contains("type: ClusterIP"));
    }

    @Test
    void testIngress() {
        KubernetesSolution solution = new KubernetesSolution();
        String ingress = solution.generateIngress();
        assertNotNull(ingress);
        assertTrue(ingress.contains("kind: Ingress"));
        assertTrue(ingress.contains("tls:"));
    }

    @Test
    void testConfigMap() {
        KubernetesSolution solution = new KubernetesSolution();
        String configMap = solution.generateConfigMap();
        assertNotNull(configMap);
        assertTrue(configMap.contains("kind: ConfigMap"));
    }

    @Test
    void testHPA() {
        KubernetesSolution solution = new KubernetesSolution();
        String hpa = solution.generateHPA();
        assertNotNull(hpa);
        assertTrue(hpa.contains("kind: HorizontalPodAutoscaler"));
        assertTrue(hpa.contains("minReplicas: 2"));
    }

    @Test
    void testRBAC() {
        KubernetesSolution solution = new KubernetesSolution();
        String rbac = solution.generateRBAC();
        assertNotNull(rbac);
        assertTrue(rbac.contains("kind: Role"));
        assertTrue(rbac.contains("kind: RoleBinding"));
    }
}