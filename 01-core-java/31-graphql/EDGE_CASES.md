# Module 31: GraphQL in Spring Boot - Edge Cases & Pitfalls

---

## Pitfall 1: The N+1 Query Problem

### ❌ Wrong
Fetching deeply nested fields without optimization. If a `QueryMapping` returns a List of Users, and each User has a list of Posts resolved by a `@SchemaMapping`, Spring will issue 1 query for the Users, and N queries for the Posts.
```java
@SchemaMapping
public List<Post> posts(User user) {
    // ❌ Called N times!
    return postRepository.findByUserId(user.getId()); 
}
```

### ✅ Correct
Use `@BatchMapping` or `DataLoader` to batch the IDs together and fetch all related posts in a single query.
```java
@BatchMapping
public Map<User, List<Post>> posts(List<User> users) {
    // ✅ Called once for the entire batch
    return postService.findPostsForUsers(users);
}
```

---

## Pitfall 2: Schema Mismatches

### ❌ Wrong
Having typos between the Java method names/arguments and the GraphQL schema definition. If the names don't match exactly (or aren't explicitly mapped via annotation values), GraphQL won't be able to route the request.

### ✅ Correct
Ensure strict parity between `.graphqls` files and Java Controllers. Use tools or tests that fail the build if the schema is not fully wired.

---

## Pitfall 3: Deeply Nested Malicious Queries

### ❌ Wrong
Allowing unlimited query depth. A malicious client could send a query requesting `User -> Posts -> Author -> Posts -> Author...` causing a StackOverflow or Denial of Service (DoS) attack.

### ✅ Correct
Configure a Maximum Query Depth limit and Query Complexity analysis in your GraphQL engine to reject overly complex or infinitely nested queries.