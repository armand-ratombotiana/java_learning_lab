package solution;

import graphql.*;
import graphql.schema.*;

import java.util.*;
import java.util.stream.Collectors;

public class GraphQLSolution {

    private GraphQLSchema schema;
    private GraphQL graphQL;

    public void buildSchema() {
        // Define Query type
        GraphQLObjectType queryType = GraphQLObjectType.newObject()
            .name("Query")
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("user")
                .type(GraphQLString)
                .argument(GraphQLArgument.newArgument().name("id").type(GraphQLNonNullNonNullType.of(GraphQLStringType.string)).build())
                .dataFetcher(env -> getUserById(env.getArgument("id"))))
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("users")
                .type(GraphQLListNonNullType.of(GraphQLTypeReference.typeRef("User")))
                .dataFetcher(env -> getAllUsers()))
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("posts")
                .type(GraphQLListNonNullType.of(GraphQLTypeReference.typeRef("Post")))
                .argument(GraphQLArgument.newArgument().name("userId").type(GraphQLString).build())
                .dataFetcher(env -> getPosts(env.getArgument("userId"))))
            .build();

        // Define User type
        GraphQLObjectType userType = GraphQLObjectType.newObject()
            .name("User")
            .field(GraphQLFieldDefinition.newFieldDefinition().name("id").type(GraphQLNonNullNonNullType.of(GraphQLStringType.string)).build())
            .field(GraphQLFieldDefinition.newFieldDefinition().name("name").type(GraphQLStringType.string).build())
            .field(GraphQLFieldDefinition.newFieldDefinition().name("email").type(GraphQLStringType.string).build())
            .field(GraphQLFieldDefinition.newFieldDefinition().name("posts")
                .type(GraphQLListNonNullType.of(GraphQLTypeReference.typeRef("Post")))
                .dataFetcher(env -> getPostsByUser(env.getSource()))).build();

        // Define Post type
        GraphQLObjectType postType = GraphQLObjectType.newObject()
            .name("Post")
            .field(GraphQLFieldDefinition.newFieldDefinition().name("id").type(GraphQLNonNullNonNullType.of(GraphQLStringType.string)).build())
            .field(GraphQLFieldDefinition.newFieldDefinition().name("title").type(GraphQLStringType.string).build())
            .field(GraphQLFieldDefinition.newFieldDefinition().name("content").type(GraphQLStringType.string).build())
            .field(GraphQLFieldDefinition.newFieldDefinition().name("author")
                .type(GraphQLTypeReference.typeRef("User"))
                .dataFetcher(env -> getUserById(((Map)env.getSource()).get("authorId")))).build();

        schema = GraphQLSchema.newSchema()
            .query(queryType)
            .build(graphQL);

        graphQL = GraphQL.newGraphQL(schema).build();
    }

    public GraphQLSchema getSchema() {
        return schema;
    }

    public ExecutionResult execute(String query) {
        return graphQL.execute(query);
    }

    public ExecutionResult execute(String query, Map<String, Object> variables) {
        return graphQL.execute(query, variables);
    }

    public ExecutionResult execute(ExecutionInput input) {
        return graphQL.execute(input);
    }

    // Resolver methods
    private Map<String, Object> getUserById(String id) {
        return Map.of("id", id, "name", "User " + id, "email", id + "@test.com");
    }

    private List<Map<String, Object>> getAllUsers() {
        return List.of(
            Map.of("id", "1", "name", "Alice", "email", "alice@test.com"),
            Map.of("id", "2", "name", "Bob", "email", "bob@test.com")
        );
    }

    private List<Map<String, Object>> getPosts(String userId) {
        return List.of(
            Map.of("id", "p1", "title", "Post 1", "content", "Content 1", "authorId", userId != null ? userId : "1"),
            Map.of("id", "p2", "title", "Post 2", "content", "Content 2", "authorId", userId != null ? userId : "1")
        );
    }

    private List<Map<String, Object>> getPostsByUser(Map<String, Object> user) {
        return getPosts((String) user.get("id"));
    }

    // Mutation support
    public void addMutation(GraphQLObjectType mutationType) {
        // Add mutation support
    }

    // Subscription support
    public void addSubscription(GraphQLObjectType subscriptionType) {
        // Add subscription support
    }

    // DataLoader for N+1 prevention
    public Map<String, Object> batchLoadUsers(List<String> keys) {
        return keys.stream().collect(Collectors.toMap(k -> k, k -> getUserById(k)));
    }
}