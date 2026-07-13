# Strangler Fig Pattern Code Deep Dive

This lab provides a pure Java simulation of a simple API Gateway routing traffic between a Legacy Monolith and a new Modern Microservice to demonstrate the Strangler Fig pattern in action.

## 💻 Pure Java Implementation

```java file="labs/system-design/10-microservices/strangler-fig/SOLUTION/StranglerGatewaySim.java"
package systemdesign.microservices.strangler;

import java.util.HashMap;
import java.util.Map;

/**
 * A simulation of an API Gateway implementing the Strangler Fig Pattern.
 */
public class StranglerGatewaySim {

    // Simulated Backend Systems
    static class LegacyMonolith {
        String handleRequest(String path) {
            return "[LEGACY MONOLITH] Handled legacy request for: " + path;
        }
    }

    static class ModernUserService {
        String handleRequest(String path) {
            return "[MODERN MICROSERVICE] Handled fast, isolated request for: " + path;
        }
    }

    // The API Gateway
    private final LegacyMonolith monolith = new LegacyMonolith();
    private final ModernUserService userService = new ModernUserService();
    
    // The Routing Table (The "Strangler" logic)
    private final Map<String, Boolean> routingTable = new HashMap<>();

    public StranglerGatewaySim() {
        // Initial state: 100% of traffic goes to the monolith
        routingTable.put("/api/orders", false);
        routingTable.put("/api/inventory", false);
        routingTable.put("/api/users", false);
    }

    /**
     * The core routing logic of the API Gateway.
     */
    public String routeRequest(String path) {
        System.out.println("Gateway received request: " + path);
        
        // Find the base path (e.g., "/api/users")
        String basePath = getBasePath(path);
        
        // Check the routing table to see if this feature has been strangled
        boolean isStrangled = routingTable.getOrDefault(basePath, false);
        
        if (isStrangled) {
            // Feature has been migrated. Route to new microservice.
            return userService.handleRequest(path);
        } else {
            // Feature is still in the legacy system. Route to monolith.
            return monolith.handleRequest(path);
        }
    }

    /**
     * Dynamically updates the routing table without downtime.
     */
    public void strangleFeature(String basePath) {
        System.out.println("\n[GATEWAY ADMIN] STRANGLING FEATURE: " + basePath);
        System.out.println("[GATEWAY ADMIN] Routing all future traffic for " + basePath + " to the Modern Microservice.");
        routingTable.put(basePath, true);
    }

    /**
     * Extracts the base path for routing (e.g., "/api/users/123" -> "/api/users")
     */
    private String getBasePath(String path) {
        String[] parts = path.split("/");
        if (parts.length >= 3) {
            return "/" + parts[1] + "/" + parts[2];
        }
        return path;
    }

    public static void main(String[] args) {
        StranglerGatewaySim gateway = new StranglerGatewaySim();

        System.out.println("--- PHASE 1: Initial State (100% Legacy) ---");
        System.out.println(gateway.routeRequest("/api/orders/445"));
        System.out.println(gateway.routeRequest("/api/users/99"));

        System.out.println("\n--- PHASE 2: Incremental Migration ---");
        // The business finishes rewriting the User Profile service.
        // We update the Gateway routing rules.
        gateway.strangleFeature("/api/users");

        System.out.println("\n--- PHASE 3: Strangled State ---");
        System.out.println(gateway.routeRequest("/api/orders/446")); // Still goes to Monolith
        System.out.println(gateway.routeRequest("/api/users/99"));  // Now goes to Microservice!
    }
}
```

## 🔍 Key Takeaways
1. **Dynamic Routing**: The `strangleFeature` method demonstrates how an API Gateway can be updated dynamically. In a real system like Kong or Spring Cloud Gateway, you would update the configuration via an API call or a GitOps pipeline, and the Gateway would instantly begin routing traffic to the new microservice without dropping a single HTTP connection.
2. **The Facade Pattern**: Notice that the client making the request (`gateway.routeRequest("/api/users/99")`) has absolutely no idea that the architecture changed between Phase 1 and Phase 3. The URL remained exactly the same. The Gateway abstracts the complexity of the migration away from the frontend applications (Web, iOS, Android).