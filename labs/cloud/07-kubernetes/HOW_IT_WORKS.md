# How Kubernetes Works

## API Request Flow

```
1. kubectl apply -f deployment.yaml
2. kubectl sends POST to API Server (authentication + authorization)
3. API Server validates manifest (schema + admission webhooks)
4. Manifest stored in etcd (distributed key-value store)
5. Controller Manager watches for new Deployment
6. Creates ReplicaSet (for version tracking)
7. Scheduler watches for unscheduled pods
8. Scheduler assigns pod to node (score-based: resources, affinity, taints)
9. kubelet on assigned node receives pod spec
10. kubelet pulls image (via containerd/CRI)
11. Container runtime creates pod (CNI for networking, CSI for storage)
12. kubelet reports pod status back to API Server
```

## Deployment and Scaling

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: java-app
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: java-app
  template:
    metadata:
      labels:
        app: java-app
    spec:
      containers:
      - name: app
        image: myapp:1.0.0
        ports:
        - containerPort: 8080
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
```

## Service Networking

```yaml
apiVersion: v1
kind: Service
metadata:
  name: java-app-svc
spec:
  selector:
    app: java-app
  ports:
  - port: 80          # Service port
    targetPort: 8080   # Container port
  type: ClusterIP      # Internal only (default)
  # type: LoadBalancer # Creates cloud LB
  # type: NodePort    # Exposes on node IP
```

```
DNS: java-app-svc.namespace.svc.cluster.local
Resolves to ClusterIP (virtual IP) → kube-proxy iptables/IPVS → pod IP
```

## Ingress (HTTP Routing)

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: java-app-ingress
spec:
  rules:
  - host: api.example.com
    http:
      paths:
      - path: /users
        pathType: Prefix
        backend:
          service:
            name: user-service
            port:
              number: 80
      - path: /orders
        pathType: Prefix
        backend:
          service:
            name: order-service
            port:
              number: 80
```
