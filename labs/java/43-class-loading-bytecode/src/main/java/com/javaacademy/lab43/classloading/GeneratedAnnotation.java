package com.javaacademy.lab43.classloading;

import java.lang.annotation.*;

/**
 * Marker annotation used by AsmTransformer to demonstrate
 * ASM-based bytecode annotation injection.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GeneratedAnnotation {
    String value() default "";
}
