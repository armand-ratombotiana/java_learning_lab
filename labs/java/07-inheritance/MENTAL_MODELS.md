# Inheritance — Mental Models

## Model 1: The Family Tree

A subclass is a child who inherits traits from parents (superclass). The child has everything the parent has (fields, methods) plus their own additions. A parent trait that's marked `final` cannot be changed by any child. The `super` keyword is like saying "as my parent would do it."

## Model 2: The Organizational Chart

At a company, `Employee` defines standard behavior (clock in, get paid). `Manager extends Employee` inherits those behaviors and adds `manageTeam()`. `CEO extends Manager` inherits everything plus `setCompanyDirection()`. The `Object` class is like the "person" at the root — every employee is a person.

## Model 3: The Russian Doll 2.0

Object creation is like opening Russian dolls. To create a `Manager`, you first create the `Employee` inside, then wrap it with manager features. Constructor calls go from the outermost visible doll to the inmost: `new Manager()` calls `Employee()` calls `Object()`.

## Model 4: The Wiki Page

Inheritance is like a wiki page hierarchy. A `Vehicle` page says "has wheels, moves, uses fuel." The `Car` page inherits from `Vehicle` and adds "has 4 wheels, has steering wheel, seats 5." The `Motorcycle` page inherits from `Vehicle` and overrides "has 2 wheels." Looking up "wheels" on the `Car` page finds it on the `Vehicle` page.
