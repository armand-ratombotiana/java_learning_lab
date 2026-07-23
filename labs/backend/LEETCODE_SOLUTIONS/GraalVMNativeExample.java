// GraalVMNativeExample.java — GraalVM Native Image
// Demonstrates GraalVM native compilation configuration and patterns

package com.backend.academy.leetcode;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;
import org.graalvm.nativeimage.hosted.RuntimeSerialization;
import org.graalvm.nativeimage.ImageInfo;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ClassPathResource;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Demonstrates GraalVM native image compilation for Spring Boot backend services.
 *
 * Key GraalVM Concepts:
 * - AOT (Ahead-of-Time) compilation: Java bytecode → native machine code
 * - Closed-world assumption: all classes known at build time
 * - Reflection: must be explicitly registered via hints
 * - Runtime performance: faster startup, lower memory, but slower peak throughput
 * - Substrate VM: GraalVM's native image runtime
 */

// ============================================================
// Reflective Access Registration
// ============================================================

/**
 * Classes that use reflection must be registered for native image.
 */
@TypeHint(
    types = {
        GraalVMNativeExample.class,
        NativeGreetingService.class,
        ReflectiveBean.class
    },
    access = {TypeHint.Access.PUBLIC_METHODS, TypeHint.Access.PUBLIC_CONSTRUCTORS}
)
@NativeHint(
    trigger = GraalVMNativeExample.class,
    options = {
        "--initialize-at-build-time=com.backend.academy.leetcode",
        "--report-unsupported-elements-at-runtime",
        "--allow-incomplete-classpath",
        "-H:+PrintAnalysisCallTree",
        "-H:+StackTrace"
    }
)
@ImportRuntimeHints(NativeRuntimeHints.class)
@SpringBootApplication
public class GraalVMNativeExample {
    public static void main(String[] args) {
        // Check if running as native image
        if (ImageInfo.inImageCode()) {
            System.out.println("Running as GraalVM native image");
            System.out.println("Image build time: " + ImageInfo.getImageVersion());
        } else {
            System.out.println("Running on JVM (not native image)");
        }
        SpringApplication.run(GraalVMNativeExample.class, args);
    }
}

// ============================================================
// Runtime Hints Registration
// ============================================================

class NativeRuntimeHints implements org.springframework.aot.hint.RuntimeHintsRegistrar {
    @Override
    public void registerHints(
            org.springframework.aot.hint.RuntimeHints hints,
            ClassLoader classLoader) {

        // Register reflection for specific classes
        hints.reflection().registerType(
            org.springframework.aot.hint.TypeReference.of(ReflectiveBean.class),
            builder -> builder.withMembers(
                org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_METHODS,
                org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                org.springframework.aot.hint.MemberCategory.DECLARED_FIELDS
            )
        );

        // Register resources
        hints.resources().registerPattern("messages/*");
        hints.resources().registerPattern("config/*.properties");
        hints.resources().registerPattern("static/*");
        hints.resources().registerPattern("templates/*");

        // Register serialization
        hints.serialization().registerType(
            org.springframework.aot.hint.TypeReference.of(NativeGreetingService.class)
        );

        // Register proxies
        hints.proxies().registerJdkProxy(
            org.springframework.aot.hint.TypeReference.of(GreetingInterface.class)
        );
    }
}

// ============================================================
// GraalVM Feature Implementation
// ============================================================

/**
 * Custom GraalVM native image feature for backend service registration.
 * This runs at build time to register runtime elements.
 */
class BackendNativeFeature implements Feature {
    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        // Register reflection for service classes
        RuntimeReflection.register(GraalVMNativeExample.class);
        RuntimeReflection.register(NativeGreetingService.class);
        RuntimeReflection.register(ReflectiveBean.class);

        // Register all public methods for reflective invocation
        RuntimeReflection.register(NativeGreetingService.class.getDeclaredMethods());
        RuntimeReflection.register(ReflectiveBean.class.getDeclaredMethods());

        // Register for serialization
        RuntimeSerialization.register(GraalVMNativeExample.class);
        RuntimeSerialization.register(NativeGreetingService.class);

        // Register constructors
        try {
            RuntimeReflection.register(GraalVMNativeExample.class.getDeclaredConstructor());
            RuntimeReflection.register(NativeGreetingService.class.getDeclaredConstructor());
        } catch (NoSuchMethodException e) {
            // Constructor not found — handled by GC
        }
    }

    @Override
    public void afterAnalysis(AfterAnalysisAccess access) {
        // Verify all reachable code is registered
        System.out.println("GraalVM analysis complete. Reachable classes: "
            + access.getReachableClasses().count());
    }
}

// ============================================================
// Service with Reflective Access
// ============================================================

@Service
class NativeGreetingService {

    private static final Map<String, String> GREETINGS = Map.of(
        "en", "Hello",
        "es", "Hola",
        "fr", "Bonjour",
        "de", "Hallo",
        "ja", "Konnichiwa",
        "pt", "Ola"
    );

    /**
     * Uses reflection to invoke methods on a dynamically loaded bean.
     */
    @Reflective
    public String greetWithReflection(String name, String lang) {
        try {
            ReflectiveBean bean = new ReflectiveBean(lang);
            Method method = ReflectiveBean.class.getMethod("greet", String.class);
            return (String) method.invoke(bean, name);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Direct method call (no reflection — fully supported in native image).
     */
    public String greetDirect(String name, String lang) {
        String greeting = GREETINGS.getOrDefault(lang, "Hello");
        return greeting + ", " + name + "!";
    }

    /**
     * Read resource file (must be registered via RuntimeHintsRegistrar).
     */
    public String readGreetingTemplate(String lang) throws IOException {
        ClassPathResource resource = new ClassPathResource("messages/greeting_" + lang + ".txt");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    /**
     * Lambda expression — fully supported in native image.
     */
    public List<String> processNames(List<String> names, String lang) {
        String greeting = GREETINGS.getOrDefault(lang, "Hello");
        return names.stream()
            .map(name -> greeting + ", " + name + "!")
            .collect(Collectors.toList());
    }
}

/**
 * Bean that will be accessed via reflection.
 */
class ReflectiveBean {
    private final String lang;

    public ReflectiveBean(String lang) {
        this.lang = lang;
    }

    public String greet(String name) {
        return switch (lang) {
            case "en" -> "Hello, " + name + "!";
            case "es" -> "Hola, " + name + "!";
            case "fr" -> "Bonjour, " + name + "!";
            case "de" -> "Hallo, " + name + "!";
            case "ja" -> name + "sama, konnichiwa!";
            default -> "Hello, " + name + "!";
        };
    }

    public String getLang() { return lang; }
}

interface GreetingInterface {
    String greet(String name);
}

// ============================================================
// Native Image Build Configuration Reference
// ============================================================

/*
 * Maven configuration for GraalVM native image:
 * 
 * <plugin>
 *   <groupId>org.graalvm.buildtools</groupId>
 *   <artifactId>native-maven-plugin</artifactId>
 *   <configuration>
 *     <buildArgs>
 *       <buildArg>--initialize-at-build-time=com.backend.academy</buildArg>
 *       <buildArg>--report-unsupported-elements-at-runtime</buildArg>
 *       <buildArg>-H:+StackTrace</buildArg>
 *       <buildArg>-H:+PrintAnalysisCallTree</buildArg>
 *       <buildArg>--enable-https</buildArg>
 *       <buildArg>--enable-all-security-services</buildArg>
 *       <buildArg>-march=compatibility</buildArg>
 *     </buildArgs>
 *     <metadataRepository>
 *       <enabled>true</enabled>
 *     </metadataRepository>
 *   </configuration>
 * </plugin>
 * 
 * Build command: ./mvnw -Pnative native:compile
 * Run: ./target/backend-service
 * 
 * Advantages:
 * - Startup time: 0.1-0.5 seconds (vs 5-15 seconds for JVM)
 * - Memory: 50-100MB (vs 200-500MB for JVM)
 * - Instant scale-up for serverless (AWS Lambda, Google Cloud Run)
 * 
 * Trade-offs:
 * - Longer build time (5-15 minutes vs 30 seconds)
 * - Slower peak throughput vs JIT (just-in-time compiler)
 * - Dynamic class loading limited (no unlimited reflection)
 * - Debugging more difficult (native stack traces)
 */

// === LeetCode-Style Problems ===

/*
 * LeetCode 528: Random Pick with Weight
 * Analogy: load balancing with weighted distribution
 */
class RandomPickWithWeight {
    private int[] prefixSums;
    private Random rand;
    private int totalSum;

    public RandomPickWithWeight(int[] w) {
        this.rand = new Random();
        this.prefixSums = new int[w.length];
        int sum = 0;
        for (int i = 0; i < w.length; i++) {
            sum += w[i];
            prefixSums[i] = sum;
        }
        this.totalSum = sum;
    }

    public int pickIndex() {
        int target = rand.nextInt(totalSum) + 1;
        int left = 0, right = prefixSums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (prefixSums[mid] < target) left = mid + 1;
            else right = mid;
        }
        return left;
    }
}

/*
 * LeetCode 912: Sort an Array (QuickSort)
 * Analogy: AOT compilation sorts code paths at build time
 */
class Solution {
    public int[] sortArray(int[] nums) {
        quickSort(nums, 0, nums.length - 1);
        return nums;
    }

    private void quickSort(int[] arr, int left, int right) {
        if (left >= right) return;
        int pivot = partition(arr, left, right);
        quickSort(arr, left, pivot - 1);
        quickSort(arr, pivot + 1, right);
    }

    private int partition(int[] arr, int left, int right) {
        int pivot = arr[right];
        int i = left - 1;
        for (int j = left; j < right; j++) {
            if (arr[j] <= pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[right];
        arr[right] = temp;
        return i + 1;
    }
}

/*
 * LeetCode 227: Basic Calculator II
 */
class Calculator {
    public int calculate(String s) {
        if (s == null || s.isEmpty()) return 0;
        java.util.Stack<Integer> stack = new java.util.Stack<>();
        int num = 0;
        char sign = '+';
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            }
            if ((!Character.isDigit(c) && c != ' ') || i == s.length() - 1) {
                switch (sign) {
                    case '+' -> stack.push(num);
                    case '-' -> stack.push(-num);
                    case '*' -> stack.push(stack.pop() * num);
                    case '/' -> stack.push(stack.pop() / num);
                }
                sign = c;
                num = 0;
            }
        }
        int result = 0;
        for (int n : stack) result += n;
        return result;
    }
}
