package com.learning.lab.module26;

import graphql.*;
import graphql.schema.*;

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 26: GraphQL Resolvers ===");

        schemaDefinitionDemo();
        resolverDemo();
        executionDemo();
    }

    static void schemaDefinitionDemo() {
        System.out.println("\n--- GraphQL Schema Definition ---");
        String schema = "type Query { user(id: ID!): User users: [User] } " +
                        "type Mutation { createUser(name: String!): User } " +
                        "type User { id: ID name: String email: String }";
        System.out.println("Schema: " + schema);
    }

    static void resolverDemo() {
        System.out.println("\n--- GraphQL Resolvers ---");
        GraphQLObjectType userType = GraphQLObjectType.newObject()
                .name("User")
                .field(GraphQLFieldDefinition.newFieldDefinition().name("id").type(GraphQLNonNull(GraphQLID)).build())
                .field(GraphQLFieldDefinition.newFieldDefinition().name("name").type(GraphQLString).build())
                .field(GraphQLFieldDefinition.newFieldDefinition().name("email").type(GraphQLString).build())
                .build();

        GraphQLObjectType queryType = GraphQLObjectType.newObject()
                .name("Query")
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("user")
                        .type(userType)
                        .argument(GraphQLArgument.newArgument().name("id").type(GraphQLNonNull(GraphQLID)).build())
                        .dataFetcher(env -> getUserById(env.getArgument("id")))
                        .build())
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("users")
                        .type(GraphQLList.list(userType))
                        .dataFetcher(env -> getAllUsers())
                        .build())
                .build();

        System.out.println("Query Type defined: " + queryType.getName());
        System.out.println("User Type fields: " + userType.getFieldDefinitions().size());
    }

    static void executionDemo() {
        System.out.println("\n--- GraphQL Execution ---");
        String query = "{ user(id: \"1\") { name email } }";
        System.out.println("Query: " + query);

        GraphQLSchema schema = GraphQLSchema.newSchema()
                .query(GraphQLObjectType.newObject().name("Query").build())
                .build();
        GraphQL graphQL = GraphQL.newGraphQL(schema).build();
        ExecutionResult result = graphQL.execute(query);
        System.out.println("Result: " + result.getData());
    }

    static Map<String, Object> getUserById(String id) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("name", "John Doe");
        user.put("email", "john@example.com");
        return user;
    }

    static List<Map<String, Object>> getAllUsers() {
        List<Map<String, Object>> users = new ArrayList<>();
        Map<String, Object> user = new HashMap<>();
        user.put("id", "1");
        user.put("name", "John Doe");
        user.put("email", "john@example.com");
        users.add(user);
        return users;
    }
}

class User {
    private String id;
    private String name;
    private String email;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}

class UserResolver {
    public User resolveUser(DataFetchingEnvironment env) {
        String id = env.getArgument("id");
        return new User(id, "John Doe", "john@example.com");
    }
}