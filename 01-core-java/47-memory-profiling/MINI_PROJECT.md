# Module 47: Memory Profiling & Analysis - Mini Project

**Project Name**: The Memory Leak Detective  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Simulate a severe memory leak using `ThreadLocal` variables and static caches. Use the JDK CLI tools (`jmap`) and Eclipse MAT (Memory Analyzer Tool) to locate the exact cause of the leak in a pseudo-production environment.

## 📝 Requirements

### Core Features

1. **The Leaking Application**:
   - Create a Spring Boot application or a plain Java multi-threaded application.
   - **Leak 1 (Static Cache)**: Create a `UserService` with a `private static final Map<String, byte[]> userCache = new HashMap<>();`. Write an endpoint or loop that continuously adds 1MB byte arrays to this cache but never removes them.
   - **Leak 2 (ThreadLocal)**: Create a `UserContextHolder` with a `private static final ThreadLocal<UserContext> context = new ThreadLocal<>();`. In a web endpoint, populate this context but intentionally "forget" to call `context.remove()` in the `finally` block.

2. **Triggering the Crash**:
   - Run the application from the command line, severely limiting the heap space to accelerate the crash:
     `java -Xmx100m -XX:+HeapDumpOnOutOfMemoryError -jar myapp.jar`
   - Hit the endpoints rapidly using a script (e.g., `curl` in a bash loop or JMeter) until the application throws an `OutOfMemoryError: Java heap space`.

3. **Forensic Analysis (MAT)**:
   - Locate the `.hprof` file generated in the working directory.
   - Download and install **Eclipse Memory Analyzer (MAT)**.
   - Open the heap dump in MAT.
   - Run the **Leak Suspects Report**.

4. **The Report**:
   - Document the findings.
   - Screenshot the "Dominator Tree" showing the `HashMap` holding all the byte arrays.
   - Find the ThreadLocal leak and document which thread pool thread was holding onto the `UserContext`.

---

## 💡 Solution Blueprint

**The ThreadLocal Leak Simulation**:
```java
@RestController
public class LeakController {

    private static final ThreadLocal<byte[]> threadLocalLeak = new ThreadLocal<>();

    @GetMapping("/leak")
    public String causeLeak() {
        // Allocate 5MB per request. Because TomCat reuses threads, 
        // this ThreadLocal is never cleared, quickly exhausting the heap.
        threadLocalLeak.set(new byte[5 * 1024 * 1024]);
        
        // ❌ Missing: threadLocalLeak.remove();
        return "Leaked 5MB";
    }
}
```