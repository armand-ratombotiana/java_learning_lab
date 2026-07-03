# Deep Dive: Variance & Covariance

## 1. The Core Problem: Subtyping with Containers
In Java, inheritance is straightforward: if `Dog` extends `Animal`, then `Dog` is a subtype of `Animal`. You can assign a `Dog` to an `Animal` reference.
```java
Animal a = new Dog(); // Valid
```
However, when we put these types inside containers (like Arrays or Generics), the subtyping rules become complex. If `Dog` is an `Animal`, is a `List<Dog>` a subtype of `List<Animal>`?

The answer depends on the concept of **Variance**. Variance defines how subtyping between more complex types (containers) relates to subtyping between their component types.

There are three types of variance:
1.  **Covariant**: Preserves the subtyping relationship.
2.  **Contravariant**: Reverses the subtyping relationship.
3.  **Invariant**: Ignores the subtyping relationship entirely.

## 2. Arrays: Covariant (and Dangerous)
In Java, arrays are **Covariant**. This means if `Dog` is a subtype of `Animal`, then `Dog[]` is a subtype of `Animal[]`.

```java
Dog[] dogs = new Dog[10];
Animal[] animals = dogs; // Valid! Arrays are covariant.

animals[0] = new Cat(); // Compiles fine. Runtime crash: ArrayStoreException!
```
**Why is this bad?** Because `animals` is actually pointing to a `Dog[]` in memory. You cannot put a `Cat` into a `Dog[]`. The compiler allows it, but the JVM catches it at runtime and throws an `ArrayStoreException`. This violates type safety.

## 3. Generics: Invariant (and Safe)
To fix the array problem, Java designers made Generics **Invariant**. 
This means `List<Dog>` is **NOT** a subtype of `List<Animal>`. They have absolutely no subtyping relationship.

```java
List<Dog> dogs = new ArrayList<>();
// List<Animal> animals = dogs; // COMPILE ERROR! Generics are invariant.
```
**Why is this good?** It prevents the runtime crash we saw with arrays. If the compiler allowed the assignment, you could add a `Cat` to the `animals` list, which would corrupt the underlying `Dog` list. Invariance guarantees type safety at compile time.

## 4. Wildcards: Restoring Flexibility
Invariance makes generics very safe, but highly inflexible. If you write a method `printNames(List<Animal> list)`, you cannot pass a `List<Dog>` to it, even though printing dogs is perfectly safe!

To regain flexibility, Java uses **Use-Site Variance** via Wildcards (`?`). We met this briefly in Module 53 (PECS), but now we will formalize the theory.

### Covariance (`? extends T`)
Creates a covariant relationship. `List<Dog>` becomes a subtype of `List<? extends Animal>`.
*   **Meaning**: "A list of some unknown type that is an Animal or a subclass of Animal."
*   **Restriction**: Because the compiler doesn't know the *exact* type (it could be a list of Cats), it forbids you from **writing** anything into the list (except `null`). It is strictly a **Producer** of data.

```java
public void printNames(List<? extends Animal> animals) {
    for (Animal a : animals) { // Safe to read as Animal
        System.out.println(a.getName());
    }
    // animals.add(new Dog()); // ERROR! Cannot write.
}
```

### Contravariance (`? super T`)
Creates a contravariant relationship (reverses the hierarchy). `List<Animal>` becomes a subtype of `List<? super Dog>`.
*   **Meaning**: "A list of some unknown type that is a Dog or a superclass of Dog (up to Object)."
*   **Restriction**: Because it could be a list of `Animal` or `Object`, you can safely **write** a `Dog` into it. However, when you **read** from it, the compiler can only guarantee that the object is an `Object`, not a `Dog`. It is strictly a **Consumer** of data.

```java
public void addDog(List<? super Dog> list) {
    list.add(new Dog()); // Safe to write a Dog
    
    // Dog d = list.get(0); // ERROR! Might be an Animal or Object.
    Object obj = list.get(0); // Safe, but not very useful.
}
```

## 5. Declaration-Site vs Use-Site Variance
It is important to note that Java uses **Use-Site Variance**. The variance (`? extends` or `? super`) is defined at the point where the generic type is *used* (e.g., in a method parameter).

Other languages (like Scala or Kotlin) support **Declaration-Site Variance**. You can define the class itself as covariant (`class List<out T>`) or contravariant (`class Comparator<in T>`). Java does not support this, forcing developers to use wildcards everywhere flexibility is needed.