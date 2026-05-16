# Mental Models for Lambda Expressions

## Model 1: Anonymous Function

Think of a lambda as a "function without a name" that can be passed around:

```
Traditional method:  Named function - "doSomething()"
Lambda:              Anonymous function - (a, b) -> a + b
```

The lambda is the behavior itself, not a reference to a named method.

## Model 2: Behavior as Value

Just as you can pass an Integer or String, you can pass behavior:

```java
// Passing data (normal)
print(42);

// Passing behavior (lambda)
execute(x -> x * 2);
```

## Model 3: Method Shortcut

Lambdas are like "shorthand methods":

```java
// Long form
(x, y) -> { return x + y; }

// Short form (single expression)
(x, y) -> x + y

// Method reference equivalent
this::addMethod
```

## Model 4: Functional Interface Connection

Every lambda connects to a functional interface:

```
Lambda:  x -> x * 2
Target:  Function<T, R>
         └── Implements apply(T) → R
```

## Model 5: Pipeline of Operations

Think of Stream operations as a pipeline:

```
Input → [filter] → [map] → [collect] → Output
         └───────┴───────┴───────────┘
          All use lambdas
```

## Key Insight

Lambdas enable **behavior parameterization**—passing what to do, not just what to operate on.