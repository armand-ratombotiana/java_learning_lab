# DNS and Load Balancing - Theory

## DNS Resolution Process
```
Client                    DNS Resolver
   |                         |
   |--- query example.com -->|
   |                         |--- Root DNS --->
   |                         |--- TLD DNS (.com) --->
   |                         |--- Authoritative --->
   |<-- IP: 93.184.216.34 ---|
```

## DNS Record Types
- **A**: IPv4 address mapping
- **AAAA**: IPv6 address mapping
- **CNAME**: Canonical name (alias)
- **MX**: Mail exchange
- **TXT**: Text metadata
- **SRV**: Service locator
- **NS**: Name server

## Load Balancing Algorithms
1. **Round Robin**: Sequentially distribute requests
2. **Least Connections**: Send to server with fewest active connections
3. **IP Hash**: Consistent routing based on client IP
4. **Weighted Round Robin**: Servers with higher capacity get more traffic
5. **Random**: Simple random distribution

## Java Load Balancer Implementation
```java
public class RoundRobinLoadBalancer {
    private final List<String> servers;
    private int currentIndex = 0;

    public RoundRobinLoadBalancer(List<String> servers) {
        this.servers = servers;
    }

    public synchronized String getNextServer() {
        String server = servers.get(currentIndex);
        currentIndex = (currentIndex + 1) % servers.size();
        return server;
    }
}

// Weighted Round Robin
public class WeightedLoadBalancer {
    private final List<Server> servers;
    private int currentWeight = 0;
    private int currentIndex = -1;

    record Server(String host, int weight) {}

    public synchronized Server getNext() {
        while (true) {
            currentIndex = (currentIndex + 1) % servers.size();
            if (currentIndex == 0) {
                currentWeight -= gcd();
                if (currentWeight <= 0) {
                    currentWeight = maxWeight();
                    if (currentWeight == 0) return null;
                }
            }
            if (servers.get(currentIndex).weight >= currentWeight)
                return servers.get(currentIndex);
        }
    }

    private int gcd() { /* calculate GCD of all weights */ return 1; }
    private int maxWeight() { /* return max weight */ return 1; }
}
```

## Health Checks
```java
public class HealthChecker {
    private final ScheduledExecutorService scheduler =
        Executors.newScheduledThreadPool(1);
    private final Map<String, Boolean> serverHealth = new ConcurrentHashMap<>();

    public void startHealthCheck(List<String> servers) {
        scheduler.scheduleAtFixedRate(() -> {
            servers.parallelStream().forEach(server -> {
                try {
                    URL url = new URL("http://" + server + "/health");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    boolean healthy = conn.getResponseCode() == 200;
                    serverHealth.put(server, healthy);
                    conn.disconnect();
                } catch (Exception e) {
                    serverHealth.put(server, false);
                }
            });
        }, 0, 10, TimeUnit.SECONDS);
    }

    public List<String> getHealthyServers() {
        return serverHealth.entrySet().stream()
            .filter(Map.Entry::getValue)
            .map(Map.Entry::getKey)
            .toList();
    }
}
```
