# Pass-By-Value Internals

## 🔬 The JVM Stack
To truly understand parameter passing, we must look at how the JVM manages memory via the Call Stack.

Every thread in the JVM has its own Stack. When a method is invoked, a new **Stack Frame** is pushed onto the top of the Stack. This frame contains the method's Local Variable Array.

### Scenario: Passing a Primitive
```java
void main() {
    int a = 10;
    modify(a);
}
void modify(int x) {
    x = 20;
}
```
1. **`main` Frame**: The local variable array contains `a = 10`.
2. **Method Call**: `modify(a)` is called. A new frame is pushed.
3. **Copying**: The JVM copies the value `10` from `main`'s frame into the `modify` frame's local variable array at index 0 (representing `x`).
4. **Execution**: `modify` changes its local `x` to `20`. The `main` frame is untouched.
5. **Return**: The `modify` frame is popped and destroyed. `a` in `main` is still `10`.

### Scenario: Passing an Object Reference
```java
void main() {
    Dog d = new Dog("Fido");
    modify(d);
}
void modify(Dog param) {
    param.setName("Rex");
    param = new Dog("Spot");
}
```
1. **Heap Allocation**: `new Dog("Fido")` allocates memory on the Heap (e.g., at address `0x999`).
2. **`main` Frame**: The local variable `d` holds the value `0x999`.
3. **Method Call**: `modify(d)` is called. A new frame is pushed.
4. **Copying**: The JVM copies the value `0x999` from `main`'s frame into the `modify` frame's variable `param`.
5. **Mutation (`param.setName`)**: The `modify` method follows the address `0x999` to the Heap and changes the internal state of the object. Since `main`'s `d` also points to `0x999`, `main` sees this change.
6. **Reassignment (`param = new`)**: A new object is created on the Heap at address `0x888`. The `modify` frame updates its local variable `param` to hold `0x888`. 
   - *Crucially*, `main`'s variable `d` still holds `0x999`. It was not updated.
7. **Return**: The `modify` frame is popped. The object at `0x888` ("Spot") is now unreachable and becomes eligible for Garbage Collection. The original object at `0x999` is now named "Rex".

## 🛡️ C++ vs Java (True Pass-By-Reference)
To understand what Java *isn't*, look at C++.
In C++, you can explicitly pass by reference using the `&` operator: `void modify(Dog& param)`.
In this case, the compiler does not make a copy of the memory address. The parameter `param` becomes an **alias** for the original variable `d`. If you reassign `param` inside the method, the original variable `d` in the caller is actually reassigned. This is impossible in Java.