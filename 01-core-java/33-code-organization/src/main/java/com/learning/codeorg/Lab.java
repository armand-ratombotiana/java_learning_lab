package com.learning.codeorg;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Code Organization Lab ===\n");

        packageStructure();
        modularDesign();
        layeredArchitecture();
        solidPrinciples();
        dependencyInjection();
    }

    static void packageStructure() {
        System.out.println("--- Package Organization ---");

        for (var p : List.of(
            "config/ - Configuration, beans, properties",
            "controller/ - REST controllers, DTOs",
            "service/ - Business logic, interfaces",
            "repository/ - Data access, DAOs",
            "model/ - Domain entities, value objects",
            "dto/ - Request/response data transfer objects",
            "exception/ - Custom exceptions",
            "util/ - Helper classes, constants",
            "mapper/ - Entity-DTO converters"))
            System.out.println("  " + p);

        System.out.println("\n  Organization strategies:");
        for (var s : List.of("By Layer: controller/, service/, repository/ (simple, high coupling)",
                "By Feature: user/, order/, payment/ (high cohesion, low coupling)",
                "Hybrid: user/UserController, order/OrderService (best of both)"))
            System.out.println("  " + s);
    }

    static void modularDesign() {
        System.out.println("\n--- Modular Design (Java Modules) ---");

        System.out.println("  module-info.java directives:");
        for (var d : List.of("exports: makes package accessible",
                "requires: declares dependency", "opens: opens for reflection (Hibernate)",
                "provides ... with: service provider", "uses: service loader",
                "transitive: transitive dependency"))
            System.out.println("  " + d);

        System.out.println("\n  Module dependency graph:");
        System.out.println("""
            com.company.web -> com.company.service -> com.company.persistence -> com.company.core
                                              -> java.base
            """);
    }

    static void layeredArchitecture() {
        System.out.println("\n--- Layered Architecture ---");

        for (var l : List.of("Presentation: HTTP handling, validation, response formatting",
                "Application: use cases, orchestration, transactions",
                "Domain: business rules, entities, value objects (self-contained)",
                "Infrastructure: database, messaging, external APIs"))
            System.out.println("  " + l);

        System.out.println("\n  Dependency rule: outer layers depend on inner layers only.");
        System.out.println("  Presentation -> Application -> Domain <- Infrastructure");

        System.out.println("\n  Call flow:");
        System.out.println("""
            Controller (POST /api/orders)
              -> OrderService.createOrder()
                -> validate business rules (Domain)
                -> OrderRepository.save(order) (Infrastructure)
              <- returns OrderResponse DTO""");
    }

    static void solidPrinciples() {
        System.out.println("\n--- SOLID Principles ---");

        for (var p : List.of(
            "SRP: one reason to change (split UserService into UserRepo + EmailService + Validator)",
            "OCP: open for extension, closed for modification (Strategy pattern)",
            "LSP: subtypes substitutable (Square/REctangle -> separate abstractions)",
            "ISP: many specific interfaces > one fat interface",
            "DIP: depend on abstractions, not concretions (inject repository interface)"))
            System.out.println("  " + p);

        System.out.println("\n  SOLID in package structure:");
        for (var a : List.of("SRP: each package has one responsibility",
                "OCP: service interfaces allow new implementations",
                "ISP: small focused interfaces (Reader, Writer)",
                "DIP: services depend on repo interfaces"))
            System.out.println("  " + a);
    }

    static void dependencyInjection() {
        System.out.println("\n--- Dependency Injection ---");

        interface PaymentGateway { boolean charge(double amt); }

        class StripeGateway implements PaymentGateway {
            public boolean charge(double amt) { return amt > 0; }
        }

        class OrderService {
            final PaymentGateway pg;
            OrderService(PaymentGateway pg) { this.pg = pg; }
            String pay(double amt) {
                return pg.charge(amt) ? "Paid $" + amt + " via " + pg.getClass().getSimpleName() : "Failed";
            }
        }

        class Container {
            final Map<Class<?>, Object> beans = new HashMap<>();
            <T> void reg(Class<T> type, T instance) { beans.put(type, instance); }
            <T> T get(Class<T> type) { return type.cast(beans.get(type)); }
        }

        var c = new Container();
        c.reg(PaymentGateway.class, new StripeGateway());
        var svc = new OrderService(c.get(PaymentGateway.class));
        System.out.println("  " + svc.pay(99.99));

        System.out.println("\n  DI patterns:");
        for (var p : List.of("Constructor (required deps, recommended)",
                "Setter (optional deps, use sparingly)",
                "Field (avoid, hard to test)",
                "Service Locator (anti-pattern)"))
            System.out.println("  " + p);

        System.out.println("\n  Naming conventions:");
        for (var n : List.of("Package: com.company.project.module",
                "Class: PascalCase (OrderService)", "Method: camelCase (findById)",
                "Constant: UPPER_SNAKE (MAX_RETRY)", "Test: OrderServiceTest"))
            System.out.println("  " + n);
    }
}
