package com.learning.lab.module68;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 68: Spring Modulith Lab ===\n");

        System.out.println("1. Spring Modulith Overview:");
        System.out.println("   - Application modules as units");
        System.out.println("   - Enforced module boundaries");
        System.out.println("   - Internal package visibility");
        System.out.println("   - Documented application structure");

        System.out.println("\n2. Module Structure:");
        System.out.println("   - Package-per-module convention");
        System.out.println("   - Api package: public API");
        System.out.println("   - Internal: implementation details");
        System.out.println("   - Named modules (DDD context)");

        System.out.println("\n3. Module Dependencies:");
        System.out.println("   - Strict dependency rules");
        System.out.println("   - No cyclic dependencies");
        System.out.println("   - Allowed: domain -> domain");
        System.out.println("   - Forbidden: application -> other application");

        System.out.println("\n4. Integration Testing:");
        System.out.println("   - @ApplicationModuleTest");
        System.out.println("   - Slice tests per module");
        System.out.println("   - Test isolation");
        System.out.println("   - JUnit 5 integration");

        System.out.println("\n5. Application Events:");
        System.out.println("   - InternalApplicationEvents");
        System.out.println("   - Cross-module communication");
        System.out.println("   - Event audit trail");
        System.out.println("   - Simplified event handling");

        System.out.println("\n6. Modulithic Arch Unit:");
        System.out.println("   - Verify module structure");
        System.out.println("   - Package dependency rules");
        System.out.println("   - Static analysis integration");

        System.out.println("\n7. Documentation:");
        System.out.println("   - Asciidoctor integration");
        System.out.println("   - Module diagram generation");
        System.out.println("   - Architecture documentation");

        System.out.println("\n=== Spring Modulith Lab Complete ===");
    }
}