package com.learning.lab19;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.annotation.*;

class AnnotationsUltraDeepTest {

    @Test
    void customAnnotationOnMethod() throws Exception {
        var method = AnnotatedMethods.class.getMethod("importantMethod");
        var ann = method.getAnnotation(Important.class);
        assertNotNull(ann);
        assertEquals("high", ann.level());
    }

    @Test
    void customAnnotationWithArrayValue() throws Exception {
        var method = AnnotatedMethods.class.getMethod("multiTaggedMethod");
        var tags = method.getAnnotation(Tags.class);
        assertNotNull(tags);
        assertEquals(2, tags.value().length);
    }

    @Test
    void annotationInheritedFromParent() throws Exception {
        var ann = ChildAnnotation.class.getAnnotation(InheritedAnnotation.class);
        // @Inherited annotations on parent class are inherited by subclass
        assertNotNull(ann);
    }

    @Test
    void annotationNotInheritedByDefault() throws Exception {
        var ann = ChildAnnotation.class.getAnnotation(NonInheritedAnnotation.class);
        assertNull(ann);
    }

    @Test
    void annotatedElementHasAnnotations() {
        assertTrue(AnnotatedMethods.class.isAnnotationPresent(ClassAnnotation.class));
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Important {
    String level() default "low";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface ClassAnnotation {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@interface InheritedAnnotation {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface NonInheritedAnnotation {}

@ClassAnnotation
@InheritedAnnotation
@NonInheritedAnnotation
class BaseClassWithAnnotations {}

class ChildAnnotation extends BaseClassWithAnnotations {}

class AnnotatedMethods {
    @Important(level = "high")
    public void importantMethod() {}

    @Tag("api")
    @Tag("core")
    public void multiTaggedMethod() {}
}
