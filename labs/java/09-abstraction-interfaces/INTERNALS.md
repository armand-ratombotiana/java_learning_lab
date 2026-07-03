# Abstraction & Interfaces — Internal Mechanics

## Interface Class File Structure

An interface's class file has `ACC_INTERFACE` (0x0200) and `ACC_ABSTRACT` (0x0400) flags set. Methods have `ACC_PUBLIC` and `ACC_ABSTRACT` flags. Default methods additionally have `ACC_PUBLIC` (no ACC_ABSTRACT).

## Default Method Dispatch

Default methods in interfaces use `invokespecial` (not `invokeinterface`) when called via `InterfaceName.super.method()`. This bypasses the normal vtable dispatch and directly invokes the interface's implementation.

## Functional Interface Detection

`@FunctionalInterface` is a compile-time annotation. The compiler verifies the interface has exactly one abstract method (excluding default, static, and Object methods). The annotation is optional — any SAM interface is a functional interface.

## Lambda Desugaring

Lambda expressions are compiled using `invokedynamic`:
```java
List<String> list = ...;
list.forEach(s -> System.out.println(s));
// Compiled as: invokedynamic #lambda$0
```

The bootstrap method links to a synthetic implementation class. The lambda object implements the functional interface and delegates to the method handle.

## Interface Static Method Invocation

Static interface methods use `invokestatic` — same as static class methods. They cannot be inherited or overridden by implementing classes.
