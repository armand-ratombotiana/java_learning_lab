# Module 02: OOP Concepts - Mini Project

**Project Name**: Library Management System  
**Difficulty Level**: Intermediate  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Design and implement a system that demonstrates the four pillars of Object-Oriented Programming: Encapsulation, Inheritance, Polymorphism, and Abstraction.

## 📝 Requirements

### 1. Abstraction & Inheritance
- Create an abstract class `Item` with:
  - Encapsulated fields: `id` (String), `title` (String), `isCheckedOut` (boolean).
  - A constructor to initialize `id` and `title`.
  - An abstract method `calculateLateFee(int daysLate)`.
  - Concrete methods `checkOut()` and `returnItem()`.
- Create concrete subclasses: `Book` and `DVD`.
  - `Book` has an extra field `author`. Late fee is $0.50 per day.
  - `DVD` has an extra field `director`. Late fee is $1.00 per day.

### 2. Interfaces
- Create an interface `Searchable`.
  - Define methods: `boolean matchesTitle(String keyword)` and `String getDetails()`.
  - Make `Item` implement `Searchable`.

### 3. Encapsulation & Composition
- Create a `Library` class.
  - Maintain a private `List<Item>` or an array to store inventory.
  - Provide public methods: `addItem(Item item)`, `searchItems(String keyword)`, `checkOutItem(String id)`, and `returnItem(String id)`.

### 4. Polymorphism
- The `searchItems` method in `Library` should iterate through the collection of `Item` references, calling `.getDetails()` polymorphically on each object and printing the result if it matches.

---

## 💡 Solution Blueprint

1. Start by defining the `Searchable` interface.
2. Build the abstract `Item` class, ensuring fields are `private` or `protected` and providing public getters/setters.
3. Extend `Item` into `Book` and `DVD`, providing implementations for the abstract `calculateLateFee` method and overriding `getDetails`.
4. Build the `Library` class as the central manager.
5. Create a `Main` class to instantiate the `Library`, add several books and DVDs, and demonstrate searching and checking out items.