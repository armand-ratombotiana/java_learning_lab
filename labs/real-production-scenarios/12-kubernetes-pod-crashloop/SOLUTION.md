# Solution: Kubernetes Pod CrashLoop Resolution

## Step 1: Immediate Rollback

```bash
# Rollback to previous working version via ArgoCD
argocd app rollback order-processor --prune

# Verify rollback status
argocd app get order-processor

# Check pod status
kubectl get pods -n production -l app=order-processor

# Confirm all pods running
kubectl wait --for=condition=Ready pods -n production -l app=order-processor --timeout=120s
```

## Step 2: Proper Resource Requests and Limits

### Profiling-Based Resource Configuration

```yaml
# kubernetes/overlays/production/order-processor-resources.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-processor
  namespace: production
spec:
  template:
    spec:
      containers:
        - name: order-processor
          resources:
            requests:
              memory: "1Gi"
              cpu: "500m"
            limits:
              memory: "4Gi"
              cpu: "2"
```

### Java Code — JVM Container Awareness

```java
package com.acmecorp.orderprocessor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.concurrent.Executor;

@Configuration
public class ThreadPoolConfig {

    private static final long MEGABYTE = 1024L * 1024L;
    private static final double NATIVE_MEMORY_OVERHEAD_PER_THREAD_MB = 2.0;
    private static final double SAFETY_MARGIN = 0.8;

    private final ThreadPoolProperties properties;

    public ThreadPoolConfig(ThreadPoolProperties properties) {
        this.properties = properties;
    }

    @Bean(name = "orderProcessingExecutor")
    public Executor orderProcessingExecutor() {
        int maxThreads = calculateMaxThreads();
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getCorePoolSize());
        executor.setMaxPoolSize(maxThreads);
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.setThreadNamePrefix("order-processor-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        
        return executor;
    }

    public int calculateMaxThreads() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long maxHeapBytes = memoryBean.getHeapMemoryUsage().getMax();
        long maxHeapMB = maxHeapBytes / MEGABYTE;

        double availableForNative = (maxHeapMB * SAFETY_MARGIN) - maxHeapMB;
        int maxThreads = (int) Math.floor(availableForNative / NATIVE_MEMORY_OVERHEAD_PER_THREAD_MB);

        int configuredMax = properties.getMaxPoolSize();
        return Math.min(maxThreads, configuredMax);
    }

    public static void main(String[] args) {
        ThreadPoolConfig config = new ThreadPoolConfig(new ThreadPoolProperties());
        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Max memory (MB): " + Runtime.getRuntime().maxMemory() / MEGABYTE);
        System.out.println("Calculated max threads: " + config.calculateMaxThreads());
    }
}
```

### Thread Pool Properties

```java
package com.acmecorp.orderprocessor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "order.processor.thread-pool")
public class ThreadPoolProperties {
    private int corePoolSize = 50;
    private int maxPoolSize = 200;
    private int queueCapacity = 1000;

    public int getCorePoolSize() { return corePoolSize; }
    public void setCorePoolSize(int corePoolSize) { this.corePoolSize = corePoolSize; }
    public int getMaxPoolSize() { return maxPoolSize; }
    public void setMaxPoolSize(int maxPoolSize) { this.maxPoolSize = maxPoolSize; }
    public int getQueueCapacity() { return queueCapacity; }
    public void setQueueCapacity(int queueCapacity) { this.queueCapacity = queueCapacity; }
}
```

## Step 3: Startup Probe Configuration

```yaml
# kubernetes/overlays/production/order-processor-probes.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-processor
  namespace: production
spec:
  template:
    spec:
      containers:
        - name: order-processor
          startupProbe:
            httpGet:
              path: /actuator/health
              port: 8080
              httpHeaders:
                - name: Accept
                  value: application/json
            initialDelaySeconds: 5
            periodSeconds: 5
            timeoutSeconds: 5
            successThreshold: 1
            failureThreshold: 12  # 12 * 5 = 60 seconds max startup time
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 60  # After startup probe succeeds
            periodSeconds: 15
            timeoutSeconds: 5
            successThreshold: 1
            failureThreshold: 3
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
            timeoutSeconds: 3
            successThreshold: 1
            failureThreshold: 3
```

### Java Code — Custom Health Indicator

```java
package com.acmecorp.orderprocessor.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class StartupHealthIndicator implements HealthIndicator {

    private volatile boolean ready = false;
    private final long startTime = System.currentTimeMillis();

    @Override
    public Health health() {
        if (ready) {
            return Health.up()
                .withDetail("startupTime", System.currentTimeMillis() - startTime + "ms")
                .build();
        }
        return Health.down()
            .withDetail("status", "initializing")
            .withDetail("elapsed", System.currentTimeMillis() - startTime + "ms")
            .build();
    }

    public void markReady() {
        this.ready = true;
    }
}
```

## Step 4: Resource Profiling in CI Pipeline

```java
package com.acmecorp.orderprocessor.test.profiling;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ResourceProfiler {

    private static final long MEGABYTE = 1024L * 1024L;

    public static class ProfileResult {
        private final long maxHeapMB;
        private final long maxNativeMemoryMB;
        private final int threadCount;
        private final double cpuLoad;
        private final boolean withinLimits;
        private final String[] warnings;

        public ProfileResult(long maxHeapMB, long maxNativeMemoryMB,
                            int threadCount, double cpuLoad,
                            long memoryLimitMB, double cpuLimit) {
            this.maxHeapMB = maxHeapMB;
            this.maxNativeMemoryMB = maxNativeMemoryMB;
            this.threadCount = threadCount;
            this.cpuLoad = cpuLoad;
            
            long totalMemory = maxHeapMB + maxNativeMemoryMB;
            java.util.List<String> warningList = new java.util.ArrayList<>();
            
            if (totalMemory > memoryLimitMB * 0.8) {
                warningList.add("WARNING: Memory usage (" + totalMemory + "MB) exceeds 80% of limit ("
                    + memoryLimitMB + "MB)");
            }
            if (totalMemory > memoryLimitMB) {
                warningList.add("CRITICAL: Memory usage (" + totalMemory + "MB) exceeds limit ("
                    + memoryLimitMB + "MB) — OOM risk");
            }
            if (cpuLoad > cpuLimit * 0.8) {
                warningList.add("WARNING: CPU load (" + cpuLoad + ") exceeds 80% of limit ("
                    + cpuLimit + ")");
            }
            
            this.withinLimits = warningList.isEmpty();
            this.warnings = warningList.toArray(new String[0]);
        }

        public boolean isWithinLimits() { return withinLimits; }
        public String[] getWarnings() { return warnings; }
        public long getMaxHeapMB() { return maxHeapMB; }
        public long getMaxNativeMemoryMB() { return maxNativeMemoryMB; }
        public int getThreadCount() { return threadCount; }
    }

    public ProfileResult profile(long memoryLimitMB, double cpuLimit) throws InterruptedException {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

        // Simulate thread pool load
        int threadCount = 200;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                latch.countDown();
                // Simulate work: consume some heap
                byte[] allocation = new byte[1024 * 100]; // 100 KB per thread
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        long maxHeapMB = heapUsage.getUsed() / MEGABYTE;
        long maxNativeMB = heapUsage.getUsed() / MEGABYTE; // approximate

        double cpuLoad = threadBean.getThreadCount() / (double) Runtime.getRuntime().availableProcessors();

        return new ProfileResult(maxHeapMB, maxNativeMB, threadBean.getThreadCount(), cpuLoad, memoryLimitMB, cpuLimit);
    }

    public static void main(String[] args) throws InterruptedException {
        ResourceProfiler profiler = new ResourceProfiler();
        ProfileResult result = profiler.profile(4096, 2.0);

        System.out.println("=== Resource Profile ===");
        System.out.println("Max heap: " + result.getMaxHeapMB() + " MB");
        System.out.println("Native memory: " + result.getMaxNativeMemoryMB() + " MB");
        System.out.println("Thread count: " + result.getThreadCount());
        System.out.println("Within limits: " + result.isWithinLimits());

        for (String warning : result.getWarnings()) {
            System.err.println(warning);
        }

        if (!result.isWithinLimits()) {
            System.exit(1); // Fail the CI build
        }
    }
}
```

## Step 5: CPU Throttling Alerts

```yaml
# prometheus/rules/cpu-throttling.yml
groups:
  - name: cpu_throttling
    rules:
      - alert: ContainerCPUThrottlingHigh
        expr: |
          sum(increase(container_cpu_cfs_throttled_seconds_total{container!=""}[5m]))
          / sum(increase(container_cpu_cfs_periods_total{container!=""}[5m]))
          > 0.25
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Container CPU throttling > 25% for {{ $labels.container }}"

      - alert: ContainerCPUThrottlingCritical
        expr: |
          sum(increase(container_cpu_cfs_throttled_seconds_total{container!=""}[5m]))
          / sum(increase(container_cpu_cfs_periods_total{container!=""}[5m]))
          > 0.50
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "Container CPU throttling > 50% for {{ $labels.container }}"
          runbook: "https://runbook.acmecorp.com/cpu-throttling"
```

## Step 6: Canary Deployment Configuration

```yaml
# argocd/order-processor.yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: order-processor
spec:
  destination:
    namespace: production
    server: https://kubernetes.default.svc
  source:
    repoURL: https://github.com/acmecorp/order-processor
    targetRevision: HEAD
    path: kubernetes/overlays/production
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
      allowEmpty: false
    retry:
      limit: 2
      backoff:
        duration: 5s
        factor: 2
        maxDuration: 3m
  # Progressive delivery with canary
  strategy:
    canary:
      steps:
        - setWeight: 20
        - pause: {duration: 5m}
        - setWeight: 50
        - pause: {duration: 5m}
        - setWeight: 100
```

## Step 7: Verifying Probe and Resource Configuration

```bash
# Verify resource limits
kubectl describe pod -n production -l app=order-processor | grep -A 5 "Limits"

# Check startup probe configuration
kubectl get pod -n production -l app=order-processor -o jsonpath='{.items[0].spec.containers[0].startupProbe}'

# Verify no CrashLoopBackOff
kubectl get pods -n production -l app=order-processor | grep -v Running

# Check CPU throttling
kubectl exec -n production deployment/order-processor -- cat /sys/fs/cgroup/cpu/cpu.stat

# Verify memory usage
kubectl top pod -n production -l app=order-processor

# Check JVM container awareness
kubectl exec -n production deployment/order-processor -- java -XX:+PrintFlagsFinal 2>&1 | grep -i "usecontainer"
```

## Verification Commands

```bash
# Check all pods are stable
kubectl get pods -n production -l app=order-processor
kubectl describe deployments -n production order-processor | grep Replicas

# Check resource usage
kubectl top pod -n production -l app=order-processor

# Check probe status
kubectl get events -n production --field-selector involvedObject.name=order-processor

# Verify no OOM kills
kubectl describe pod -n production -l app=order-processor | grep -c "OOMKilled"

# Check CPU throttling metrics
kubectl exec -n production deployment/order-processor -- cat /sys/fs/cgroup/cpu/cpu.stat | grep throttled

# Verify metrics-server
kubectl top node
```
