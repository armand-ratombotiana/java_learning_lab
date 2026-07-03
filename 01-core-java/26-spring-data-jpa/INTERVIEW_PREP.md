 # Module 26: Spring Data JPA - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the N+1 Query Problem in JPA/Hibernate, and how do you solve it?
**Answer**:
The N+1 problem occurs when you execute 1 query to fetch a list of parent entities (e.g., fetching 100 `Department` entities), and then, while iterating over that list in your code, you access a lazy-loaded child collection (e.g., `dept.getEmployees()`). Hibernate detects the uninitialized collection and issues N additional queries (1 for each department) to fetch the children. This results in 101 total queries instead of 1, severely degrading performance.
**Solution**:
You can solve this by fetching the parent and the children together in a single SQL query using a `JOIN FETCH` clause in JPQL, or by using Spring Data JPA's `@EntityGraph` annotation.

### Q2: What is the difference between `save()` and "Dirty Checking" in JPA?
**Answer**:
In Spring Data JPA, `repository.save()` is used to persist a *new* entity to the database. It calls `EntityManager.persist()`. If the entity already has an ID, `save()` calls `EntityManager.merge()`.
However, if you retrieve an entity from the database inside a `@Transactional` method, the entity becomes "Managed" by the Persistence Context. If you use setter methods to modify the entity's state, you do **not** need to call `save()`. When the transaction completes, Hibernate performs "Dirty Checking"—it compares the current state of the entity to its initial state when it was loaded. If there are differences, it automatically generates and executes an `UPDATE` SQL statement.

### Q3: Why is it recommended to use `FetchType.LAZY` over `FetchType.EAGER`?
**Answer**:
By default, `@ManyToOne` and `@OneToOne` relationships use `FetchType.EAGER`, while `@OneToMany` and `@ManyToMany` use `FetchType.LAZY`.
`FetchType.EAGER` forces Hibernate to load the associated entity immediately via an outer join or a secondary select every single time the parent entity is queried. This often leads to fetching massive amounts of unnecessary data (e.g., loading a User loads their Profile, which loads their Settings, etc.).
Best practice dictates making all relationships `LAZY` globally. If a specific use case requires the child data, you should explicitly fetch it for that specific query using a `JOIN FETCH` or `@EntityGraph`.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Bidirectional Relationship Synchronization
**Problem**: You have a `Post` and `Comment` entity mapped bidirectionally (`@OneToMany` in `Post`, `@ManyToOne` in `Comment`). A junior developer writes `post.getComments().add(newComment); repository.save(post);`, but the database shows a `null` foreign key for the comment's `post_id`. Why? How do you fix the code?

**Solution**:
In a bidirectional relationship, the side with the `@JoinColumn` (the `Comment` entity) is the "owning" side. Hibernate only looks at the owning side to update the foreign key in the database. Updating the `mappedBy` side (the collection in `Post`) does not trigger a database update.
To fix this, you must update *both* sides of the relationship in the object model.

Create a helper method in the `Post` class:
```java
public void addComment(Comment comment) {
    // 1. Add to the collection
    this.comments.add(comment);
    // 2. Set the owning side!
    comment.setPost(this);
}
```
Now, call `post.addComment(newComment);` instead.

### Scenario 2: Derived Query Parsing
**Problem**: Write the Spring Data JPA method signature to find a list of `User` entities where the `lastName` exactly matches a parameter, the `age` is greater than a parameter, and the results are sorted by `createdAt` descending.

**Solution**:
```java
List<User> findByLastNameAndAgeGreaterThanOrderByCreatedAtDesc(String lastName, int age);
```