package com.learning.lab07;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Ultra-deep tests for Inheritance lab.
 * Explores diamond problem, constructor chaining, initialization order.
 */
class UltraDeepTest {

    @Test
    void defaultMethodDiamondResolution() {
        interface A { default String m() { return "A"; } }
        interface B extends A { default String m() { return "B"; } }
        interface C extends A {}
        class Impl implements B, C {}
        Impl impl = new Impl();
        assertEquals("B", impl.m(), "Most-specific default wins");
    }

    @Test
    void classBeatsDefaultMethod() {
        interface A { default String m() { return "interface"; } }
        class Base { public String m() { return "class"; } }
        class Child extends Base implements A {}
        Child c = new Child();
        assertEquals("class", c.m(), "Class implementation beats interface default");
    }

    @Test
    void constructorChainingOrder() {
        StringBuilder sb = new StringBuilder();
        class Parent {
            Parent() { sb.append("P"); }
        }
        class Child extends Parent {
            Child() { sb.append("C"); }
        }
        new Child();
        assertEquals("PC", sb.toString(), "Parent constructor runs before Child");
    }
}
