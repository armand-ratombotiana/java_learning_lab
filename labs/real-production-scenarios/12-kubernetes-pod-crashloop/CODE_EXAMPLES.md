# Lab 12 — Kubernetes Pod CrashLoop: Code Examples

## Health Check Endpoints

```java
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HealthCheckServer {
    private final HttpServer server;

    public HealthCheckServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(2));

        // Liveness: is the process alive and not hung?
        server.createContext("/healthz", exchange -> {
            String response = "{\"status\":\"ok\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });

        // Readiness: is the app ready to serve traffic?
        server.createContext("/readyz", exchange -> {
            if (isDatabaseConnected() && isCacheWarmed()) {
                String response = "{\"status\":\"ready\"}";
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
            } else {
                String response = "{\"status\":\"not ready\"}";
                exchange.sendResponseHeaders(503, response.length());
                exchange.getResponseBody().write(response.getBytes());
            }
            exchange.close();
        });

        // Startup: has the app completed initialization?
        server.createContext("/startupz", exchange -> {
            String response = "{\"status\":\"started\"}";
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });
    }

    public void start() { server.start(); }
    public void stop() { server.stop(0); }

    boolean isDatabaseConnected() { return true; }
    boolean isCacheWarmed() { return true; }
}
```

## OOM Error Handler

```java
import java.lang.management.ManagementFactory;
import javax.management.*;

public class OOMErrorHandler {
    public static void register() {
        NotificationEmitter emitter = (NotificationEmitter) ManagementFactory.getMemoryMXBean();
        emitter.addNotificationListener((notification, handback) -> {
            if (notification.getType().equals(
                    "java.management.memory.threshold.exceeded")) {
                System.err.println("OOM WARNING: Memory threshold exceeded!");
                System.err.println("Dumping diagnostic data before crash...");
                dumpDiagnostics();
            }
        }, null, null);
    }

    static void dumpDiagnostics() {
        try {
            Runtime rt = Runtime.getRuntime();
            System.err.println("Max memory: " + rt.maxMemory() / 1024 / 1024 + " MB");
            System.err.println("Total memory: " + rt.totalMemory() / 1024 / 1024 + " MB");
            System.err.println("Free memory: " + rt.freeMemory() / 1024 / 1024 + " MB");

            // Trigger heap dump
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            new ProcessBuilder("jcmd", pid, "GC.heap_dump", "/tmp/pre_oom.hprof").start();
        } catch (Exception e) {
            System.err.println("Failed to dump diagnostics: " + e.getMessage());
        }
    }
}
```
