package com.learning.cqrs;

public class CQRSLab {

    public static void main(String[] args) {
        System.out.println("=== CQRS Pattern Lab ===\n");

        System.out.println("1. CQRS Concept:");
        System.out.println("   Command Side: Handles Create, Update, Delete operations");
        System.out.println("   Query Side: Handles Read operations");
        System.out.println("   Separation allows independent scaling and optimization");

        System.out.println("\n2. Command Handler:");
        System.out.println("   class CreateProductCommand { String name; String description; double price; }");
        System.out.println("   class ProductCommandHandler {");
        System.out.println("       String handle(CreateProductCommand cmd) {");
        System.out.println("           String id = generateId();");
        System.out.println("           repository.save(new Product(id, cmd.name, cmd.price));");
        System.out.println("           return id;");
        System.out.println("       }");
        System.out.println("   }");

        System.out.println("\n3. Query Handler:");
        System.out.println("   class ProductQueryHandler {");
        System.out.println("       List<ProductDTO> getAll() { return readStore.getAll(); }");
        System.out.println("       ProductDTO getById(String id) { return readStore.getById(id); }");
        System.out.println("   }");

        System.out.println("\n=== CQRS Pattern Lab Complete ===");
    }
}