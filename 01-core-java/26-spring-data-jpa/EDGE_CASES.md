# Module 26: Spring Data JPA - Edge Cases & Pitfalls

---

## Pitfall 1: N+1 Query Problem

### ❌ Wrong
Fetching an entity with a `@OneToMany` or `@ManyToMany` relationship, and then looping through the collection to access the lazy-loaded children. This causes 1 query to fetch the parents, and N queries to fetch the children.
```java
List<Department> departments = departmentRepository.findAll();
for (Department dept : departments) {
    // Triggers an additional query for each department!
    System.out.println(dept.getEmployees().size()); 
}
```

### ✅ Correct
Use a `JOIN FETCH` in a custom JPQL query or define `@EntityGraph` to fetch the children in the same single query.
```java
@Query("SELECT d FROM Department d JOIN FETCH d.employees")
List<Department> findAllWithEmployees();
```

---

## Pitfall 2: Eager Fetching by Default

### ❌ Wrong
Using `@ManyToOne` or `@OneToOne` relationships. By default, JPA fetches these eagerly. This means querying for an `Employee` automatically joins the `Department` table, even if you never need the department data, causing massive performance drops.

### ✅ Correct
Always set `fetch = FetchType.LAZY` on all relational mappings to ensure data is only queried when explicitly needed.
```java
@ManyToOne(fetch = FetchType.LAZY)
private Department department;
```

---

## Pitfall 3: Calling save() Unnecessarily

### ❌ Wrong
Explicitly calling `repository.save()` inside a `@Transactional` method after modifying an entity fetched from the database.
```java
@Transactional
public void updateEmail(Long id, String email) {
    Employee emp = repository.findById(id).orElseThrow();
    emp.setEmail(email);
    repository.save(emp); // Unnecessary!
}
```

### ✅ Correct
JPA uses "Dirty Checking". Since the object is managed by the persistence context within the `@Transactional` boundary, Hibernate will automatically detect the change and issue an `UPDATE` statement upon transaction commit. `repository.save()` is only needed for inserting new entities.