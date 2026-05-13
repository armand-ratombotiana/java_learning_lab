# Debugging OOP Issues in Java

## Common Failure Scenarios

### Inheritance and Method Override Issues

Improper method overriding is one of the most frequent OOP bugs. The @Override annotation catches many issues, but subtle bugs still occur when method signatures appear correct but semantics differ. A common mistake is defining a method with covariant return type that breaks the Liskov Substitution Principle. If a subclass method accepts more parameters or throws fewer exceptions than the parent, code that works with the parent class may fail when using the subclass.

Static methods cannot be overridden in Java—they are hidden, not overridden. Calling a static method on a reference of the parent type executes the parent's implementation even when the object is an instance of the subclass. This confuses developers who expect polymorphic behavior from what appears to be an instance method call.

### Polymorphism Bugs

Polymorphic calls dispatch to the correct method at runtime, but this can surprise developers when they pass a collection of base class references to code that expects specific subclass behavior. The ClassCastException occurs when you attempt a downcast that isn't valid. Using `instanceof` before casting is essential, but the cleaner approach is to use generics or design patterns that avoid casting altogether.

The "diamond problem" in multiple inheritance doesn't directly apply to Java classes (since Java doesn't allow multiple class inheritance), but interface default methods can create ambiguity. When two interfaces provide default methods and a class implements both, the compiler requires you to explicitly override the method to resolve the conflict.

### Stack Trace Examples

**ClassCastException from improper casting:**
```
Exception in thread "main" java.lang.ClassCastException: com.example.Dog cannot be cast to com.example.Cat
    at com.example.Veterinary.examine(Veterinary.java:15)
    at com.example.Main.main(Main.java:22)
```

**AbstractMethodError from incomplete implementation:**
```
java.lang.AbstractMethodError: com.example.ConcreteTask.execute()V
    at com.example.TaskRunner.run(TaskRunner.java:10)
    at com.example.Main.main(Main.java:8)
```

**IllegalAccessError from accessing private method:**
```
java.lang.IllegalAccessError: Class com.example.SubClass cannot access private method 'process' in com.example.ParentClass
    at com.example.SubClass.invokeProcess(SubClass.java:25)
```

## Debugging Techniques

### Identifying Inheritance Issues

When inheritance hierarchies behave unexpectedly, create a simple test that invokes each method on both parent and child instances to verify behavior. Use the debugger to inspect the actual runtime type (`object.getClass()`) at each call site. For polymorphism issues, add logging in the parent class constructor—it gets called before the subclass constructor, which sometimes surprises developers expecting different initialization order.

For method resolution problems, use `javap -c` to inspect the bytecode and verify that virtual method dispatch is configured correctly. The `invokevirtual` opcode indicates runtime dispatch, while `invokestatic` or `invokespecial` indicates compile-time resolution.

### Design Pattern Verification

The Template Method pattern frequently causes issues when hooks (methods designed to be overridden) are confused with required methods. Document which methods are hooks and which are template methods that call hooks.

The Strategy pattern can cause ClassNotFoundException if the strategy implementation isn't on the classpath. This often happens when strategy implementations are in separate modules or JARs that aren't included in the runtime classpath.

## Best Practices

Always annotate overridden methods with @Override. This triggers a compile-time error if the method signature doesn't match a parent method, catching typos and signature mistakes immediately. It also documents the intent clearly to future readers.

Prefer composition over inheritance unless you truly need is-a semantics. Inheritance creates tight coupling—the subclass is bound to the parent's implementation details. Favor interfaces and delegation for flexible designs that can change behavior without deep hierarchy changes.

Make inheritance hierarchies shallow. Deep hierarchies become difficult to understand and maintain. Each level of inheritance multiplies the complexity of reasoning about behavior. If you find yourself going more than two or three levels deep, reconsider the design.

Use sealed classes (Java 17+) when you control the inheritance hierarchy. This explicitly declares which classes can extend a base class, catching accidental extension at compile time rather than runtime errors later.