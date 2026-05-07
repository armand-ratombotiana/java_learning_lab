package com.learning.micronaut.data;

public class MicronautDataLab {

    public static void main(String[] args) {
        System.out.println("=== Micronaut Data Lab ===\n");

        System.out.println("1. Micronaut Controller Example:");
        System.out.println("   @Controller(\"/products\")");
        System.out.println("   public class ProductController {");
        System.out.println("       @Get public List<Product> getAll() { ... }");
        System.out.println("       @Get(\"/{id}\") public Product getById(Long id) { ... }");
        System.out.println("       @Post public HttpResponse<Product> create(Product p) { ... }");
        System.out.println("   }");

        System.out.println("\n2. Micronaut Data Features:");
        System.out.println("   - Compile-time dependency injection");
        System.out.println("   - GraalVM native image support");
        System.out.println("   - Reactive programming with RxJava");
        System.out.println("   - @Introspected for DTOs");
        System.out.println("   - @Repository for data access");

        System.out.println("\n=== Micronaut Data Lab Complete ===");
    }
}