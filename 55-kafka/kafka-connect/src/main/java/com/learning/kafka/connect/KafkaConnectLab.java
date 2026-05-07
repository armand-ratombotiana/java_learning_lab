package com.learning.kafka.connect;

public class KafkaConnectLab {

    public static void main(String[] args) {
        System.out.println("=== Kafka Connect Lab ===\n");

        System.out.println("1. Source Connector Example:");
        System.out.println("   POST /connectors");
        System.out.println("   {");
        System.out.println("     \"name\": \"jdbc-source\",");
        System.out.println("     \"config\": {");
        System.out.println("       \"connector.class\": \"JdbcSourceConnector\",");
        System.out.println("       \"connection.url\": \"jdbc:postgresql://localhost:5432/mydb\",");
        System.out.println("       \"mode\": \"incrementing\",");
        System.out.println("       \"incrementing.column.name\": \"id\",");
        System.out.println("       \"topic.prefix\": \"db-\"");
        System.out.println("     }");
        System.out.println("   }");

        System.out.println("\n2. Sink Connector Example:");
        System.out.println("   {");
        System.out.println("     \"name\": \"elasticsearch-sink\",");
        System.out.println("     \"config\": {");
        System.out.println("       \"connector.class\": \"ElasticsearchSinkConnector\",");
        System.out.println("       \"topics\": \"db-users\",");
        System.out.println("       \"connection.url\": \"http://localhost:9200\"");
        System.out.println("     }");
        System.out.println("   }");

        System.out.println("\n=== Kafka Connect Lab Complete ===");
    }
}