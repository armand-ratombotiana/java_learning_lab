package com.architecture.deep.lab06;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class C4Lab {
    public static void main(String[] args) {
        var context = new C4ModelBuilder()
            .addContext("Online Banking System", "Allows customers to manage accounts and payments.")
            .addActor("Customer", "A bank customer")
            .addActor("Admin", "System administrator")
            .addExternalSystem("Payment Gateway", "Processes payments")
            .addExternalSystem("Fraud Detection", "Detects suspicious activity")
            .build();

        System.out.println(context.render());
    }
}

record C4Element(String id, String name, String description, String type, Set<String> relationships) {}

class C4ModelBuilder {
    private final Map<String, C4Element> elements = new LinkedHashMap<>();

    C4ModelBuilder addContext(String name, String description) {
        return add("sys-" + name.hashCode(), name, description, "System", Set.of());
    }

    C4ModelBuilder addActor(String name, String description) {
        return add("actor-" + name.hashCode(), name, description, "Person", Set.of("sys-Online Banking System".hashCode() + ""));
    }

    C4ModelBuilder addExternalSystem(String name, String description) {
        return add("ext-" + name.hashCode(), name, description, "External System",
            Set.of("sys-Online Banking System".hashCode() + ""));
    }

    private C4ModelBuilder add(String id, String name, String description, String type, Set<String> relationships) {
        elements.put(id, new C4Element(id, name, description, type, relationships));
        return this;
    }

    C4Model build() { return new C4Model(Map.copyOf(elements)); }
}

record C4Model(Map<String, C4Element> elements) {
    String render() {
        var sb = new StringBuilder();
        sb.append("C4 Context Diagram: Online Banking System\n");
        sb.append("=" .repeat(45)).append("\n\n");
        elements.values().forEach(el -> {
            sb.append("[").append(el.type()).append("] ").append(el.name()).append("\n");
            sb.append("  ").append(el.description()).append("\n");
            if (!el.relationships().isEmpty()) {
                sb.append("  Relationships: ").append(String.join(", ", el.relationships())).append("\n");
            }
            sb.append("\n");
        });
        return sb.toString();
    }
}
