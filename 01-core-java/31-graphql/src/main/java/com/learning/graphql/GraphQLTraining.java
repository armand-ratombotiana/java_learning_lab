package com.learning.graphql;

import java.util.*;

public class GraphQLTraining {

    public static void main(String[] args) {
        System.out.println("=== GraphQL Training ===");

        demonstrateGraphQLConcepts();
        demonstrateSchema();
        demonstrateQueries();
        demonstrateMutations();
    }

    private static void demonstrateGraphQLConcepts() {
        System.out.println("\n--- GraphQL Overview ---");

        String[] concepts = {
            "Single endpoint - /graphql",
            "Client requests exact data needed",
            "No over-fetching or under-fetching",
            "Strongly typed schema",
            "Introspection support"
        };

        for (String c : concepts) System.out.println("  - " + c);

        System.out.println("\nREST vs GraphQL:");
        String[] comparison = {
            "REST: Multiple endpoints, fixed response",
            "GraphQL: Single endpoint, flexible response",
            "REST: /users, /users/1, /users/1/posts",
            "GraphQL: One query for nested data"
        };

        for (String c : comparison) System.out.println("  " + c);
    }

    private static void demonstrateSchema() {
        System.out.println("\n--- GraphQL Schema ---");

        String schema = """
            type User {
              id: ID!
              name: String!
              email: String!
              posts: [Post!]!
            }
            
            type Post {
              id: ID!
              title: String!
              content: String
              author: User!
              comments: [Comment!]!
            }
            
            type Comment {
              id: ID!
              text: String!
              author: User!
            }
            
            type Query {
              users: [User!]!
              user(id: ID!): User
              posts: [Post!]!
            }
            
            type Mutation {
              createUser(input: CreateUserInput!): User!
              createPost(input: CreatePostInput!): Post!
            }
            
            input CreateUserInput {
              name: String!
              email: String!
            }
            
            input CreatePostInput {
              title: String!
              content: String
              authorId: ID!
            }""";
        System.out.println(schema);

        System.out.println("Schema Definition Language (SDL):");
        String[] sdl = {
            "type - object type definition",
            "! - non-null (required)",
            "[] - list type",
            "input - input object type",
            "scalar - built-in types (ID, String, Int, Float, Boolean)"
        };

        for (String s : sdl) System.out.println("  " + s);
    }

    private static void demonstrateQueries() {
        System.out.println("\n--- GraphQL Queries ---");

        System.out.println("Basic Query:");
        String query = """
            query {
              users {
                name
                email
              }
            }""";
        System.out.println(query);

        System.out.println("\nQuery with Nested Data:");
        String nested = """
            query {
              users {
                name
                posts {
                  title
                  comments {
                    text
                  }
                }
              }
            }""";
        System.out.println(nested);

        System.out.println("\nQuery with Arguments:");
        String args = """
            query {
              user(id: "1") {
                name
                email
                posts {
                  title
                }
              }
            }""";
        System.out.println(args);

        System.out.println("\nQuery with Aliases:");
        String aliases = """
            query {
              activeUsers: users(isActive: true) {
                name
              }
              inactiveUsers: users(isActive: false) {
                name
              }
            }""";
        System.out.println(aliases);

        System.out.println("\nFragments:");
        String fragment = """
            fragment UserFields on User {
              id
              name
              email
            }
            
            query {
              user1: user(id: "1") {
                ...UserFields
              }
              user2: user(id: "2") {
                ...UserFields
              }
            }""";
        System.out.println(fragment);

        System.out.println("\nVariables:");
        String variables = """
            query GetUser($userId: ID!) {
              user(id: $userId) {
                name
                email
              }
            }
            
            # Variables:
            { "userId": "1" }""";
        System.out.println(variables);
    }

    private static void demonstrateMutations() {
        System.out.println("\n--- GraphQL Mutations ---");

        System.out.println("Create Mutation:");
        String create = """
            mutation CreateUser {
              createUser(input: {
                name: "John Doe"
                email: "john@example.com"
              }) {
                id
                name
                email
              }
            }""";
        System.out.println(create);

        System.out.println("\nUpdate Mutation:");
        String update = """
            mutation UpdatePost {
              updatePost(id: "1", input: {
                title: "Updated Title"
              }) {
                id
                title
              }
            }""";
        System.out.println(update);

        System.out.println("\nDelete Mutation:");
        String delete = """
            mutation DeleteUser {
              deleteUser(id: "1") {
                success
                message
              }
            }""";
        System.out.println(delete);

        System.out.println("\nSubscriptions (Real-time):");
        String subscription = """
            subscription OnNewComment {
              newComment(postId: "1") {
                id
                text
                author {
                  name
                }
              }
            }""";
        System.out.println(subscription);

        System.out.println("\nGraphQL Java Tools:");
        String[] tools = {
            "graphql-java - Java implementation",
            "GraphQL SPQR - code generation",
            "Netflix DGS - Spring Boot framework",
            "Apollo GraphQL - federated gateway"
        };

        for (String t : tools) System.out.println("  - " + t);
    }
}