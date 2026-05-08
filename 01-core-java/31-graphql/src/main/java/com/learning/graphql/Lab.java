package com.learning.graphql;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== GraphQL Lab (Conceptual) ===\n");

        schemaTypes();
        queriesMutations();
        resolvers();
        subscriptions();
        graphqlConcepts();
    }

    static void schemaTypes() {
        System.out.println("--- Schema Definition Language ---");

        System.out.println("""
          type User { id: ID!  name: String!  email: String!  posts: [Post!]! }
          type Post { id: ID!  title: String!  author: User! }
          type Query { user(id: ID!): User  users: [User!]! }
          type Mutation { createUser(name: String!, email: String!): User! }
          type Subscription { userCreated: User! }""");

        System.out.println("\n  Type system:");
        for (var t : List.of("Scalar: String, Int, Float, Boolean, ID",
                "Object: User, Post", "Input: input CreateUserInput { }",
                "Enum: enum Status { ACTIVE INACTIVE }",
                "Union: union SearchResult = User | Post",
                "List: [Type]", "NonNull: Type!"))
            System.out.println("  " + t);
    }

    static void queriesMutations() {
        System.out.println("\n--- Queries & Mutations ---");

        class GQLEngine {
            Object execute(String query) {
                if (query.contains("users"))
                    return Map.of("users", List.of(
                        Map.of("id", "1", "name", "Alice", "email", "alice@example.com"),
                        Map.of("id", "2", "name", "Bob", "email", "bob@example.com")));
                if (query.contains("createUser"))
                    return Map.of("createUser", Map.of("id", "3", "name", "Charlie"));
                return Map.of("errors", List.of(Map.of("message", "Unknown query")));
            }
        }

        var engine = new GQLEngine();
        System.out.println("  Query: { users { id name } }");
        System.out.println("  Response: " + engine.execute("users"));

        System.out.println("\n  Mutation params:");
        System.out.println("   mutation { createUser(name: \"Charlie\", email: \"c@x.com\") { id name } }");
        System.out.println("  Response: " + engine.execute("createUser"));

        System.out.println("\n  Operation types:");
        for (var c : List.of("Query - read (parallel)", "Mutation - write (sequential)", "Subscription - real-time stream"))
            System.out.println("  " + c);
    }

    static void resolvers() {
        System.out.println("\n--- Resolvers & Data Fetching ---");

        record User(String id, String name, String email) {}
        record Post(String id, String title, String authorId) {}

        class DataStore {
            Map<String, User> users = Map.of("1", new User("1", "Alice", "a@x.com"), "2", new User("2", "Bob", "b@x.com"));
            Map<String, Post> posts = Map.of("p1", new Post("p1", "GraphQL Intro", "1"), "p2", new Post("p2", "Java 21", "1"), "p3", new Post("p3", "Spring", "2"));
        }

        class ResolverEngine {
            DataStore store = new DataStore();
            Object resolve(String parentType, String field, Object source, Map<String, Object> args) {
                if ("Query".equals(parentType) && "user".equals(field)) return store.users.get(args.get("id"));
                if ("Query".equals(parentType) && "users".equals(field)) return List.copyOf(store.users.values());
                if ("User".equals(parentType) && "posts".equals(field)) {
                    String uid = source instanceof User u ? u.id() : ((Map<String,?>)source).get("id").toString();
                    return store.posts.values().stream().filter(p -> p.authorId().equals(uid)).toList();
                }
                if ("Post".equals(parentType) && "author".equals(field)) {
                    String aid = source instanceof Post p ? p.authorId() : ((Map<String,?>)source).get("authorId").toString();
                    return store.users.get(aid);
                }
                return null;
            }
        }

        var resolver = new ResolverEngine();
        var user = resolver.resolve("Query", "user", null, Map.of("id", "1"));
        var posts = resolver.resolve("User", "posts", user, Map.of());
        var author = resolver.resolve("Post", "author", ((List<?>)posts).get(0), Map.of());
        System.out.println("  Query.user(id:1) -> " + user);
        System.out.println("  User.posts       -> " + posts);
        System.out.println("  Post.author      -> " + author);

        System.out.println("\n  N+1 problem & DataLoader:");
        System.out.println("""
            Without batching:
              SELECT * FROM posts WHERE author_id = '1'   (1 query)
              SELECT * FROM users WHERE id = '1'           (per author)
              SELECT * FROM users WHERE id = '2'

            With DataLoader:
              SELECT * FROM posts WHERE author_id = '1'   (1 query)
              SELECT * FROM users WHERE id IN ('1','2')   (1 batch)""");
    }

    static void subscriptions() {
        System.out.println("\n--- Subscriptions (Real-time) ---");

        class SubManager {
            final Map<String, List<Consumer<Object>>> topics = new ConcurrentHashMap<>();
            void subscribe(String topic, Consumer<Object> cb) {
                topics.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(cb);
                System.out.println("  Subscribed: " + topic);
            }
            void publish(String topic, Object data) {
                var subs = topics.get(topic);
                if (subs != null) subs.forEach(c -> c.accept(data));
            }
        }

        var sm = new SubManager();
        sm.subscribe("userCreated", data -> System.out.println("    [Live] New user: " + data));
        sm.publish("userCreated", Map.of("id", "4", "name", "Diana"));

        System.out.println("\n  Transport:");
        for (var t : List.of("WebSocket - Full-duplex (Apollo, graphql-ws)",
                "SSE - Server-Sent Events", "Callback - HTTP webhook"))
            System.out.println("  " + t);
    }

    static void graphqlConcepts() {
        System.out.println("\n--- GraphQL Concepts & Best Practices ---");

        for (var c : List.of("Schema-first: define .graphqls then implement",
                "Code-first: generate schema from @ annotations",
                "Federation: compose multiple GraphQL services",
                "Depth limiting: prevent deeply nested queries",
                "Complexity analysis: cost-based query limiting"))
            System.out.println("  " + c);

        System.out.println("\n  vs REST:");
        for (var c : List.of("Single endpoint vs multiple endpoints",
                "Client controls fields (no over/under fetching)",
                "No versioning needed (evolve schema, add fields)",
                "Caching: HTTP caching vs resolver-level cache",
                "Tooling: GraphiQL, Apollo vs Swagger, Postman"))
            System.out.println("  " + c);

        System.out.println("\n  Java libraries:");
        for (var c : List.of("graphql-java - core engine",
                "Spring for GraphQL - Spring Boot integration",
                "Netflix DGS - Domain Graph Service"))
            System.out.println("  " + c);
    }
}
