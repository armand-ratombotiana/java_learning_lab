# Exercises: Optional

## Exercise 1: Basic Optional Operations

Write a method `Optional<String> getConfig(String key)` that returns an Optional from a Properties map. Then write methods to:
1. Return the value or "default" if not found
2. Return the value as integer if possible (parseInt), or empty otherwise
3. Return the value converted to uppercase if present, or "N/A" if absent

## Exercise 2: Chaining Optional Operations

Create a `User` class with `Optional<Address>`, `Address` with `Optional<String> getCity()`. Write a method `Optional<String> getCity(User user)` that safely extracts the city name from a potentially null user with potentially null address.

## Exercise 3: Validation with Optional

Write a validation chain for a `Payment` record:
- Amount must be positive
- Currency must be USD, EUR, or GBP
- Method must be credit_card, paypal, or bank_transfer
Use `Optional.filter()` for each validation and return `Optional<Payment>` for valid payments or empty for invalid.

## Exercise 4: Optional with or() Chain

Simulate a multi-level cache:
- `Optional<String> findL1Cache(String key)` — always returns empty
- `Optional<String> findL2Cache(String key)` — return Optional.of("l2:value") for keys starting with "a"
- `Optional<String> findDatabase(String key)` — return Optional.of("db:value") for any key
Write a method `getValue(String key)` that tries L1, then L2, then database.

## Exercise 5: Optional in Streams

Given a list of strings (some may be null), write a method that:
1. Wraps each string in Optional (null becomes empty)
2. Filters empty optionals using `Optional::stream` (Java 9+)
3. Maps to uppercase
4. Collects to a list
Also write the same method using the Java 8 approach (filter + map).

## Exercise 6: Refactoring Null Checks

Take the following code and refactor it to use Optional:
```java
public String getFullAddress(Company company) {
    if (company == null) return "No company";
    Address addr = company.getAddress();
    if (addr == null) return "No address";
    String street = addr.getStreet();
    String city = addr.getCity();
    String zip = addr.getZip();
    if (street == null) street = "";
    if (city == null) city = "";
    if (zip == null) zip = "";
    return street + ", " + city + " " + zip;
}
```

## Exercise 7: Service with Optional Responses

Create a `BookService` with methods:
- `Optional<Book> findByIsbn(String isbn)`
- `Optional<Book> findByTitle(String title)`
- `List<Book> findByAuthor(String author)`
- `Optional<Book> findCheapestBook()`

Write a method `List<Book> findBooks(String query)` that searches by isbn first, then title, then falls back to author search.

## Exercise 8: Avoiding Anti-Patterns

Identify and fix the anti-patterns in this code:
```java
public class ConfigService {
    private Optional<String> configValue = Optional.empty();
    
    public void setConfig(Optional<String> value) {
        this.configValue = value;
    }
    
    public String getConfig() {
        if (configValue.isPresent()) {
            return configValue.get();
        }
        return null;
    }
}
```

## Exercise 9: Optional and Exception Handling

Write a method `Optional<Integer> parseIntSafe(String input)` that returns `Optional.empty()` for invalid inputs instead of throwing. Then write a method that parses a list of strings and returns only the valid integers using `flatMap(Optional::stream)`.

## Exercise 10: Real-World Scenario

Design a `ShoppingCart` system where:
- Items can be added and removed
- `Optional<Item> findItem(String sku)` returns item or empty if not in cart
- A discount code can be applied: `Optional<Discount> applyDiscount(String code)`
- The total is calculated as sum of item prices minus discount if present
- `Optional<CheckoutResult> checkout()` returns the result or empty if cart is empty
