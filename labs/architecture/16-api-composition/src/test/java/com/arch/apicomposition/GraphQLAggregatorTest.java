package com.arch.apicomposition;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class GraphQLAggregatorTest {
    @Test
    void shouldResolveRegisteredTypes() {
        GraphQLAggregator agg = new GraphQLAggregator();
        agg.registerResolver("User", vars -> Map.of("name", "Alice"));
        Map<String, Object> result = agg.resolve("{ User { name } }", Map.of());
        assertTrue(result.containsKey("User"));
    }

    @Test
    void shouldHandleMissingResolvers() {
        GraphQLAggregator agg = new GraphQLAggregator();
        Map<String, Object> result = agg.resolve("{ Unknown }", Map.of());
        assertTrue(result.isEmpty());
    }
}
