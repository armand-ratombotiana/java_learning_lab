# Module 52: Serverless Java & AWS Lambda - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What are the main benefits and drawbacks of Serverless Computing?
**Answer**:
- **Benefits**:
  - **No Infrastructure Management**: Developers never patch OS vulnerabilities or manage auto-scaling rules.
  - **Pay-as-you-go Pricing**: You are billed precisely for the compute time used (per millisecond). If there is no traffic, the cost is $0.
  - **Infinite Auto-Scaling**: The platform seamlessly scales from 0 to 10,000 concurrent requests instantly.
- **Drawbacks**:
  - **Cold Starts**: Initializing new containers causes unpredictable high-latency spikes.
  - **Vendor Lock-in**: Tightly coupling code to AWS Lambda or Azure Functions makes migration difficult.
  - **Stateless Constraints**: Functions must be completely stateless, making features like WebSockets difficult to implement natively.

### Q2: Why has Java historically struggled in the Serverless environment compared to Node.js or Python?
**Answer**:
Node.js and Python are interpreted or quickly compiled languages with very small memory footprints, meaning their runtime boots up almost instantly.
Java, conversely, relies on the JVM. Booting the JVM, allocating heap memory, loading thousands of `.class` files, executing dependency injection (like Spring Boot), and running JIT compilation takes a significant amount of time (often 5-15 seconds). This massive "Cold Start" penalty made Java unviable for user-facing Serverless REST APIs until the introduction of GraalVM Native Images.

### Q3: Explain the "Closed World Assumption" in GraalVM.
**Answer**:
To solve Java's cold start problem, GraalVM AOT (Ahead-of-Time) compilation translates Java code into a standalone native binary that doesn't need a JVM.
However, to do this, GraalVM must know *exactly* which classes, methods, and resources will be executed at compile time to aggressively remove dead code. This is the **Closed World Assumption**. 
It breaks dynamic Java features like Reflection, Dynamic Proxies, and dynamic ClassLoading because those rely on discovering code at runtime. To use Reflection in GraalVM, you must provide explicit JSON configuration files telling the compiler exactly which classes will be reflected upon at runtime so they aren't stripped from the final binary.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Preventing "Zombie" Database Connections
**Problem**: An interviewer presents this code:
```java
public class DBHandler implements RequestHandler<Map<String,String>, String> {
    public String handleRequest(Map<String,String> event, Context context) {
        Connection conn = DriverManager.getConnection("jdbc:mysql://db:3306", "user", "pass");
        // execute query
        return "Success";
    }
}
```
They note that after 2 days in production, the database crashes due to "Too many connections." Why? How do you fix it?

**Solution**:
1. **The Bug**: The developer failed to close the database connection. Because Lambda freezes the execution environment between invocations and keeps the instance alive, the dangling connection is never cleaned up. If 1,000 concurrent Lambda instances spin up, 1,000 dangling connections saturate the database.
2. **The Fix**: First, always use `try-with-resources` to close connections at the end of the invocation. Second, do *not* establish a new connection per request. Establish it once in the constructor so it survives warm invocations, but ensure you handle connection drops (e.g., using a robust connection pool like HikariCP configured specifically for serverless, or using AWS RDS Proxy to manage the connections).