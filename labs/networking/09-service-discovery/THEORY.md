# Service Discovery - Theory

## Discovery Patterns

### 1. Client-Side Discovery
Client queries service registry and load-balances directly.
```
Client -> Discovery Client (Eureka/Consul)
         -> Receives list of instances
         -> Load balances (Ribbon) and calls service
```

### 2. Server-Side Discovery
Client goes through a load balancer which queries the registry.
```
Client -> Load Balancer (AWS ALB, Kubernetes)
         -> Queries registry
         -> Forwards to healthy instance
```

## Eureka Architecture
```
+---------------+       +---------------+
| Eureka Server |       | Eureka Server |
|  (Primary)    |<----->|  (Replica)    |
+-------+-------+       +-------+-------+
        ^                         ^
        |                         |
+-------+--------+     +----------+-------+
| User Service   |     | Order Service    |
| (Eureka Client)|     | (Eureka Client)  |
+----------------+     +------------------+
        ^                         ^
        |                         |
+-------+-------------------------+-------+
|           API Gateway                   |
|   (Eureka-aware load balancing)         |
+-----------------------------------------+
```

## Eureka Client Implementation
```java
@Component
public class ServiceRegistrar {
    private final EurekaClient eurekaClient;

    public ServiceRegistrar(EurekaClient eurekaClient) {
        this.eurekaClient = eurekaClient;
    }

    public List<ServiceInstance> getHealthyInstances(String serviceId) {
        return eurekaClient.getInstancesByVipAddress(serviceId, false)
            .stream()
            .filter(InstanceInfo::isHealthy)
            .map(this::toServiceInstance)
            .toList();
    }

    private ServiceInstance toServiceInstance(InstanceInfo info) {
        return new SimpleServiceInstance(
            URI.create("http://" + info.getIPAddr() + ":" + info.getPort()));
    }
}

// Custom load balancing
@Component
public class ServiceLoadBalancer {
    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    public URI getNextInstance(String serviceId) {
        List<ServiceInstance> instances = getInstances(serviceId);
        if (instances.isEmpty()) throw new RuntimeException("No instances for " + serviceId);
        int index = counters.computeIfAbsent(serviceId, k -> new AtomicInteger(0))
            .getAndIncrement() % instances.size();
        return instances.get(index).getUri();
    }
}
```

## Kubernetes DNS Discovery
```yaml
# Kubernetes headless service for DNS-based discovery
apiVersion: v1
kind: Service
metadata:
  name: user-service
spec:
  clusterIP: None  # Headless
  selector:
    app: user-service
  ports:
    - port: 8080
```

```java
// Kubernetes DNS lookup in Java
public class K8sDnsDiscovery {
    public static String resolveService(String serviceName, String namespace) {
        try {
            // Kubernetes DNS format: service.namespace.svc.cluster.local
            String dnsName = serviceName + "." + namespace + ".svc.cluster.local";
            InetAddress[] addresses = InetAddress.getAllByName(dnsName);
            return addresses[0].getHostAddress() + ":8080";
        } catch (UnknownHostException e) {
            throw new RuntimeException("Service not found: " + serviceName, e);
        }
    }
}
```
