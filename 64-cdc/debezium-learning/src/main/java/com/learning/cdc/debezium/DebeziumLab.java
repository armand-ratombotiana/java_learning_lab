package com.learning.cdc.debezium;

public class DebeziumLab {

    public static void main(String[] args) {
        System.out.println("=== Debezium CDC Lab ===\n");

        System.out.println("1. CDC Connector Configuration:");
        System.out.println("   {");
        System.out.println("     \"name\": \"postgres-connector\",");
        System.out.println("     \"config\": {");
        System.out.println("       \"connector.class\": \"io.debezium.connector.postgresql.PostgresConnector\",");
        System.out.println("       \"database.hostname\": \"localhost\",");
        System.out.println("       \"database.port\": \"5432\",");
        System.out.println("       \"database.dbname\": \"mydb\",");
        System.out.println("       \"topic.prefix\": \"db\"");
        System.out.println("     }");
        System.out.println("   }");

        System.out.println("\n2. CDC Events:");
        System.out.println("   INSERT: users -> {\"id\":1,\"name\":\"Alice\"}");
        System.out.println("   UPDATE: orders -> {\"id\":100,\"status\":\"shipped\"}");
        System.out.println("   DELETE: products -> {\"id\":50}");

        System.out.println("\n=== Debezium CDC Lab Complete ===");
    }
}