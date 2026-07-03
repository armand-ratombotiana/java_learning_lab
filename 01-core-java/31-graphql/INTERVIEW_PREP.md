# Module 31: GraphQL in Spring Boot - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What problems does GraphQL solve compared to traditional REST APIs?
**Answer**:
GraphQL primarily solves two major issues present in REST architectures:
1. **Over-fetching**: In REST, an endpoint (e.g., `/api/users/1`) returns a fixed data structure defined by the server. If the client only needs the user's `name`, it still receives the entire user object (email, address, history, etc.), wasting network bandwidth. GraphQL allows the client to explicitly query *only* the fields it needs.
2. **Under-fetching (N+1 Requests)**: In REST, if a client needs a user and their last 5 posts, it might have to make one request to `/users/1`, parse the response, and then make 5 subsequent requests to `/posts/{id}`. GraphQL allows the client to request deeply nested relational data in a single HTTP request to a single endpoint.

### Q2: What is a Mutation in GraphQL?
**Answer**:
While a `Query` is used for reading or fetching data (idempotent, side-effect free), a `Mutation` is used to create, update, or delete data on the server. Mutations are executed sequentially to prevent race conditions, whereas Queries are typically executed in parallel by the GraphQL execution engine. 

### Q3: Explain the N+1 Query Problem in GraphQL and how `DataLoader` solves it.
**Answer**:
Because GraphQL resolvers execute hierarchically, fetching a list of 100 `Post`s might trigger the top-level `Post` resolver 1 time. If the client also requests the `Author` for each post, the `Author` resolver will be invoked 100 separate times, leading to 101 database queries.
**DataLoader** (or Spring's `@BatchMapping`) solves this by "batching" the IDs. As the GraphQL engine traverses the tree, instead of querying the database immediately for each author, the DataLoader collects the Author IDs into a queue. Once all IDs for that tick are collected, it dispatches a single batched query (`SELECT * FROM users WHERE id IN (...)`) to the database, mapping the results back to the original objects.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Schema Design for Pagination
**Problem**: An interviewer asks you to design a GraphQL schema for a `getAllUsers` query that supports cursor-based pagination (often referred to as Relay-style connections) instead of simple offset/limit pagination. How do you structure the types?

**Solution**:
Cursor-based pagination requires a wrapper type (Connection) containing edges (which contain the actual node and its cursor) and page info (metadata).

```graphql
type Query {
    getAllUsers(first: Int, after: String): UserConnection
}

type UserConnection {
    edges: [UserEdge]
    pageInfo: PageInfo!
}

type UserEdge {
    cursor: String!
    node: User!
}

type PageInfo {
    hasNextPage: Boolean!
    hasPreviousPage: Boolean!
    startCursor: String
    endCursor: String
}

type User {
    id: ID!
    name: String!
}
```

### Scenario 2: Error Handling in GraphQL
**Problem**: In REST, if a user is not found, you return a `404 Not Found` HTTP status code. How does error handling work in GraphQL, and what HTTP status code does a GraphQL server typically return if a query fails validation or logic?

**Solution**:
A GraphQL API almost always returns an HTTP `200 OK` status code, even if errors occurred during query execution (unless there is a catastrophic server/network failure).
Errors are handled within the JSON payload itself. A GraphQL response contains two root-level keys: `"data"` and `"errors"`.
If the `getUser` query fails because the user isn't found, the response looks like:
```json
{
  "data": {
    "getUser": null
  },
  "errors": [
    {
      "message": "User with ID 1 not found",
      "locations": [ { "line": 2, "column": 3 } ],
      "path": [ "getUser" ]
    }
  ]
}
```
If multiple queries were sent in the same request, some might succeed (populating `"data"`) while others fail (populating `"errors"`), creating a partial success response.