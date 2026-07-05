package com.net.graphql;

import java.util.*;

public class GraphQLSchemaBuilder {

    public enum FieldType { STRING, INT, FLOAT, BOOLEAN, ID, LIST, OBJECT }

    public static class FieldDef {
        public final String name;
        public final FieldType type;
        public final String ofType;
        public final boolean nonNull;
        public final String description;

        public FieldDef(String name, FieldType type, boolean nonNull, String description) {
            this(name, type, null, nonNull, description);
        }

        public FieldDef(String name, FieldType type, String ofType, boolean nonNull, String description) {
            this.name = name;
            this.type = type;
            this.ofType = ofType;
            this.nonNull = nonNull;
            this.description = description;
        }

        public String typeString() {
            String t = switch (type) {
                case STRING -> "String";
                case INT -> "Int";
                case FLOAT -> "Float";
                case BOOLEAN -> "Boolean";
                case ID -> "ID";
                case LIST -> "[" + ofType + "]";
                case OBJECT -> ofType;
            };
            return nonNull ? t + "!" : t;
        }
    }

    public static class ObjectType {
        public final String name;
        public final String description;
        public final List<FieldDef> fields = new ArrayList<>();

        public ObjectType(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public ObjectType field(String name, FieldType type, boolean nonNull, String desc) {
            fields.add(new FieldDef(name, type, nonNull, desc));
            return this;
        }

        public ObjectType field(String name, FieldType type, String ofType, boolean nonNull, String desc) {
            fields.add(new FieldDef(name, type, ofType, nonNull, desc));
            return this;
        }
    }

    public static class Schema {
        public final List<ObjectType> types = new ArrayList<>();
        public ObjectType query;
        public ObjectType mutation;

        public ObjectType addType(String name, String description) {
            ObjectType t = new ObjectType(name, description);
            types.add(t);
            return t;
        }

        public void setQuery(ObjectType q) { this.query = q; }
        public void setMutation(ObjectType m) { this.mutation = m; }

        public String build() {
            StringBuilder sb = new StringBuilder();
            if (query != null) sb.append("type Query {\n");
            if (query != null) {
                for (FieldDef f : query.fields) {
                    sb.append("  ").append(f.name).append(": ").append(f.typeString()).append("\n");
                }
                sb.append("}\n\n");
            }
            for (ObjectType t : types) {
                sb.append("type ").append(t.name).append(" {\n");
                for (FieldDef f : t.fields) {
                    sb.append("  ").append(f.name).append(": ").append(f.typeString()).append("\n");
                }
                sb.append("}\n\n");
            }
            if (mutation != null) {
                sb.append("type Mutation {\n");
                for (FieldDef f : mutation.fields) {
                    sb.append("  ").append(f.name).append(": ").append(f.typeString()).append("\n");
                }
                sb.append("}\n");
            }
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        Schema schema = new Schema();

        ObjectType userType = schema.addType("User", "A system user")
            .field("id", FieldType.ID, true, "Unique identifier")
            .field("name", FieldType.STRING, true, "Display name")
            .field("email", FieldType.STRING, false, "Email address")
            .field("posts", FieldType.LIST, "Post", false, "User's posts");

        schema.addType("Post", "A blog post")
            .field("id", FieldType.ID, true, "Post ID")
            .field("title", FieldType.STRING, true, "Post title")
            .field("content", FieldType.STRING, true, "Post content");

        ObjectType query = new ObjectType("Query", "")
            .field("user", FieldType.OBJECT, "User", true, "Get user by ID")
            .field("users", FieldType.LIST, "User", false, "List all users");

        schema.setQuery(query);

        System.out.println("=== GraphQL Schema ===");
        System.out.println(schema.build());
    }
}
