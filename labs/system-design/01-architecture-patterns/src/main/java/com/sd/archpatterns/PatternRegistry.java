package com.sd.archpatterns;

import java.util.*;

public class PatternRegistry {

    public enum PatternCategory {
        STRUCTURAL, INTEGRATION, DATA, DEPLOYMENT
    }

    public record ArchitecturePattern(
        String name,
        PatternCategory category,
        String description,
        List<String> useCases,
        List<String> tradeoffs
    ) {}

    public static class PatternLibrary {
        private final List<ArchitecturePattern> patterns = new ArrayList<>();

        public PatternLibrary add(ArchitecturePattern p) {
            patterns.add(p);
            return this;
        }

        public void printAll() {
            System.out.println("=== Architecture Pattern Library ===\n");
            for (ArchitecturePattern p : patterns) {
                System.out.println("[" + p.category() + "] " + p.name());
                System.out.println("  " + p.description());
                System.out.println("  Use cases: " + String.join(", ", p.useCases()));
                System.out.println("  Tradeoffs: " + String.join(", ", p.tradeoffs()));
                System.out.println();
            }
        }

        public List<ArchitecturePattern> findByCategory(PatternCategory cat) {
            return patterns.stream().filter(p -> p.category() == cat).toList();
        }
    }

    public static void main(String[] args) {
        PatternLibrary lib = new PatternLibrary();

        lib.add(new ArchitecturePattern("Layered Architecture", PatternCategory.STRUCTURAL,
            "Organizes code into layers (presentation, business, persistence)",
            List.of("Enterprise apps", "Simple web apps"),
            test("Monolithic deployment", "Limited scalability")));

        lib.add(new ArchitecturePattern("Microservices", PatternCategory.STRUCTURAL,
            "Decomposes application into independently deployable services",
            List.of("Large teams", "Complex domains", "Cloud-native apps"),
            test("Operational complexity", "Network latency")));

        lib.add(new ArchitecturePattern("Event-Driven Architecture", PatternCategory.INTEGRATION,
            "Components communicate via events through a message broker",
            List.of("Real-time systems", "Decoupled systems"),
            test("Eventual consistency", "Debugging difficulty")));

        lib.add(new ArchitecturePattern("CQRS", PatternCategory.DATA,
            "Separates read and write models for different optimization",
            List.of("Complex queries", "High read/write disparity"),
            test("Increased complexity", "Eventual consistency")));

        lib.add(new ArchitecturePattern("Hexagonal Architecture", PatternCategory.STRUCTURAL,
            "Core business logic isolated from external concerns via ports/adapters",
            List.of("Testable design", "Framework-independent apps"),
            test("More interfaces", "Learning curve")));

        lib.printAll();
    }

    private static List<String> test(String... args) { return Arrays.asList(args); }
}
