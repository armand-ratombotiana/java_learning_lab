# Module 31: GraphQL in Spring Boot - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-30, especially Spring Boot and REST API  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Introduction to GraphQL](#intro)
2. [GraphQL vs REST](#comparison)
3. [GraphQL Schema Definition Language (SDL)](#schema)
4. [Queries and Mutations](#operations)
5. [Spring for GraphQL Integration](#spring)

---

## 1. Introduction to GraphQL <a name="intro"></a>
GraphQL is a query language for APIs and a runtime for fulfilling those queries with your existing data. It allows clients to request exactly the data they need, nothing more and nothing less.

---

## 2. GraphQL vs REST <a name="comparison"></a>
- **REST**: Multiple endpoints returning fixed data structures. Often leads to Over-fetching (getting too much data) or Under-fetching (requiring multiple requests).
- **GraphQL**: A single endpoint (`/graphql`). The client defines the structure of the response, solving both over-fetching and under-fetching.

---

## 3. GraphQL Schema Definition Language (SDL) <a name="schema"></a>
The schema defines the types and relationships in your API. It is usually placed in `src/main/resources/graphql/schema.graphqls`.

```graphql
type User {
    id: ID!
    name: String!
    email: String!
    posts: [Post!]!
}

type Post {
    id: ID!
    title: String!
    content: String!
}
```

---

## 4. Queries and Mutations <a name="operations"></a>
- **Query**: Used to read data (analogous to GET).
- **Mutation**: Used to write or modify data (analogous to POST/PUT/DELETE).

```graphql
type Query {
    getUserById(id: ID!): User
    getAllUsers: [User]
}

type Mutation {
    createUser(name: String!, email: String!): User
}
```

---

## 5. Spring for GraphQL Integration <a name="spring"></a>
Spring for GraphQL provides annotation-based programming models mapped to the schema.

```java
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    @QueryMapping
    public User getUserById(@Argument Long id) {
        return userService.findById(id);
    }

    @MutationMapping
    public User createUser(@Argument String name, @Argument String email) {
        return userService.createUser(name, email);
    }
}
```