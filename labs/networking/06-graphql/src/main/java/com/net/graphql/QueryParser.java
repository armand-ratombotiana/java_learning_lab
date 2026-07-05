package com.net.graphql;

import java.util.*;

public class QueryParser {

    public static class ParsedQuery {
        public final String operationType;
        public final String operationName;
        public final Map<String, String> arguments;
        public final List<String> fields;

        public ParsedQuery(String type, String name, Map<String, String> args, List<String> fields) {
            this.operationType = type;
            this.operationName = name;
            this.arguments = args;
            this.fields = fields;
        }

        @Override
        public String toString() {
            return operationType + " " + operationName + args + " { " + fields + " }";
        }
    }

    public static class Resolver {
        private final Map<String, Object> data = new HashMap<>();

        public Resolver() {
            data.put("user:1", Map.of("id", "1", "name", "Alice", "email", "alice@test.com"));
            data.put("user:2", Map.of("id", "2", "name", "Bob", "email", "bob@test.com"));
        }

        public Map<String, Object> resolve(ParsedQuery query) {
            System.out.println("Resolving: " + query);
            Map<String, Object> result = new LinkedHashMap<>();

            if ("user".equals(query.operationName)) {
                String id = query.arguments.get("id");
                Object userData = data.get("user:" + id);
                if (userData instanceof Map) {
                    Map<?, ?> userMap = (Map<?, ?>) userData;
                    for (String field : query.fields) {
                        result.put(field, userMap.get(field));
                    }
                }
            }
            return result;
        }
    }

    public static ParsedQuery parseQuery(String queryStr) {
        String type = "query";
        String name = "unknown";
        Map<String, String> args = new HashMap<>();
        List<String> fields = new ArrayList<>();

        if (queryStr.contains("user")) name = "user";
        if (queryStr.contains("posts")) name = "posts";

        if (queryStr.contains("id:")) {
            int start = queryStr.indexOf("id:") + 3;
            int end = queryStr.indexOf(")", start);
            if (end == -1) end = queryStr.length();
            args.put("id", queryStr.substring(start, end).trim().replace("\"", ""));
        }

        int braceStart = queryStr.lastIndexOf("{");
        int braceEnd = queryStr.lastIndexOf("}");
        if (braceStart >= 0 && braceEnd > braceStart) {
            String fieldsStr = queryStr.substring(braceStart + 1, braceEnd).trim();
            for (String f : fieldsStr.split("\\s+")) {
                if (!f.isEmpty() && !f.equals("user")) {
                    fields.add(f);
                }
            }
        }

        return new ParsedQuery(type, name, args, fields);
    }

    public static void main(String[] args) {
        String query = "query user(id: \"1\") { name email }";
        ParsedQuery parsed = parseQuery(query);
        System.out.println("Parsed: " + parsed);

        Resolver resolver = new Resolver();
        Map<String, Object> result = resolver.resolve(parsed);
        System.out.println("Resolved: " + result);
    }
}
