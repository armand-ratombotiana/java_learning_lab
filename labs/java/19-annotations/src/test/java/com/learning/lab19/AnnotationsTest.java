package com.learning.lab19;

import org.junit.jupiter.api.*;
import java.lang.annotation.*;
import static org.junit.jupiter.api.Assertions.*;

class AnnotationsTest {

    @Test
    @DisplayName("Override annotation is present on overriding method")
    void overrideAnnotationPresent() throws Exception {
        var method = Child.class.getMethod("sayHello");
        assertTrue(method.isAnnotationPresent(Override.class));
    }

    @Test
    @DisplayName("Deprecated annotation is present on deprecated method")
    void deprecatedAnnotationPresent() throws Exception {
        var method = DeprecatedExample.class.getMethod("oldMethod");
        assertTrue(method.isAnnotationPresent(Deprecated.class));
    }

    @Test
    @DisplayName("SuppressWarnings annotation is present")
    void suppressWarningsAnnotationPresent() throws Exception {
        var method = SuppressWarningsExample.class.getMethod("uncheckedMethod");
        assertTrue(method.isAnnotationPresent(SuppressWarnings.class));
    }

    @Test
    @DisplayName("Custom annotation retains at runtime")
    void customAnnotationRetention() throws Exception {
        var field = CustomAnnotationExample.class.getDeclaredField("annotatedField");
        assertTrue(field.isAnnotationPresent(FieldAnnotation.class));
    }

    @Test
    @DisplayName("Custom annotation with default values")
    void customAnnotationDefaults() throws Exception {
        var field = CustomAnnotationExample.class.getDeclaredField("annotatedField");
        var ann = field.getAnnotation(FieldAnnotation.class);
        assertEquals("default description", ann.description());
    }

    @Test
    @DisplayName("Repeatable annotations are repeatable")
    void repeatableAnnotations() throws Exception {
        var method = RepeatableAnnotationExample.class.getMethod("repeatedMethod");
        var tags = method.getAnnotationsByType(Tag.class);
        assertEquals(3, tags.length);
    }

    @Test
    @DisplayName("AnnotationProcessor reads class annotations")
    void annotationProcessorReads() {
        assertDoesNotThrow(() -> AnnotationProcessorExample.processAnnotations(BuiltInAnnotationsExample.class));
    }
}

class Child extends BuiltInAnnotationsExample {
    @Override
    public String sayHello() {
        return "Child hello";
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface FieldAnnotation {
    String description() default "default description";
    int priority() default 0;
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Tags.class)
@interface Tag {
    String value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Tags {
    Tag[] value();
}

class RepeatableAnnotationExample {
    @Tag("java")
    @Tag("testing")
    @Tag("annotations")
    public void repeatedMethod() {}
}

class SuppressWarningsExample {
    @SuppressWarnings("unchecked")
    public void uncheckedMethod() {}
}

class DeprecatedExample {
    @Deprecated
    public void oldMethod() {}
}
