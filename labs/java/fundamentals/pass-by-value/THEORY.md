# Pass-By-Value Theory & Intuition

## 💡 The Golden Rule of Java
There is one absolute, unbreakable rule in Java regarding method arguments:
**Java is strictly Pass-By-Value. It is never Pass-By-Reference.**

This is one of the most misunderstood concepts among Java beginners, primarily because the terminology is confusing when dealing with Objects.

## 🔄 What is Pass-By-Value?
When you pass a variable to a method, Java makes a **copy** of that variable's value and passes the copy to the method.
- The method receives the copy.
- If the method reassigns the copy to a new value, the original variable in the caller method is completely unaffected.

### Primitives
For primitive types (`int`, `double`, `boolean`), the "value" is the actual number.
If you have `int x = 5` and pass `x` to a method, Java copies the number `5`. If the method changes its copy to `10`, the original `x` remains `5`.

### Objects
This is where the confusion starts. 
When you create an object `Person p = new Person("Alice")`, the variable `p` does **not** hold the object itself. The object lives on the Heap. The variable `p` holds a **reference** (a memory address, like `0x1234ABCD`) pointing to where the object lives on the Heap.

When you pass `p` to a method, Java is still Pass-By-Value. What is the value? The value is the memory address.
Java makes a **copy of the memory address** and passes it to the method.

## 🎭 The Illusion of Pass-By-Reference
Because the method receives a copy of the memory address, both the original variable and the method's parameter are pointing to the **exact same object on the Heap**.

- **Mutation**: If the method uses its copy of the address to reach into the Heap and change the object's internal state (e.g., `person.setName("Bob")`), the caller will see the change. This makes it *look* like Pass-By-Reference.
- **Reassignment**: However, if the method tries to assign its parameter to a completely new object (e.g., `person = new Person("Charlie")`), it only changes its *copy* of the address to point to the new object. The caller's variable still points to the original object ("Bob"). This proves Java is Pass-By-Value.