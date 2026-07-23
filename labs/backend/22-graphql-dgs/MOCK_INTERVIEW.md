# Mock Interview: GraphQL with Netflix DGS (Lab 22)

**Role:** Backend Engineer (Senior)  
**Duration:** 45 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** How does GraphQL differ from REST? When would you choose GraphQL?

**Candidate:**

| Aspect | REST | GraphQL |
|--------|------|---------|
| Data fetching | Multiple endpoints, fixed responses | Single endpoint, client-specified fields |
| Over/under-fetching | Common | Eliminated |
| Versioning | URL/header versioning | Evolve schema, deprecated fields |
| Caching | HTTP caching (URL-based) | More complex, needs persisted queries |
| Tooling | Swagger/OpenAPI | GraphiQL, Apollo Studio |
| Learning curve | Lower | Higher (schema, resolvers, N+1) |

Choose GraphQL when: clients need flexible data (mobile apps with varying screen sizes), multiple clients with different data needs, or when over-fetching is a performance concern. Choose REST for simple CRUD APIs, when HTTP caching is critical, or when API consumers prefer simplicity.

**Interviewer:** How does the Netflix DGS framework implement GraphQL in Spring Boot?

**Candidate:** DGS (Domain Graph Service) is Netflix's GraphQL framework for Spring Boot. It provides:
- Code-first or schema-first approaches
- Annotations: `@DgsComponent`, `@DgsQuery`, `@DgsMutation`, `@DgsData` for field resolvers
- DataLoader integration for N+1 prevention
- Federated GraphQL support
- Automatic schema generation from annotations

```java
@DgsComponent
public class UserDataFetcher {
    @DgsQuery
    public User user(@InputArgument String id) {
        return userService.findById(id);
    }
    
    @DgsData(parentType = "User", field = "orders")
    public CompletableFuture<List<Order>> orders(DgsDataFetchingEnvironment env) {
        User user = env.getSource();
        return ordersLoader.load(user.getId());
    }
}
```

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Explain the N+1 problem in GraphQL and how DataLoaders solve it.

**Candidate:** In GraphQL, a query like:
```graphql
{ users { name, orders { total } } }
```
Without optimization, would execute: 1 query for users + N queries for orders (one per user).

**DataLoader solution:** DataLoader batches and caches requests within a single request scope:
```java
@DgsComponent
public class OrderDataLoader {
    @DgsDataLoader(name = "ordersLoader")
    public class OrdersLoader implements BatchLoader<String, List<Order>> {
        @Override
        public CompletionStage<List<List<Order>>> load(List<String> userIds) {
            return CompletableFuture.supplyAsync(() -> {
                List<Order> orders = orderRepository.findByUserIds(userIds);
                // Group by userId to match order
                return userIds.stream()
                    .map(id -> orders.stream().filter(o -> o.getUserId().equals(id)).toList())
                    .toList();
            });
        }
    }
}
```

This reduces N+1 queries to 1 query for all users' orders, regardless of the number of users.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a GraphQL API for a social media platform using DGS that handles 10K QPS with varying client needs (mobile app, web app, third-party API).

**Candidate:** 

**Schema design:**
```graphql
type Query {
    feed(userId: ID!, first: Int!, after: String): FeedConnection!
    user(id: ID!): User
    search(query: String!, type: SearchType!): SearchResult!
}

type FeedConnection {
    edges: [FeedEdge!]!
    pageInfo: PageInfo!
}

type FeedEdge {
    node: Post!
    cursor: String!
}

type Post {
    id: ID!
    content: String!
    author: User!
    comments(first: Int, after: String): CommentConnection!
    likes: [User!]!
    likeCount: Int!
    createdAt: DateTime!
}

type User {
    id: ID!
    name: String!
    avatar: String!
    followers: [User!]!
    following: [User!]!
}
```

**Performance optimizations:**
1. **Connection pagination (Relay spec)** — cursor-based, not offset-based
2. **DataLoaders for all list fields** — batch parent lookups
3. **Computed fields** — `likeCount` as a computed field, not a separate request
4. **Persisted queries** — clients send hash, server maps to query (reduces parsing overhead)
5. **Query depth limiting** — `@DgsComponent` with `maxQueryDepth=10`
6. **Alias HTTP caching** — Apollo cache control directives for CDN caching

**DataLoader batch strategy for high QPS:**
```java
@DgsDataLoader(name = "feedLoader")
public class FeedLoader implements BatchLoader<String, List<Post>> {
    @Override
    public CompletionStage<List<List<Post>>> load(List<String> keys) {
        return CompletableFuture.supplyAsync(() -> {
            // Single query with WHERE id IN (:keys)
            List<Post> posts = postRepository.findByIdIn(keys);
            // Maintain order matching keys
            return keys.stream()
                .map(key -> posts.stream()
                    .filter(p -> p.getId().equals(key)).toList())
                .toList();
        });
    }
}
```

**Interviewer:** How would you implement the "comments" field on Post — considering it's only shown on post detail, not in the list?

**Candidate:** The `comments` field on `Post` is only resolved when requested:
```java
@DgsData(parentType = "Post", field = "comments")
public CompletableFuture<List<Comment>> getComments(DgsDataFetchingEnvironment env) {
    Post post = env.getSource();
    // Only load if client requested it
    // DataLoader batches multiple comment requests
    return commentLoader.load(post.getId());
}
```

DGS lazily resolves only requested fields. The DataLoader batches all `Post.comments` resolutions into a single query, regardless of how many posts were in the result. This is the key optimization: **batching across the entire query tree, not per parent.**

---

## Interviewer Feedback

**Strengths:** Excellent GraphQL fundamentals, practical DataLoader usage, clear schema design  
**Areas to Improve:** Could discuss federated GraphQL and Apollo Federation vs DGS federation  
**Verdict:** Strong Hire

---

*Lab 22 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
