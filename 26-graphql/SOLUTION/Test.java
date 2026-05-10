package solution;

import graphql.ExecutionResult;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GraphQLSolutionTest {

    private GraphQLSolution solution;

    @BeforeEach
    void setUp() {
        solution = new GraphQLSolution();
        solution.buildSchema();
    }

    @Test
    void testSchemaCreation() {
        assertNotNull(solution.getSchema());
    }

    @Test
    void testSimpleQuery() {
        ExecutionResult result = solution.execute("{ user(id: \"1\") { id name } }");
        assertTrue(result.getErrors().isEmpty());
        Map<String, Object> data = result.getData();
        assertNotNull(data);
    }

    @Test
    void testQueryWithVariables() {
        String query = "query($id: String!) { user(id: $id) { id name email } }";
        Map<String, Object> variables = Map.of("id", "1");
        ExecutionResult result = solution.execute(query, variables);
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testQueryWithAlias() {
        String query = "{ user1: user(id: \"1\") { id } user2: user(id: \"2\") { id } }";
        ExecutionResult result = solution.execute(query);
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testFragmentQuery() {
        String query = "{ user(id: \"1\") { ...UserFields } } fragment UserFields on User { id name email }";
        ExecutionResult result = solution.execute(query);
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testListQuery() {
        String query = "{ users { id name } }";
        ExecutionResult result = solution.execute(query);
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testNestedQuery() {
        String query = "{ user(id: \"1\") { id name posts { id title } } }";
        ExecutionResult result = solution.execute(query);
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testQueryWithArgument() {
        String query = "{ posts(userId: \"1\") { id title } }";
        ExecutionResult result = solution.execute(query);
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testSchemaIntrospection() {
        String query = "{ __schema { types { name } } }";
        ExecutionResult result = solution.execute(query);
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testDataLoader() {
        List<String> keys = List.of("1", "2", "3");
        Map<String, Object> result = solution.batchLoadUsers(keys);
        assertEquals(3, result.size());
    }
}