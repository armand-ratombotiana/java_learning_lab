# Generics — Mental Models

## Model 1: The Custom Stamp

A generic class is a rubber stamp with a blank label. You specify the label when you use it: `Stamp<String>` creates a stamp that always prints strings. `Stamp<Integer>` creates one that prints numbers. The stamping mechanism (the class logic) stays the same — only the material changes.

## Model 2: The Parking Garage

A generic method `List<T>` is a parking garage. You decide what kind of vehicles (type) it holds. `List<Car>` is a car-only garage. You cannot park a Truck in a `List<Car>` (compile-time check). When you retrieve a vehicle, you know it's a Car without looking.

Wildcards are different parking rules:
- `List<? extends Vehicle>`: You can take vehicles out (they're at least Vehicles), but you can't put new ones in (you don't know which subtype)
- `List<? super Car>`: You can park Cars in (and subtypes), but when you take them out they might be any Vehicle or Object

## Model 3: The Vaccine Vial

`T` is a type parameter — like a vial waiting to be filled with a specific vaccine. `Box<T>` is the vial. `Box<String>` is filled with "string vaccine." The manufacturing process (class logic) is the same; only the contents differ.

## Model 4: The Funnel (PECS)

PECS is a funnel: producers extend (wide mouth pour in), consumers super (narrow mouth pour out).

- Producer `? extends T`: You can only read from it (extract). Like taking items off a conveyor belt.
- Consumer `? super T`: You can only write to it (insert). Like dropping items into a collection bin.
- If you need both read and write, you need the exact type — no funnel, direct connection.

## Model 5: The Contract Template

A generic interface `Comparable<T>` is a contract template. It says "I know how to compare myself to another T." When a class signs `implements Comparable<String>`, it fills in "I compare myself to String objects." The contract is type-specific, but the template (interface) is generic.
