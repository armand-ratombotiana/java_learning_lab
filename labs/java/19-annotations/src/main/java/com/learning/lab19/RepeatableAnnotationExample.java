package com.learning.lab19;

import java.lang.annotation.*;

/**
 * Demonstrates repeatable annotations and @Inherited meta-annotation.
 */
public class RepeatableAnnotationExample {

    public static void showRepeatable() {
        System.out.println("=== Repeatable & Inherited Annotations ===");

        Class<?> clazz = AnnotatedService.class;

        if (clazz.isAnnotationPresent(TaskInfoContainer.class)) {
            TaskInfoContainer container = clazz.getAnnotation(TaskInfoContainer.class);
            for (TaskInfo task : container.value()) {
                System.out.println("  Task: " + task.name() + " (priority=" + task.priority() + ")");
            }
        }
    }
}

@Repeatable(TaskInfoContainer.class)
@Retention(RetentionPolicy.RUNTIME)
@interface TaskInfo {
    String name();
    int priority() default 5;
}

@Retention(RetentionPolicy.RUNTIME)
@interface TaskInfoContainer {
    TaskInfo[] value();
}

@TaskInfo(name = "Setup", priority = 1)
@TaskInfo(name = "Process", priority = 3)
@TaskInfo(name = "Cleanup", priority = 5)
class AnnotatedService {
    public void execute() {
        System.out.println("  Service executing all tasks");
    }
}
