package com.arch.apicomposition;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GraphQLAggregator {
    private final Map<String, TypeResolver> resolvers = new ConcurrentHashMap<>();

    public void registerResolver(String typeName, TypeResolver resolver) {
        resolvers.put(typeName, resolver);
    }

    public Map<String, Object> resolve(String query, Map<String, Object> variables) {
        Map<String, Object> result = new HashMap<>();
        resolvers.forEach((type, resolver) -> {
            if (query.contains(type)) {
                try {
                    result.put(type, resolver.resolve(variables));
                } catch (Exception e) {
                    result.put(type, Map.of("error", e.getMessage()));
                }
            }
        });
        return result;
    }
}

interface TypeResolver {
    Object resolve(Map<String, Object> variables);
}
