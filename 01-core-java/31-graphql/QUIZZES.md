# Module 31: GraphQL in Spring Boot - Quizzes

---

## Q1: GraphQL vs REST
What is the primary advantage of GraphQL over traditional REST APIs?

A) GraphQL requires less server processing power.
B) GraphQL uses multiple endpoints for better routing.
C) GraphQL allows clients to specify exactly the data they need, eliminating over-fetching and under-fetching.
D) GraphQL automatically encrypts payloads.

**Answer**: C
**Explanation**: By allowing the client to define the structure of the response payload, GraphQL ensures that exactly the requested data is transferred, avoiding bloated responses (over-fetching) or the need for multiple round-trips (under-fetching).

---

## Q2: Operations
Which GraphQL operation type is functionally equivalent to an HTTP POST, PUT, or DELETE request used for modifying data?

A) Query
B) Mutation
C) Subscription
D) Fragment

**Answer**: B
**Explanation**: While `Query` is used for reading data, `Mutation` is specifically designed for operations that cause side-effects or modify data on the server.

---

## Q3: Resolving the N+1 Problem
In Spring for GraphQL, which annotation is highly recommended to efficiently load nested relationships and avoid the N+1 Query problem?

A) `@SchemaMapping`
B) `@QueryMapping`
C) `@BatchMapping`
D) `@LazyFetch`

**Answer**: C
**Explanation**: `@BatchMapping` solves the N+1 problem by batching the parent IDs and fetching all related children in a single, optimized backend query (leveraging `DataLoader` under the hood).