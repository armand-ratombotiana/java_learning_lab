package solution;

import java.util.*;

public class KubernetesSolution {

    public static class K8sResource {
        public String kind;
        public String apiVersion;
        public Map<String, Object> metadata = new HashMap<>();
        public Map<String, Object> spec = new HashMap<>();

        public K8sResource(String kind) {
            this.kind = kind;
        }
    }

    // Deployment YAML
    public String generateDeployment() {
        return """
            apiVersion: apps/v1
            kind: Deployment
            metadata:
              name: myapp
              labels:
                app: myapp
                version: v1
            spec:
              replicas: 3
              selector:
                matchLabels:
                  app: myapp
              template:
                metadata:
                  labels:
                    app: myapp
                spec:
                  serviceAccountName: myapp-sa
                  securityContext:
                    runAsNonRoot: true
                    runAsUser: 1000
                  containers:
                  - name: myapp
                    image: myapp:latest
                    imagePullPolicy: Always
                    ports:
                    - containerPort: 8080
                      name: http
                    - containerPort: 8443
                      name: https
                    env:
                    - name: SPRING_PROFILES_ACTIVE
                      value: "prod"
                    - name: JAVA_OPTS
                      value: "-Xmx512m -Xms256m"
                    resources:
                      requests:
                        memory: "256Mi"
                        cpu: "250m"
                      limits:
                        memory: "1Gi"
                        cpu: "1000m"
                    livenessProbe:
                      httpGet:
                        path: /actuator/health/liveness
                        port: 8080
                      initialDelaySeconds: 60
                      periodSeconds: 10
                    readinessProbe:
                      httpGet:
                        path: /actuator/health/readiness
                        port: 8080
                      initialDelaySeconds: 30
                      periodSeconds: 5
                    volumeMounts:
                    - name: config
                      mountPath: /config
                  volumes:
                  - name: config
                    configMap:
                      name: myapp-config
            """;
    }

    // Service YAML
    public String generateService() {
        return """
            apiVersion: v1
            kind: Service
            metadata:
              name: myapp-service
              labels:
                app: myapp
            spec:
              type: ClusterIP
              selector:
                app: myapp
              ports:
              - name: http
                port: 80
                targetPort: 8080
                protocol: TCP
              - name: https
                port: 443
                targetPort: 8443
                protocol: TCP
            """;
    }

    // Ingress YAML
    public String generateIngress() {
        return """
            apiVersion: networking.k8s.io/v1
            kind: Ingress
            metadata:
              name: myapp-ingress
              annotations:
                nginx.ingress.kubernetes.io/ssl-redirect: "true"
                cert-manager.io/cluster-issuer: "letsencrypt-prod"
            spec:
              ingressClassName: nginx
              tls:
              - hosts:
                - myapp.example.com
                secretName: myapp-tls
              rules:
              - host: myapp.example.com
                http:
                  paths:
                  - path: /
                    pathType: Prefix
                    backend:
                      service:
                        name: myapp-service
                        port:
                          number: 80
            """;
    }

    // ConfigMap
    public String generateConfigMap() {
        return """
            apiVersion: v1
            kind: ConfigMap
            metadata:
              name: myapp-config
            data:
              application.properties: |
                server.port=8080
                spring.datasource.url=jdbc:postgresql://postgres:5432/mydb
                spring.redis.host=redis
              logback.xml: |
                <configuration>
                  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
                    <encoder>
                      <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
                    </encoder>
                  </appender>
                  <root level="INFO">
                    <appender-ref ref="STDOUT" />
                  </root>
                </configuration>
            """;
    }

    // Secret
    public String generateSecret() {
        return """
            apiVersion: v1
            kind: Secret
            metadata:
              name: myapp-secret
            type: Opaque
            data:
              username: YWRtaW4=
              password: c2VjcmV0cGFzcw==
              # Base64 encoded values
            """;
    }

    // HorizontalPodAutoscaler
    public String generateHPA() {
        return """
            apiVersion: autoscaling/v2
            kind: HorizontalPodAutoscaler
            metadata:
              name: myapp-hpa
            spec:
              scaleTargetRef:
                apiVersion: apps/v1
                kind: Deployment
                name: myapp
              minReplicas: 2
              maxReplicas: 10
              metrics:
              - type: Resource
                resource:
                  name: cpu
                  target:
                    type: Utilization
                    averageUtilization: 70
              - type: Resource
                resource:
                  name: memory
                  target:
                    type: Utilization
                    averageUtilization: 80
              behavior:
                scaleDown:
                  stabilizationWindowSeconds: 300
                  policies:
                  - type: Percent
                    value: 50
                    periodSeconds: 60
                scaleUp:
                  stabilizationWindowSeconds: 0
                  policies:
                  - type: Percent
                    value: 100
                    periodSeconds: 15
                  - type: Pods
                    value: 4
                    periodSeconds: 15
            """;
    }

    // PodDisruptionBudget
    public String generatePDB() {
        return """
            apiVersion: policy/v1
            kind: PodDisruptionBudget
            metadata:
              name: myapp-pdb
            spec:
              minAvailable: 2
              selector:
                matchLabels:
                  app: myapp
            """;
    }

    // ServiceAccount
    public String generateServiceAccount() {
        return """
            apiVersion: v1
            kind: ServiceAccount
            metadata:
              name: myapp-sa
              annotations:
                description: "Service account for myapp"
            """;
    }

    // Role and RoleBinding
    public String generateRBAC() {
        return """
            apiVersion: rbac.authorization.k8s.io/v1
            kind: Role
            metadata:
              name: myapp-role
            rules:
            - apiGroups: [""]
              resources: ["pods", "services", "configmaps"]
              verbs: ["get", "list", "watch"]
            ---
            apiVersion: rbac.authorization.k8s.io/v1
            kind: RoleBinding
            metadata:
              name: myapp-rolebinding
            subjects:
            - kind: ServiceAccount
              name: myapp-sa
              namespace: default
            roleRef:
              kind: Role
              name: myapp-role
              apiGroup: rbac.authorization.k8s.io
            """;
    }
}