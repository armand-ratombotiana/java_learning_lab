# Polymorphism — Visual Guide

## Runtime Polymorphism

```
Animal a = new Dog();

Compiler: "a has type Animal"
    └── Checks: Animal has sound() → OK
    └── Generates: invokevirtual Animal.sound()

Runtime: "a points to a Dog object"
    └── Looks up Dog's vtable entry for sound()
    └── Executes Dog.sound(): "Woof!"
```

## Virtual Method Dispatch

```
Call: a.sound()

    ┌─ a's klass ○──────────────────┐
    │            │                   │
    ▼            ▼                   │
┌──────┐   ┌───────────┐            │
│ Dog  │   │ Dog vtable│            │
│object│──→├───────────┤            │
└──────┘   │ toString  │── Object   │
           │ hashCode  │── Object   │
           │ equals    │── Object   │
           │ eat()     │── Animal   │
           │ sound() ──┤── Dog ◄────┘  ← selected!
           │ bark()    │── Dog
           └───────────┘
```

## Compile-Time vs Runtime Binding

```
Compile-time (Overloading):            Runtime (Overriding):
print(Object obj)                       Animal a = new Dog();
print(String str)                       a.sound(); // always Dog.sound()
   ↑                                        ↑
   print("hello") — selects               Determined by actual object type
   print(String) at compile time           at runtime
```

## Polymorphic Collection

```
List<Animal> animals = [Dog, Cat, Bird]
                           │     │     │
for (Animal a : animals)   │     │     │
    a.sound()              │     │     │
                           │     │     │
                      ┌────┘     │     └──────┐
                      ▼          ▼            ▼
                   Dog.sound  Cat.sound   Bird.sound
                   "Woof!"    "Meow!"     "Chirp!"
```

## Covariant Return Type

```
class Animal {
    Animal reproduce() { return new Animal(); }
}

class Dog extends Animal {
    @Override
    Dog reproduce() { return new Dog(); }  // returns subtype
}

Dog d = new Dog();
Dog puppy = d.reproduce();  // no cast needed!
```
