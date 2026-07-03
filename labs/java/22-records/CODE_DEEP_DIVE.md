# Code Deep Dive: Records

## Complete Record with Validation

```java
import java.time.LocalDate;
import java.util.List;

public record Person(
    String name,
    LocalDate dateOfBirth,
    List<String> emailAddresses
) {
    // Compact constructor with validation
    public Person {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (dateOfBirth == null || dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("invalid date of birth");
        }
        if (emailAddresses == null) {
            emailAddresses = List.of();  // Normalize null to empty list
        }
        // Defensive copy: wrap in unmodifiable list
        emailAddresses = List.copyOf(emailAddresses);
    }

    // Non-canonical constructor (delegating)
    public Person(String name, LocalDate dateOfBirth) {
        this(name, dateOfBirth, List.of());  // Must call this(...)
    }
    
    // Factory method
    public static Person createAnonymous(String name) {
        return new Person(name, LocalDate.now(), List.of());
    }

    // Custom instance methods
    public int age() {
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    public boolean isAdult() {
        return age() >= 18;
    }

    public Person withAdditionalEmail(String email) {
        var newEmails = new java.util.ArrayList<>(emailAddresses);
        newEmails.add(email);
        return new Person(name, dateOfBirth, List.copyOf(newEmails));
    }

    // Static methods
    public static Person oldest(Person a, Person b) {
        return a.dateOfBirth().isBefore(b.dateOfBirth()) ? a : b;
    }
}
```

**Key observations:**
- Compact constructor validates input and normalizes null references
- Defensive copy of mutable component (List)
- Factory methods and non-canonical constructors provide flexibility
- Instance methods leverage record components naturally
- `with*` methods return new instances (immutability pattern)

## Records with Sealed Types

```java
// Sealed interface with record implementations
sealed interface Vehicle permits Car, Truck, Motorcycle {}

record Car(String make, String model, int doors) implements Vehicle {}
record Truck(String make, int payloadKg) implements Vehicle {}
record Motorcycle(String make, boolean hasSidecar) implements Vehicle {}

class VehicleProcessor {
    public String describe(Vehicle v) {
        return switch (v) {
            case Car(var make, var model, int doors) when doors > 2 
                -> make + " " + model + " family car";
            case Car(var make, var model, var doors) 
                -> make + " " + model + " " + doors + "-door";
            case Truck(var make, int payload) when payload > 5000
                -> make + " heavy truck";
            case Truck(var make, var payload) 
                -> make + " light truck";
            case Motorcycle(var make, var hasSidecar) 
                -> make + (hasSidecar ? " with sidecar" : "");
        };
        // No default needed — sealed hierarchy makes it exhaustive
    }
}
```

## Records with Builder Pattern

```java
public record Query(String select, String from, String where, String orderBy, int limit) {
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String select = "*";
        private String from;
        private String where = "";
        private String orderBy = "";
        private int limit = 100;
        
        public Builder select(String select) {
            this.select = select;
            return this;
        }
        
        public Builder from(String from) {
            this.from = from;
            return this;
        }
        
        public Builder where(String where) {
            this.where = where;
            return this;
        }
        
        public Builder orderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }
        
        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }
        
        public Query build() {
            return new Query(select, from, where, orderBy, limit);
        }
    }
}

// Usage
Query q = Query.builder()
    .select("id, name, email")
    .from("users")
    .where("active = true")
    .orderBy("name")
    .limit(50)
    .build();
```

## Records in Stream Pipelines

```java
record Order(String product, int quantity, double price) {}
record SalesSummary(String product, int totalUnits, double totalRevenue) {}

public List<SalesSummary> summarizeSales(List<Order> orders) {
    return orders.stream()
        .collect(Collectors.groupingBy(
            Order::product,
            Collectors.summarizingDouble(o -> o.quantity() * o.price())
        ))
        .entrySet().stream()
        .map(e -> new SalesSummary(
            e.getKey(),
            (int) e.getValue().getCount(),
            e.getValue().getSum()
        ))
        .sorted(Comparator.comparing(SalesSummary::totalRevenue).reversed())
        .toList();
}
```

## Records with JSON Serialization (Jackson)

```java
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

public record Employee(
    @JsonProperty("employee_id") int id,
    String name,
    String department,
    @JsonSerialize(using = LocalDateSerializer.class) LocalDate hireDate
) {
    // Jackson 2.12+ supports records natively
    // No annotations needed for basic serialization
}

// Serialization
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new JavaTimeModule());
String json = mapper.writeValueAsString(new Employee(1, "Alice", "Engineering", LocalDate.now()));

// Deserialization
Employee emp = mapper.readValue(json, Employee.class);
```

## Records with Database Mapping (Spring Data JDBC)

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("customers")
public record Customer(
    @Id Long id,
    String name,
    String email,
    boolean active
) {
    // Spring Data JDBC supports records as query results
}

// Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    List<Customer> findByActiveTrue();
}
```
