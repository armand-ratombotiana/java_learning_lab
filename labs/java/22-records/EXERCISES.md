# Exercises: Records

## Exercise 1: Basic Record Declaration

Create a record called `Book` with components: `String title`, `String author`, `String isbn`, and `int yearPublished`. Then create a few instances and demonstrate that equals/hashCode/toString work correctly.

## Exercise 2: Validation in Compact Constructor

Create a record `Temperature` with a single `double celsius` component. Add a compact constructor that throws `IllegalArgumentException` if the temperature is below absolute zero (-273.15°C). Add instance methods `toFahrenheit()` and `toKelvin()`.

## Exercise 3: Defensive Copying

Create a record `Student` with components `String name` and `List<Integer> grades`. Ensure the list is defensively copied in the constructor and accessor so that external code cannot modify the internal grades.

## Exercise 4: Local Records in Streams

Write a method that takes a list of `Transaction` objects (with `String category` and `double amount` fields) and returns a summary report. Use a local record `CategorySummary` with fields `String category` and `double total` to group and summarize transactions by category.

## Exercise 5: Records with Sealed Types

Model a simple payroll system:
- Define a sealed interface `PayrollItem` with implementations for `SalaryEmployee` (monthly salary), `HourlyEmployee` (hourly rate and hours worked), and `Contractor` (fixed contract amount)
- Each implementation is a record
- Write a method that calculates total pay for a list of `PayrollItem` using pattern matching switch

## Exercise 6: Builder Pattern for Records

Create a record `EmailMessage` with components: `List<String> to`, `List<String> cc`, `String subject`, `String body`, `boolean isHtml`. Implement a builder pattern (either nested class or static factory methods) to construct instances with sensible defaults.

## Exercise 7: Recursive Record

Create a record `TreeNode<T>` that represents a node in a binary tree with components `T value`, `TreeNode<T> left`, and `TreeNode<T> right`. Implement methods to:
- Count the number of nodes
- Find the depth of the tree
- Perform in-order traversal

## Exercise 8: Database Query Result Mapping

Create a record `EmployeeRecord` mapping to a database table with fields id, name, department, salary, and hireDate. Write a method using Spring JDBC Template or plain JDBC that returns `List<EmployeeRecord>` from a SQL query.

## Exercise 9: Record with Custom Constructor and Factory Methods

Create a record `Fraction` with numerator and denominator (both int). Include:
- A compact constructor that reduces the fraction to lowest terms
- A non-canonical constructor that takes only the numerator (denominator = 1)
- Static factory methods: `valueOf(int, int)`, `parse(String)` (e.g., "3/4")
- Methods: `add(Fraction)`, `multiply(Fraction)`, `toDouble()`

## Exercise 10: Records for Event Sourcing

Design an event-sourced system for a bank account:
- Define records for domain events: `AccountOpened`, `MoneyDeposited`, `MoneyWithdrawn`, `AccountClosed`
- Each event record has a timestamp and account ID
- Write a method that replays a list of events to compute the current account balance
- Use pattern matching on the events (sealed hierarchy with record implementations)

## Solutions

Create the solutions in a `solutions/` directory under this lab. Try to solve each exercise without looking at reference implementations. Exercises 1-4 build core skills, 5-7 introduce advanced patterns, and 8-10 are real-world scenarios combining records with other Java features.
