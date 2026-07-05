package com.cloud.database;

import java.util.*;
import java.util.concurrent.*;

public class DynamoDbCrud {

    public static class Item {
        public final Map<String, Object> attributes = new LinkedHashMap<>();

        public Item put(String key, Object value) {
            attributes.put(key, value);
            return this;
        }

        public Object get(String key) { return attributes.get(key); }

        @Override
        public String toString() { return attributes.toString(); }
    }

    public static class DynamoDBTable {
        private final String tableName;
        private final String partitionKey;
        private final String sortKey;
        private final Map<String, Item> items = new ConcurrentHashMap<>();
        private final Map<String, Map<String, Object>> secondaryIndex = new ConcurrentHashMap<>();

        public DynamoDBTable(String tableName, String partitionKey, String sortKey) {
            this.tableName = tableName;
            this.partitionKey = partitionKey;
            this.sortKey = sortKey;
            System.out.println("Created table: " + tableName + " (PK=" + partitionKey + ", SK=" + sortKey + ")");
        }

        public void putItem(Item item) {
            Object pk = item.get(partitionKey);
            Object sk = item.get(sortKey);
            String key = pk + ":" + sk;
            items.put(key, item);
            System.out.println("Put item in " + tableName + ": " + key);
        }

        public Optional<Item> getItem(Object pk, Object sk) {
            Item item = items.get(pk + ":" + sk);
            return Optional.ofNullable(item);
        }

        public Item updateItem(Object pk, Object sk, String updateExpression, Object value) {
            String key = pk + ":" + sk;
            Item item = items.computeIfAbsent(key, k -> new Item().put(partitionKey, pk).put(sortKey, sk));
            item.put(updateExpression, value);
            System.out.println("Updated " + key + ": " + updateExpression + " = " + value);
            return item;
        }

        public void deleteItem(Object pk, Object sk) {
            items.remove(pk + ":" + sk);
            System.out.println("Deleted: " + pk + ":" + sk);
        }

        public List<Item> scan() {
            return new ArrayList<>(items.values());
        }

        public int size() { return items.size(); }
    }

    public static void main(String[] args) {
        DynamoDBTable table = new DynamoDBTable("Users", "userId", "sortKey");

        System.out.println("=== DynamoDB CRUD ===");

        table.putItem(new Item().put("userId", "u1").put("sortKey", "profile")
            .put("name", "Alice").put("email", "alice@test.com"));

        table.putItem(new Item().put("userId", "u2").put("sortKey", "profile")
            .put("name", "Bob").put("email", "bob@test.com"));

        table.getItem("u1", "profile").ifPresent(i -> System.out.println("Got: " + i));

        table.updateItem("u1", "profile", "status", "active");

        System.out.println("\nAll items (" + table.size() + "):");
        table.scan().forEach(i -> System.out.println("  " + i));
    }
}
