# OOP Basics — Mental Models

## Model 1: The Blueprint Factory

A class is a blueprint. An object is a house built from that blueprint. One blueprint can produce many identical houses, but each house has its own occupants (state). The blueprint describes what every house will have (doors, windows), but doesn't say who lives there.

## Model 2: The Office Building

Classes are departments. Each department has shared resources (static fields — the office coffee machine) and per-employee resources (instance fields — your desk). Static methods are like the company-wide memo system. Instance methods are like your personal phone line.

## Model 3: The Construction Crew

A constructor is a construction crew that builds the object. Overloaded constructors are different construction plans: "build a house" vs "build a house with a pool and garage." `this` is the foreman pointing at the house being built.

## Model 4: The String Name Tag

`this` is like putting on a name tag that says "ME." When a method runs, its `this` name tag points to the object it was called on. `myCar.start()` — inside `start()`, `this` = `myCar`. Static methods don't have a name tag — they work alone, without an object.
