package com.learning.capstone;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.net.*;
import java.time.*;
import java.util.concurrent.atomic.*;

public class Lab {

    record User(int id, String name, String email) {}
    record Product(String sku, String name, double price, int stock) {}
    record Order(int id, int userId, List<String> items, double total, String status, Instant createdAt) {}

    static class OrderService {
        private final AtomicInteger idGen = new AtomicInteger(1);
        private final Map<Integer, Order> orders = new ConcurrentHashMap<>();
        private final Map<String, Product> catalog = new ConcurrentHashMap<>();

        OrderService init() {
            catalog.put("SKU-001", new Product("SKU-001", "Widget", 9.99, 100));
            catalog.put("SKU-002", new Product("SKU-002", "Gadget", 24.99, 50));
            catalog.put("SKU-003", new Product("SKU-003", "Doohickey", 4.99, 200));
            return this;
        }

        Order placeOrder(int userId, List<String> items) {
            double total = 0;
            for (var sku : items) {
                var product = catalog.get(sku);
                if (product == null) throw new IllegalArgumentException("Unknown SKU: " + sku);
                if (product.stock() < 1) throw new IllegalStateException("Out of stock: " + sku);
                total += product.price();
            }
            var order = new Order(idGen.getAndIncrement(), userId, items, total, "CONFIRMED", Instant.now());
            orders.put(order.id(), order);
            return order;
        }

        Order getOrder(int id) { return orders.get(id); }
        List<Order> getUserOrders(int userId) {
            return orders.values().stream().filter(o -> o.userId() == userId).toList();
        }
        Product getProduct(String sku) { return catalog.get(sku); }
        List<Product> getCatalog() { return List.copyOf(catalog.values()); }
    }

    static class PaymentService {
        boolean processPayment(int orderId, double amount) {
            if (amount <= 0 || amount > 10000) return false;
            return true;
        }
    }

    static class NotificationService {
        private final List<String> sent = new CopyOnWriteArrayList<>();

        void sendEmail(String to, String subject, String body) {
            sent.add("To: " + to + " | " + subject + " | " + body);
        }

        List<String> getSentNotifications() { return List.copyOf(sent); }
    }

    static class CompositeApp {
        private final OrderService orderService = new OrderService().init();
        private final PaymentService paymentService = new PaymentService();
        private final NotificationService notificationService = new NotificationService();
        private final Map<Integer, User> users = Map.of(
            1, new User(1, "Alice", "alice@example.com"),
            2, new User(2, "Bob", "bob@example.com")
        );

        String checkout(int userId, List<String> items) {
            var user = users.get(userId);
            if (user == null) return "User not found";

            try {
                var order = orderService.placeOrder(userId, items);
                boolean paid = paymentService.processPayment(order.id(), order.total());
                if (!paid) {
                    return "Payment failed for order " + order.id();
                }
                notificationService.sendEmail(user.email(), "Order Confirmed",
                    "Your order #" + order.id() + " ($" + order.total() + ") is confirmed.");
                return "Order " + order.id() + " placed successfully! Total: $" + order.total();
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        void report() {
            System.out.println("  Orders placed: " + orderService.getUserOrders(1).size());
            System.out.println("  Notifications sent: " + notificationService.getSentNotifications().size());
        }
    }

    static class ECommerceMicroservices {
        private final CompositeApp app = new CompositeApp();

        void runDemo() {
            System.out.println("  Alice checks out with 2 Widgets and 1 Gadget:");
            var result1 = app.checkout(1, List.of("SKU-001", "SKU-001", "SKU-002"));
            System.out.println("    " + result1);

            System.out.println("\n  Bob checks out with 1 Doohickey:");
            var result2 = app.checkout(2, List.of("SKU-003"));
            System.out.println("    " + result2);

            System.out.println("\n  Trying out-of-stock item:");
            var result3 = app.checkout(1, List.of("SKU-999"));
            System.out.println("    " + result3);

            app.report();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Capstone Project Lab ===\n");

        systemArchitecture();
        endToEndFlow();
        observability();
        deployment();
        nextSteps();
    }

    static void systemArchitecture() {
        System.out.println("--- System Architecture ---");
        System.out.println("""
  Mini e-commerce platform (microservices):

  ┌──────────────┐
  │ API Gateway  │  (routing, auth, rate-limit)
  └──────┬───────┘
         │
    ┌────┴────────────┐
    │  Order Service  │  (business logic)
    ├─────────────────┤
    │ Payment Service │  (payment processing)
    ├─────────────────┤
    │ Catalog Service │  (product CRUD)
    ├─────────────────┤
    │  User Service   │  (authentication)
    ├─────────────────┤
    │ Notification    │  (email/SMS/push)
    └─────────────────┘

  Tech stack:
  - Spring Boot 3 + Java 21 (virtual threads)
  - PostgreSQL / MongoDB for persistence
  - Kafka / RabbitMQ for async events
  - Docker + Kubernetes for deployment
  - Prometheus + Grafana for monitoring
    """);
    }

    static void endToEndFlow() {
        System.out.println("\n--- End-to-End Flow ---");
        var app = new ECommerceMicroservices();
        app.runDemo();
    }

    static void observability() {
        System.out.println("\n--- Observability Setup ---");
        System.out.println("""
  Logging (ELK Stack):
    Filebeat -> Logstash -> Elasticsearch -> Kibana
    structured JSON logs with traceId, spanId, userId

  Metrics (Prometheus + Grafana):
    Business metrics:
      orders_per_minute{status}
      revenue_per_hour
      active_users
    Technical metrics:
      p50/p95/p99 latency per endpoint
      error_rate per service
      jvm_memory_used_bytes

  Tracing (Jaeger / OpenTelemetry):
    End-to-end traces across services:
    API Gateway -> Order Service -> Payment Service -> Notification

  Alerting (Alertmanager):
    - Error rate > 5% for 5 min -> PagerDuty
    - P95 latency > 1s -> Slack
    - Order failure rate > 1% -> email
    """);
    }

    static void deployment() {
        System.out.println("\n--- Deployment Architecture ---");
        System.out.println("""
  Kubernetes cluster:

  Namespaces:
    - prod (production workloads)
    - staging (pre-production)
    - monitoring (Prometheus, Grafana, Jaeger)
    - istio-system (service mesh control plane)

  Deployment workflow:
    1. Developer pushes code to GitHub
    2. GitHub Actions builds container image
    3. Image pushed to container registry (ECR / GCR)
    4. GitOps: ArgoCD syncs new image tag from Git
    5. Canary deployment: 10% traffic -> 50% -> 100%
    6. Health checks pass -> promotion
    7. If failures -> rollback (revert Git commit)

  Infrastructure as Code (Terraform):
    - EKS cluster (AWS)
    - RDS PostgreSQL (primary + replica)
    - ElastiCache Redis (caching + session store)
    - S3 for static assets
    """);
    }

    static void nextSteps() {
        System.out.println("\n--- Next Steps & Further Learning ---");
        System.out.println("""
  Topics to explore:

  1. Production readiness
     - Load testing with k6 / Gatling
     - Security scanning (Snyk, Trivy)
     - Penetration testing

  2. Advanced patterns
     - Saga pattern for distributed transactions
     - CQRS + Event Sourcing
     - Feature flags with LaunchDarkly

  3. Performance
     - JVM tuning for containers
     - Database query optimization (EXPLAIN ANALYZE)
     - CDN and caching strategies

  4. Career growth
     - System design interviews
     - Open source contributions
     - Cloud certifications (AWS SA Pro, CKAD, etc.)

  Congratulations on completing the Java Learning Lab! 🎉
    """);
    }
}
