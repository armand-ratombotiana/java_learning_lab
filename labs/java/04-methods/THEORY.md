# Methods — Theoretical Foundation

## Method Anatomy

A method declaration has six components:

```java
[access-modifier] [optional-modifier] returnType methodName(parameterList) [throws exceptionList] {
    // method body
}
```

1. **Access modifier**: `public`, `protected`, `private`, or package-private (default)
2. **Optional modifier**: `static`, `final`, `abstract`, `synchronized`, `native`, `strictfp`
3. **Return type**: Any type (primitive, reference, or `void`)
4. **Method name**: camelCase, verb-phrase convention (`getName`, `calculateTotal`)
5. **Parameter list**: Comma-separated type-name pairs
6. **Exception list**: Checked exceptions the method might throw

## Method Signature

The method signature consists of the method name and parameter types. The return type is NOT part of the signature for overloading purposes.

```java
public int add(int a, int b)     // Signature: add(int, int)
public double add(double a, double b) // Signature: add(double, double) — different
```

## Pass-by-Value

Java is strictly pass-by-value:
- For primitives, a copy of the value is passed
- For references, a copy of the reference is passed (the object is not copied)

```java
void setValue(int x) { x = 10; }  // Caller's variable unchanged
void setName(Person p) { p.setName("New"); }  // Object modified
void swap(Person a, Person b) {  // References are swapped within method only
    Person temp = a;
    a = b;
    b = temp;
}
```

## Method Overloading

Multiple methods with the same name but different parameter lists. Compiler determines which to call at compile time based on argument types.

```java
public int sum(int a, int b) { return a + b; }
public int sum(int a, int b, int c) { return a + b + c; }
public double sum(double a, double b) { return a + b; }
```

## Varargs

Variable-length argument lists using `Type...` syntax:

```java
public void printAll(String... messages) {
    for (String msg : messages) {
        System.out.println(msg);
    }
}
// Called as: printAll("a", "b", "c") or printAll()
```

Varargs compiles to array creation at each call site. Only one varargs parameter allowed, and it must be the last.

## Recursion

A method that calls itself. Requires a base case (terminating condition) and a recursive step. Each recursive call adds a stack frame.

```java
public int factorial(int n) {
    if (n <= 1) return 1;       // Base case
    return n * factorial(n - 1); // Recursive step
}
```

Deep recursion can cause `StackOverflowError`. Java does not have tail-call optimization.

## Method Inlining

The JIT compiler may inline small methods (replacing the call with the method body). This is a key optimization — methods marked `final`, `static`, or `private` are easier to inline because there's no dynamic dispatch.

## Access Control

| Modifier | Class | Package | Subclass | World |
|----------|-------|---------|----------|-------|
| `public` | Y | Y | Y | Y |
| `protected` | Y | Y | Y | N |
| default | Y | Y | N | N |
| `private` | Y | N | N | N |
