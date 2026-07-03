# Inheritance — Exercises

## Exercise 1: Animal Hierarchy
Create Animal → Mammal → Dog/Cat hierarchy. Each adds fields and overrides `speak()`.

## Exercise 2: Vehicle Hierarchy
Vehicle → Car/Truck/Motorcycle. Fields: make, model, year, maxSpeed. Override `getInfo()`.

## Exercise 3: Employee Hierarchy
Employee (name, salary) → Manager (bonus) → Executive (stockOptions). Override `getCompensation()`.

## Exercise 4: Shape Hierarchy
Shape → Circle (radius), Rectangle (w, h), Triangle (base, height). Abstract `getArea()` method.

## Exercise 5: Override equals/hashCode
Create Person class with name, email. Override equals and hashCode using `Objects.equals/hash`.

## Exercise 6: Bank Account Hierarchy
Account → SavingsAccount (interestRate), CheckingAccount (overdraftLimit). Override `withdraw()`.

## Exercise 7: Game Character
Character → Warrior (strength), Mage (mana), Archer (agility). Override `attack()`.

## Exercise 8: final Class
Create a final class `MathConstants` with PI, E. Show it can't be extended.

## Exercise 9: Constructor Chaining
Create A → B → C hierarchy. Show constructor execution order with print statements.

## Exercise 10: Template Method
Abstract `DataExporter` with `export()` template method calling abstract steps.

## Exercise 11: IS-A vs HAS-A
Refactor a design where inheritance was used but composition is more appropriate.

## Exercise 12: super Keyword
Create a subclass that overrides a method but calls `super.method()` as part of its implementation.
