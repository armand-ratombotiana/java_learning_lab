package com.net.graphql;

import java.util.*;
import java.util.concurrent.*;

public class Resolver {

    public interface DataFetcher {
        Object fetch(Map<String, Object> args);
    }

    public static class FieldResolver {
        private final Map<String, DataFetcher> fetchers = new ConcurrentHashMap<>();

        public void register(String fieldName, DataFetcher fetcher) {
            fetchers.put(fieldName, fetcher);
        }

        public Object resolve(String fieldName, Map<String, Object> args) {
            DataFetcher fetcher = fetchers.get(fieldName);
            if (fetcher == null) {
                throw new IllegalArgumentException("No resolver for: " + fieldName);
            }
            return fetcher.fetch(args);
        }
    }

    public static class UserResolver {
        private final Map<String, Map<String, Object>> users = new HashMap<>();

        public UserResolver() {
            users.put("1", Map.of("id", "1", "name", "Alice", "email", "alice@ex.com", "age", 30));
            users.put("2", Map.of("id", "2", "name", "Bob", "email", "bob@ex.com", "age", 25));
        }

        public Map<String, Object> getUser(String id) {
            return users.getOrDefault(id, Map.of("id", id, "name", "Unknown"));
        }

        public List<Map<String, Object>> listUsers() {
            return new ArrayList<>(users.values());
        }

        public Map<String, Object> createUser(String name, String email) {
            String id = String.valueOf(users.size() + 1);
            Map<String, Object> user = new HashMap<>(Map.of("id", id, "name", name, "email", email));
            users.put(id, user);
            return user;
        }
    }

    public static void main(String[] args) {
        FieldResolver resolver = new FieldResolver();
        UserResolver userResolver = new UserResolver();

        resolver.register("user", args1 -> {
            String id = (String) args1.get("id");
            return userResolver.getUser(id);
        });

        resolver.register("users", args1 -> userResolver.listUsers());
        resolver.register("createUser", args1 -> {
            String name = (String) args1.get("name");
            String email = (String) args1.get("email");
            return userResolver.createUser(name, email);
        });

        System.out.println("=== GraphQL Resolver ===");
        Object result1 = resolver.resolve("user", Map.of("id", "1"));
        System.out.println("user(id:1) => " + result1);

        Object result2 = resolver.resolve("users", Map.of());
        System.out.println("users => " + result2);

        Object result3 = resolver.resolve("createUser", Map.of("name", "Charlie", "email", "charlie@ex.com"));
        System.out.println("createUser => " + result3);
    }
}
