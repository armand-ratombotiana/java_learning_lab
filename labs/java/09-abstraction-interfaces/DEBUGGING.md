# Debugging Abstraction & Interfaces

## Abstract Method Not Implemented

"Class is not abstract and does not override abstract method":
1. Check the abstract class/interface for all abstract methods
2. Implement any missing methods
3. Or declare the class abstract
4. Check method signature — must match exactly

## Default Method Conflict

"Class inherits conflicting default methods":
1. Two interfaces both define the same default method
2. Override the method in the implementing class
3. Use `InterfaceName.super.methodName()` to select one

## Unexpected Default Method Behavior

1. Check if the class overrides the default method
2. Check if a superclass method takes priority (class wins over interface)
3. Concrete methods in classes always win over default methods

## Lambda Debugging

Hard to debug because lambdas don't show meaningful stack frames:
1. Extract lambda to a named method
2. Use method reference instead: `MyClass::method`
3. Add logging inside lambda body
4. Set breakpoints inside lambda (most modern IDEs support this)
5. Use `-Djdk.internal.lambda.dumpProxyClasses` to see generated classes
