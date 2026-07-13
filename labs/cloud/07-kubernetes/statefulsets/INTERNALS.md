# StatefulSets Internals

## 🗄️ Persistent Volumes (PV) and Claims (PVC)
To provide stable storage, Kubernetes decouples storage from compute.

1. **Persistent Volume (PV)**: A piece of actual storage in the cluster (e.g., an AWS EBS volume, an NFS share, or a local disk). It is provisioned by an administrator or dynamically via a StorageClass.
2. **Persistent Volume Claim (PVC)**: A request for storage by a user. (e.g., "I need a 10GB disk that is ReadWriteOnce").

In a `StatefulSet`, you do not define a single PVC. You define a **VolumeClaimTemplate**. 
When the StatefulSet creates `db-0`, it uses the template to dynamically generate a PVC named `data-db-0` and binds it to `db-0`. When it creates `db-1`, it generates `data-db-1`. 

If `db-0` is deleted, its PVC (`data-db-0`) is **not** deleted. The data is preserved. When `db-0` is recreated, it automatically reattaches to the existing `data-db-0` PVC.

## 🌐 The Headless Service
A standard K8s Service provides load balancing. It gives you a single IP address, and requests to that IP are randomly routed to one of the backing Pods.

For a database, this is usually wrong. If a client wants to write data, it *must* connect to the Primary node (`db-0`), not a read-only Replica (`db-1`). 

To solve this, StatefulSets require a **Headless Service**.
A Headless Service is created by setting `clusterIP: None`. 
- It does not load balance.
- It does not get a virtual IP.
- Instead, it creates a specific DNS record for *every single Pod* in the StatefulSet.

If your Headless Service is named `db-service`, you can resolve the IP of the primary node directly using the DNS name: `db-0.db-service.default.svc.cluster.local`.