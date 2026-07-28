# Deep Dive: Java Records

## 1. Record Patterns (Java 21)

Record patterns allow destructuring records directly in pattern matching:

```java
// Before — manual extraction
if (obj instanceof Point p) {
    int x = p.x();
    int y = p.y();
}

// After — record pattern
if (obj instanceof Point(int x, int y)) {
    // x and y are directly available
}
```

### Nested Record Patterns

```java
record Address(String street, String city) {}
record Person(String name, Address address) {}

void printCity(Object obj) {
    if (obj instanceof Person(String name, Address(String street, String city))) {
        System.out.println(name + " lives in " + city);
    }
}
```

## 2. Compact Constructors

Records provide a compact syntax where you omit the parameter list:

```java
record Temperature(double celsius) {
    Temperature { // compact constructor — no parameter list
        if (celsius < -273.15) {
            throw new IllegalArgumentException("Below absolute zero");
        }
        celsius = Math.round(celsius * 10) / 10.0; // normalize
    }
}
```

Canonical, compact, and custom constructors coexist:

```java
record Range(int min, int max) {
    Range { // compact
        if (min > max) throw new IllegalArgumentException("min > max");
    }
    Range(int max) { this(0, max); } // custom constructor
}
```

## 3. Local Records

Records can be declared inside methods to group intermediate data:

```java
List<String> processTransactions(List<Transaction> txns) {
    record Summary(String category, double total) {}
    
    Map<String, Double> grouped = txns.stream()
        .collect(Collectors.groupingBy(
            Transaction::category,
            Collectors.summingDouble(Transaction::amount)
        ));
    
    return grouped.entrySet().stream()
        .map(e -> new Summary(e.getKey(), e.getValue()))
        .sorted(Comparator.comparingDouble(Summary::total).reversed())
        .map(s -> s.category() + ": $" + s.total())
        .toList();
}
```

## 4. Records with JPA

Records are immutable value objects — ideal for DTOs but challenging as entities:

### As Projections (Spring Data JPA)

```java
public interface PersonRepository extends JpaRepository<Person, Long> {
    // Record projection
    List<PersonName> findAllProjectedBy();
}

record PersonName(String firstName, String lastName) {}
```

### As Embeddable Values

```java
@Embeddable
public record Money(BigDecimal amount, String currency) {}

@Entity
public class Order {
    @Id private Long id;
    @Embedded private Money total;
}
```

### Hibernate 6 with Records

Hibernate 6.2+ supports records as embeddables and DTOs natively via `@Embeddable` on record types.

## 5. Serialization

Records are serializable by default with special serialization behavior:

```java
record User(String username, String password) implements Serializable {}
```

- Serialization uses canonical constructor (security benefit)
- No custom `writeObject`/`readObject` needed
- No `readResolve` / `writeReplace` magic required
- Immune to deserialization attacks that exploit mutable objects

## 6. Best Practices

- Use records for **data carriers**, not behavior-heavy objects
- Keep fields small — preferably primitives or immutable references
- Avoid records as JPA **entities** (use as embeddables or DTOs)
- Leverage compact constructors for validation
- Use local records to encapsulate method-scope grouping logic
