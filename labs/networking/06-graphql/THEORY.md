# GraphQL - Theory

## Core Concepts
- **Schema**: Describes the API's types, queries, mutations, and subscriptions
- **Queries**: Read data (like GET)
- **Mutations**: Write data (like POST, PUT, DELETE)
- **Subscriptions**: Real-time updates (like WebSocket)
- **Resolvers**: Functions that return data for each field

## Schema Definition Language (SDL)
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
    author: User!
    comments: [Comment!]!
}

type Query {
    user(id: ID!): User
    users(page: Int, size: Int): [User!]!
    post(id: ID!): Post
    search(term: String!): [SearchResult!]!
}

type Mutation {
    createUser(input: CreateUserInput!): User!
    updateUser(id: ID!, input: UpdateUserInput!): User!
    deleteUser(id: ID!): Boolean!
}

type Subscription {
    postCreated: Post!
    userUpdated(id: ID!): User!
}
```

## Java Implementation
```java
// Resolver
@Controller
public class UserController {
    @QueryMapping
    public User user(@Argument String id) {
        return userService.findById(id);
    }

    @MutationMapping
    public User createUser(@Argument CreateUserInput input) {
        return userService.create(input);
    }

    @SubscriptionMapping
    public Flux<User> userUpdated(@Argument String id) {
        return userService.userUpdates(id);
    }
}

// DataLoader for N+1 prevention
@Component
public class UserDataLoader implements DataLoader<String, User> {
    private final UserRepository repository;

    public UserDataLoader(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public CompletableFuture<User> load(String userId) {
        return CompletableFuture.supplyAsync(() ->
            repository.findById(userId).orElse(null));
    }
}
```

## Benefits over REST
- **Client-specified data**: No over-fetching or under-fetching
- **Single endpoint**: /graphql replaces many REST endpoints
- **Strong typing**: Self-documenting schema
- **Introspection**: API explorer tools like GraphiQL
