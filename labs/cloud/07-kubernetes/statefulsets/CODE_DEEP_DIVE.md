# StatefulSets Code Deep Dive

This lab provides the YAML manifests required to deploy a stateful application (a simulated database) using a StatefulSet and a Headless Service.

## 💻 Kubernetes Manifests

### 1. The Headless Service (`headless-service.yaml`)

```yaml file="labs/cloud/07-kubernetes/statefulsets/SOLUTION/headless-service.yaml"
apiVersion: v1
kind: Service
metadata:
  name: db-service
  labels:
    app: database
spec:
  # clusterIP: None is what makes this service "Headless"
  clusterIP: None
  selector:
    app: database
  ports:
  - port: 5432
    name: db-port
```

### 2. The StatefulSet (`statefulset.yaml`)

```yaml file="labs/cloud/07-kubernetes/statefulsets/SOLUTION/statefulset.yaml"
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: db
spec:
  # The name of the Headless Service defined above
  serviceName: "db-service"
  
  # Start with 3 nodes (db-0, db-1, db-2)
  replicas: 3
  
  selector:
    matchLabels:
      app: database
      
  template:
    metadata:
      labels:
        app: database
    spec:
      containers:
      - name: db-container
        image: postgres:15-alpine
        ports:
        - containerPort: 5432
          name: db-port
        
        # Mount the persistent volume into the container
        volumeMounts:
        - name: db-data-volume
          mountPath: /var/lib/postgresql/data
          
  # The magic of StatefulSets: Dynamic storage provisioning
  volumeClaimTemplates:
  - metadata:
      name: db-data-volume
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: 10Gi
```

## 🔍 Key Takeaways
1. **The DNS Resolution**: Because of the `serviceName: "db-service"` link, Kubernetes will automatically create DNS records for each individual Pod. Inside the cluster, an application can connect specifically to the first node using the hostname `db-0.db-service.default.svc.cluster.local`.
2. **Volume Claim Templates**: Notice that we don't define a specific PVC. The `volumeClaimTemplates` block acts as a factory. When K8s creates `db-0`, it reads this template, creates a 10GB PVC named `db-data-volume-db-0`, and attaches it. When it creates `db-1`, it creates a 10GB PVC named `db-data-volume-db-1`.
3. **Ordered Execution**: If you run `kubectl apply -f statefulset.yaml`, you will see that `db-0` is created. Kubernetes will wait until `db-0` is fully running and its readiness probe passes before it even attempts to create `db-1`.