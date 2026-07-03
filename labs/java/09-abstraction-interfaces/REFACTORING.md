# Refactoring Abstraction & Interfaces

## Extract Interface

Before: Class with methods that could form a contract.
After: Define interface with those methods, class implements it.

## Replace Abstract Class with Interface + Default Methods

Before: Abstract class with mostly abstract methods and no fields.
After: Interface with default methods for any shared implementation.

## Use Interface for Plugin Architecture

Before: Hardcoded implementations, switch/if to choose.
After: Interface discovery with `ServiceLoader` or dependency injection.

## Add @FunctionalInterface

Before: Interface with exactly one abstract method — useful for lambdas.
After: Add `@FunctionalInterface` annotation, use lambdas where implemented.

## Remove Marker Interface if Annotation Exists

Before: `class MyClass implements Serializable { }`
After: Consider `@Retention(RUNTIME)` annotation instead (for custom markers).

## Pull Default Methods Up

Common default implementation in multiple interface implementations? Add as default method in the interface.
