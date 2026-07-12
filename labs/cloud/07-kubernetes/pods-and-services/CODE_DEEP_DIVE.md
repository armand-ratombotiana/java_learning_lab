# Kubernetes Code Deep Dive

This lab provides the YAML manifests required to deploy a highly available Java Spring Boot application to a Kubernetes cluster.

## 💻 Kubernetes Manifests

In Kubernetes, you rarely create a Pod directly. Instead, you create a **Deployment**, which acts as a supervisor. It ensures that a specified number of Pod replicas are always running.

### 1. The Deployment (`deployment.yaml`)

```yaml file="labs/cloud/07-kubernetes/pods-and-services/SOLUTION/deployment.yaml"
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-backend-deployment
  labels:
    app: backend-core
spec:
  # The supervisor will ensure exactly 3 Pods are always running
  replicas: 3
  
  # The Selector tells the Deployment WHICH Pods it is responsible for managing
  selector:
    matchLabels:
      app: spring-backend
      
  # The Template defines the actual Pod that will be created
  template:
    metadata:
      labels:
        # These labels MUST match the selector above
        app: spring-backend
        version: v1.0.0
    spec:
      containers:
      - name: spring-boot-app
        image: myregistry.com/spring-backend:1.0.0
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
        
        # Resource Requests and Limits are critical for JVM apps in K8s
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
            
        # Liveness Probe: If this fails, K8s will KILL and RESTART the Pod
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
          
        # Readiness Probe: If this fails, K8s removes the Pod from the Service load balancer
        # but does NOT kill it. It just stops sending traffic to it.
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 15
          periodSeconds: 5
```

### 2. The Service (`service.yaml`)

```yaml file="labs/cloud/07-kubernetes/pods-and-services/SOLUTION/service.yaml"
apiVersion: v1
kind: Service
metadata:
  name: spring-backend-service
spec:
  # ClusterIP makes this service only reachable from INSIDE the cluster
  type: ClusterIP
  
  # The Selector is the magic link. This Service will route traffic to ANY Pod 
  # in the entire cluster that possesses the label 'app: spring-backend'.
  selector:
    app: spring-backend
    
  ports:
    - protocol: TCP
      port: 80         # The port exposed by the Service itself
      targetPort: 8080 # The port the Java application is listening on inside the Pod
```

## 🔍 Key Takeaways
1. **The Label Connection**: The entire Kubernetes ecosystem is decoupled and tied together by labels. The Service does not care about the Deployment. It only cares about finding Pods with `app: spring-backend`. If you manually spin up a Pod with that label, the Service will start routing traffic to it immediately.
2. **Probes are Mandatory**: A Java application might start up, but take 30 seconds to initialize the Spring ApplicationContext. Without a `readinessProbe`, Kubernetes will start sending traffic to the Pod the millisecond the JVM starts, resulting in hundreds of 502 Bad Gateway errors for users. The readiness probe prevents traffic from routing until Spring Boot explicitly says "I am ready".
3. **Resource Limits**: If you do not set memory limits, a Java application might consume all the RAM on the physical worker node, causing the Linux kernel's OOMKiller to terminate critical system processes. Setting `limits.memory: "1Gi"` ensures that if the JVM exceeds 1GB, the container is killed safely without taking down the node.