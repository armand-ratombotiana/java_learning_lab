package com.learning.functional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Implementations of traditional GoF patterns using modern Functional Programming.
 */
public class FunctionalDesignPatterns {

    // =========================================================================
    // 1. Strategy Pattern (Using Lambdas instead of Classes)
    // =========================================================================
    public interface ValidationStrategy extends Predicate<String> {}

    public static class Validator {
        private final ValidationStrategy strategy;

        public Validator(ValidationStrategy strategy) {
            this.strategy = strategy;
        }

        public boolean validate(String s) {
            return strategy.test(s);
        }
    }

    public static void demonstrateStrategy() {
        // Instead of creating IsNumeric, IsLowerCase classes, we use lambdas:
        Validator numericValidator = new Validator(s -> s.matches("\\d+"));
        Validator lowerCaseValidator = new Validator(s -> s.matches("[a-z]+"));

        System.out.println("Is '1234' numeric? " + numericValidator.validate("1234"));
        System.out.println("Is 'abcd' lowercase? " + lowerCaseValidator.validate("abcd"));
    }

    // =========================================================================
    // 2. Command Pattern
    // =========================================================================
    public static class Macro {
        private final List<Runnable> commands = new ArrayList<>();

        public void record(Runnable action) {
            commands.add(action);
        }

        public void run() {
            commands.forEach(Runnable::run);
        }
    }

    public static class Editor {
        public void open() { System.out.println("Editor opened"); }
        public void save() { System.out.println("Editor saved"); }
        public void close() { System.out.println("Editor closed"); }
    }

    public static void demonstrateCommand() {
        Editor editor = new Editor();
        Macro macro = new Macro();
        
        // Passing method references as commands
        macro.record(editor::open);
        macro.record(editor::save);
        macro.record(editor::close);
        
        macro.run();
    }

    // =========================================================================
    // 3. Execute Around Method Pattern
    // =========================================================================
    // Safely manages resource opening and closing automatically
    public static class Resource {
        private Resource() { System.out.println("Resource allocated."); }
        
        public void operate(String op) {
            System.out.println("Performing operation: " + op);
        }
        
        private void close() { System.out.println("Resource closed and cleaned up."); }

        // The functional wrapper
        public static void use(Consumer<Resource> consumer) {
            Resource resource = new Resource();
            try {
                consumer.accept(resource);
            } finally {
                resource.close();
            }
        }
    }

    public static void demonstrateExecuteAround() {
        Resource.use(resource -> {
            resource.operate("Read Data");
            resource.operate("Process Data");
            // Resource will be closed automatically even if an exception occurs here
        });
    }
}