# Refactoring with Records

## Refactoring Simple POJOs to Records

### Step 1: Immutable POJO to Record

**Before**:
```java
public class Address {
    private final String street;
    private final String city;
    private final String zipCode;
    
    public Address(String street, String city, String zipCode) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
    }
    
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getZipCode() { return zipCode; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address addr = (Address) o;
        return street.equals(addr.street) && 
               city.equals(addr.city) && 
               zipCode.equals(addr.zipCode);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(street, city, zipCode);
    }
    
    @Override
    public String toString() {
        return "Address{street='" + street + "', city='" + city + "', zipCode='" + zipCode + "'}";
    }
}
```

**After**:
```java
public record Address(String street, String city, String zipCode) {}
```

### Step 2: Mutable POJO with Validation

**Before**:
```java
public class Person {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        setName(name);
        setAge(age);
    }
    
    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException();
        this.name = name;
    }
    public int getAge() { return age; }
    public void setAge(int age) {
        if (age < 0 || age > 150) throw new IllegalArgumentException();
        this.age = age;
    }
}
```

**After** (if immutability is acceptable):
```java
public record Person(String name, int age) {
    public Person {
        if (name == null || name.isBlank()) throw new IllegalArgumentException();
        if (age < 0 || age > 150) throw new IllegalArgumentException();
    }
    
    // If callers use getters, add bean-compatible accessors
    public String getName() { return name; }
    public int getAge() { return age; }
}
```

## Refactoring Multiple Return Values

**Before**: Using Object[], Map, or custom class
```java
// Using Object array
Object[] result = calculateStats(values);
double mean = (double) result[0];
double stddev = (double) result[1];

// Using Map
Map<String, Double> result = calculateStats(values);
double mean = result.get("mean");
```

**After**: Using records
```java
record Stats(double mean, double stddev) {}

Stats result = calculateStats(values);
System.out.println("Mean: " + result.mean());
System.out.println("StdDev: " + result.stddev());
```

## Refactoring Method Arguments

**Before**: Passing many primitive parameters
```java
public void createUser(String name, String email, String phone, String address, 
                      String city, String zip, boolean active) {
    // Hard to read, order matters, easy to confuse parameters
}
```

**After**: Grouping with records
```java
record Name(String first, String last) {}
record Contact(String email, String phone) {}
record Location(String address, String city, String zip) {}

public void createUser(Name name, Contact contact, Location location, boolean active) {
    // Clear, self-documenting, type-safe
}
```

## Refactoring Stream Intermediate Results

**Before**: Using arrays or tuples
```java
Map<String, List<Object[]>> grouped = items.stream()
    .collect(Collectors.groupingBy(
        Item::category,
        Collectors.mapping(
            item -> new Object[]{item.name(), item.price()},
            Collectors.toList()
        )
    ));
```

**After**: Using local records
```java
record ItemSummary(String name, double price) {}

Map<String, List<ItemSummary>> grouped = items.stream()
    .collect(Collectors.groupingBy(
        Item::category,
        Collectors.mapping(
            item -> new ItemSummary(item.name(), item.price()),
            Collectors.toList()
        )
    ));
```

## Refactoring Result Wrappers

**Before**: Multiple return types for success/failure
```java
// Either return null or throw exception
public User findUser(String id) {
    // returns null if not found
}

// Or use Optional
public Optional<User> findUser(String id) {
    // Optional.empty() if not found
}
```

**After**: Record with success/failure
```java
record Result<T>(T value, String error) {
    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }
    
    public static <T> Result<T> failure(String error) {
        return new Result<>(null, error);
    }
    
    public boolean isSuccess() { return error == null; }
}

Result<User> result = findUser(id);
if (result.isSuccess()) {
    process(result.value());
} else {
    log.error(result.error());
}
```

## Migration Checklist

1. **Identify value objects**: Look for classes used primarily as data carriers
2. **Check mutability**: If the class is already immutable, it's a direct match
3. **Check external usage**: Does anything rely on JavaBeans convention (getX/setX)?
4. **Update callers**: `.getX()` → `.x()`, constructor calls remain the same
5. **Remove boilerplate**: Delete manual equals, hashCode, toString, getters
6. **Test serialization**: If serializable, test deserialization of old data
7. **Check frameworks**: Verify framework support (Jackson 2.12+, Spring Data)
