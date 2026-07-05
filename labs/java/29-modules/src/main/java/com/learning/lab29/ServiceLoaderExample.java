package com.learning.lab29;

import java.util.*;

/**
 * Demonstrates the ServiceLoader pattern for service provider discovery.
 * In a real module setup, providers are configured via 'provides' in module-info.java.
 */
public class ServiceLoaderExample {

    public static void showServiceLoaderConcept() {
        System.out.println("=== ServiceLoader Concept ===");

        List<GreetingProvider> providers = new ArrayList<>();
        providers.add(new EnglishGreeting());
        providers.add(new SpanishGreeting());
        providers.add(new FrenchGreeting());

        System.out.println("Available greeting services:");
        for (GreetingProvider provider : providers) {
            System.out.println("  " + provider.greet("World"));
        }

        System.out.println();
        System.out.println("In a real module setup, ServiceLoader.load(GreetingProvider.class)");
        System.out.println("would discover providers declared in module-info.java:");
        System.out.println("  provides com.learning.lab29.GreetingProvider");
        System.out.println("      with com.learning.lab29.EnglishGreeting;");
    }
}

interface GreetingProvider {
    String greet(String name);
}

class EnglishGreeting implements GreetingProvider {
    @Override
    public String greet(String name) {
        return "Hello, " + name + "!";
    }
}

class SpanishGreeting implements GreetingProvider {
    @Override
    public String greet(String name) {
        return "¡Hola, " + name + "!";
    }
}

class FrenchGreeting implements GreetingProvider {
    @Override
    public String greet(String name) {
        return "Bonjour, " + name + "!";
    }
}
