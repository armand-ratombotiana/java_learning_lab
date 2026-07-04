# Service Discovery - Why It Exists

## The Problem

In dynamic environments (cloud, Kubernetes, Docker):
1. **Dynamic IPs**: Services get new IPs on restart or scaling
2. **Auto-scaling**: Services scale up/down constantly
3. **Failover**: Failed instances must be removed from rotation
4. **Blue/green deployments**: Multiple versions coexist

## Without Discovery vs With Discovery
| Aspect | Without Discovery | With Discovery |
|--------|------------------|----------------|
| Configuration | Hardcoded IPs | Dynamic resolution |
| Scaling | Manual updates | Automatic registration |
| Failure handling | Manual intervention | Automatic removal |
| Load balancing | Fixed targets | Dynamic pool |

## Service Registry Options
- **Eureka** (Netflix): Java-native, Spring Cloud integration
- **Consul** (HashiCorp): Multi-datacenter, health checking
- **Kubernetes DNS**: Built-in, no additional infrastructure
- **ZooKeeper**: Distributed coordination, used by Kafka
- **etcd**: Key-value store, used by Kubernetes
