# Common Mistakes: Spring Data JPA

## N+1 Query Problem
```java
// BAD: Each department triggers a query for employees
List<Department> depts = departmentRepository.findAll();
for (Department d : depts) {
    System.out.println(d.getEmployees().size());
}
// Fix: Use @EntityGraph or JOIN FETCH
```

## Method Name Too Long
```java
// BAD: Fragile, hard to read
List<User> findByFirstNameAndLastNameAndAgeBetweenAndCityAndActive(
    String fn, String ln, int min, int max, String city, boolean active);
// Fix: Use @Query or Specifications
```

## Ignoring Pagination
```java
// BAD: listAll() without pagination on large tables
List<Order> findAll();
// Fix: return Page<Order> or Slice<Order>
```

## Mutable Entity in Set
```java
// BAD: HashSet with mutable objects breaks hashCode/equals
@OneToMany Set<OrderItem> items;
// Fix: Use List or ensure equals/hashCode uses immutable fields
```

## Missing @Transactional on Service Layer
```java
// BAD: LazyInitializationException on lazy loaded collections
// Fix: @Transactional(readOnly = true) on service methods
```
