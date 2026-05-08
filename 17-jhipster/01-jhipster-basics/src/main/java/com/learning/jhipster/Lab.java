package com.learning.jhipster;

import java.util.*;
import java.util.stream.*;

public class Lab {
    record Field(String name, String type, boolean required, int maxLength) {}
    record Entity(String name, String tableName, List<Field> fields, String dto, String serviceImpl) {}

    static class JHipsterGenerator {
        private final List<Entity> entities = new ArrayList<>();

        Entity buildEntity(String name) {
            var fields = new ArrayList<Field>();
            var e = new Entity(name, name.toLowerCase() + "s", fields, "mapstruct", "serviceImpl");
            entities.add(e);
            return e;
        }

        Entity addField(Entity e, String name, String type, boolean required, int maxLength) {
            var updated = new Entity(e.name, e.tableName,
                new ArrayList<>(e.fields) {{ add(new Field(name, type, required, maxLength)); }},
                e.dto, e.serviceImpl);
            entities.set(entities.indexOf(e), updated);
            return updated;
        }

        void generate() {
            for (var e : entities) {
                System.out.println("Generating entity: " + e.name);
                generateEntityClass(e);
                generateRepository(e);
                generateService(e);
                generateController(e);
                generateDTO(e);
                generateAngularComponent(e);
                System.out.println();
            }
        }

        void generateEntityClass(Entity e) {
            System.out.println("  @Entity @Table(name=\"" + e.tableName + "\")");
            System.out.println("  public class " + e.name + " implements Serializable {");
            for (var f : e.fields) {
                System.out.println("    @Column(name=\"" + f.name + "\", length=" + f.maxLength + ")");
                System.out.println("    private " + f.type + " " + camel(f.name) + ";");
            }
            System.out.println("  }");
        }

        void generateRepository(Entity e) {
            System.out.println("  @Repository public interface " + e.name + "Repository extends JpaRepository<" + e.name + ", Long> {}");
        }

        void generateService(Entity e) {
            System.out.println("  @Service @Transactional public class " + e.name + "ServiceImpl implements " + e.name + "Service { }");
        }

        void generateController(Entity e) {
            System.out.println("  @RestController @RequestMapping(\"/api/" + e.tableName + "\") public class " + e.name + "Resource { }");
        }

        void generateDTO(Entity e) {
            System.out.println("  @Data public class " + e.name + "DTO { ");
            for (var f : e.fields) System.out.println("    private " + f.type + " " + camel(f.name) + ";");
            System.out.println("  }");
        }

        void generateAngularComponent(Entity e) {
            System.out.println("  // Angular: ng generate component " + camel(e.name));
            System.out.println("  // Route: { path: '" + e.tableName + "', component: " + e.name + "Component }");
        }

        String camel(String s) {
            var parts = s.split("_");
            return parts[0] + IntStream.range(1, parts.length)
                .mapToObj(i -> parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1))
                .collect(Collectors.joining());
        }
    }

    public static void main(String[] args) {
        System.out.println("=== JHipster: Entity Generation & Scaffolding ===");
        var gen = new JHipsterGenerator();
        var product = gen.buildEntity("Product");
        product = gen.addField(product, "name", "String", true, 100);
        product = gen.addField(product, "price", "BigDecimal", true, 20);
        product = gen.addField(product, "description", "String", false, 500);
        var order = gen.buildEntity("Order");
        order = gen.addField(order, "order_date", "Instant", true, 0);
        order = gen.addField(order, "total", "BigDecimal", true, 20);
        gen.generate();

        System.out.println("--- Liquibase Changelog (Simulated) ---");
        System.out.println("  databaseChangeLog:");
        for (var e : gen.entities) {
            System.out.println("  - changeSet: createTable_" + e.tableName);
            System.out.println("      createTable tableName: " + e.tableName);
            for (var f : e.fields) {
                var colType = switch (f.type()) {
                    case "String" -> "VARCHAR(" + f.maxLength() + ")";
                    case "BigDecimal" -> "DECIMAL(19,2)";
                    case "Instant" -> "TIMESTAMP";
                    default -> "VARCHAR(255)";
                };
                System.out.println("        column name: " + f.name() + " type: " + colType);
            }
        }
    }
}
