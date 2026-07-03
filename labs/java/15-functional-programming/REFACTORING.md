# Refactoring — Functional Programming

## Mutable Accumulation → Immutable Reduction
```java
// Before
int sum = 0;
for (int x : numbers) { sum += x; }

// After
int sum = numbers.stream().reduce(0, Integer::sum);
```

## Null Check → Optional
```java
// Before
String name = findUser(id);
String display = (name != null) ? name.toUpperCase() : "GUEST";

// After
String display = findUserOptional(id)
    .map(String::toUpperCase)
    .orElse("GUEST");
```

## Mutable Builder → Immutable Record + with*
```java
// Before
Person p = new Person();
p.setName("Alice");
p.setAge(30);

// After
record Person(String name, int age) {}
Person p = new Person("Alice", 30);
Person older = new Person(p.name(), p.age() + 1); // new snapshot
```

## Nested If → Monadic FlatMap
```java
// Before
if (order != null && order.getCustomer() != null) {
    String email = order.getCustomer().getEmail();
    if (email != null) send(email);
}

// After
Optional.ofNullable(order)
    .map(Order::getCustomer)
    .map(Customer::getEmail)
    .ifPresent(EmailService::send);
```
