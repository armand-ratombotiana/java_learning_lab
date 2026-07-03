# Edge Cases & Pitfalls: Advanced Polymorphism

Advanced polymorphism can lead to obscure bugs, especially when mixing static binding, dynamic binding, and generics.

## 1. The "Hidden" Field Trap
*   **The Scenario**: You declare a field in a superclass and a field with the exact same name in a subclass.
*   **The Pitfall**: Unlike instance methods, **fields are not polymorphic**. They are resolved based on the *reference type* at compile time (Static Binding).
    ```java
    class Parent { String name = "Parent"; }
    class Child extends Parent { String name = "Child"; }
    
    Parent p = new Child();
    System.out.println(p.name); // Prints "Parent"!
    ```
*   **Mitigation**: Never hide fields (also known as shadowing). Always use private fields with polymorphic getter methods, because methods *are* resolved dynamically.

## 2. Overloading vs. Overriding Confusion
*   **The Scenario**: You intend to override a method from a superclass, but you slightly change the parameter type.
    ```java
    class Parent { void process(Number n) {} }
    class Child extends Parent { void process(Integer i) {} } // This is Overloading, NOT Overriding!
    ```
*   **The Pitfall**: If you call `process()` via a `Parent` reference passing an `Integer`, the `Parent`'s method is called, not the `Child`'s. The compiler statically binds the method call to `process(Number)` because the reference is `Parent`.
*   **Mitigation**: **Always use the `@Override` annotation**. If you use it in the example above, the compiler will throw an error, warning you that you are not actually overriding anything.

## 3. The `ClassCastException` in Bridge Methods
*   **The Scenario**: You use generics with inheritance, forcing the compiler to generate a bridge method (e.g., `setData(Object data)`).
*   **The Pitfall**: If you bypass generic type checking (e.g., using raw types or unsafe casts), you can pass the wrong type into the bridge method. The bridge method attempts to cast the `Object` to the specific generic type (e.g., `String`), which will throw a `ClassCastException` at runtime in code you didn't even write!
*   **Mitigation**: Never use raw types. Always resolve compiler warnings related to unchecked casts.

## 4. Default Method Diamond Problem
*   **The Scenario**: Interface `A` and Interface `B` both define a default method `doWork()`. Interface `C` extends both `A` and `B` but doesn't override `doWork()`.
*   **The Pitfall**: The code will not compile. Java forces you to resolve the ambiguity.
*   **Mitigation**: You must override the method in `C` or the implementing class. If you want to use `A`'s implementation, you must explicitly call `A.super.doWork()`.

## 5. Constructor Polymorphism (The Initialization Trap)
*   **The Scenario**: You call a polymorphic (overridable) method from inside a superclass constructor.
*   **The Pitfall**: The superclass constructor runs *before* the subclass constructor. If the superclass calls an overridden method, the subclass's implementation executes before the subclass's fields have been initialized.
    ```java
    class Parent {
        Parent() { print(); } // Danger!
        void print() { System.out.println("Parent"); }
    }
    class Child extends Parent {
        private String name = "Child";
        void print() { System.out.println(name.length()); } // Throws NullPointerException! 'name' is not initialized yet.
    }
    ```
*   **Mitigation**: **Never call overridable methods from a constructor**. Only call `private` or `final` methods during object initialization.