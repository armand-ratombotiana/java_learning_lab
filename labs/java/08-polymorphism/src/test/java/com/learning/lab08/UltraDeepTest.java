package com.learning.lab08;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Ultra-deep tests for Polymorphism lab.
 * Explores dynamic dispatch, inline caching, bridge methods.
 */
class UltraDeepTest {

    @Test
    void dynamicDispatchWithInterface() {
        interface Animal { String speak(); }
        class Dog implements Animal {
            public String speak() { return "woof"; }
        }
        class Cat implements Animal {
            public String speak() { return "meow"; }
        }
        Animal a = new Cat();
        assertEquals("meow", a.speak(), "Dynamic dispatch resolves to Cat.speak()");
        a = new Dog();
        assertEquals("woof", a.speak(), "Dynamic dispatch resolves to Dog.speak()");
    }

    @Test
    void bridgeMethodForCovariantReturn() {
        abstract static class Parent {
            abstract Number getValue();
        }
        static class Child extends Parent {
            @Override
            Integer getValue() { return 42; }
        }
        Number val = new Child().getValue();
        assertInstanceOf(Integer.class, val);
        assertEquals(42, val.intValue());
    }
}
