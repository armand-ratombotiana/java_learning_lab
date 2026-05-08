package com.learning.messaging;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== ActiveMQ Concepts ===\n");

        demonstrateJMSModel();
        demonstrateMessageTypes();
        demonstrateBrokerTopologies();
        demonstratePersistence();
        demonstrateNetworking();
    }

    private static void demonstrateJMSModel() {
        System.out.println("--- JMS (Java Message Service) Model ---");
        System.out.println("ConnectionFactory -> Connection -> Session -> MessageProducer/Consumer");
        System.out.println();
        System.out.println("Publish/Subscribe (Topic):");
        System.out.println("  Topic = 1-to-many broadcast, non-durable subscriber loses messages");
        System.out.println("  Durable subscriber -> broker stores messages until subscriber retrieves");
        System.out.println();
        System.out.println("Point-to-Point (Queue):");
        System.out.println("  Queue = 1-to-1, message consumed by exactly one consumer");
        System.out.println("  Load-balanced across competing consumers");
        System.out.println("  Messages stay until consumed (persistent queues)");
    }

    private static void demonstrateMessageTypes() {
        System.out.println("\n--- JMS Message Types ---");
        System.out.println("TextMessage   -> String payload (e.g., JSON, XML)");
        System.out.println("BytesMessage  -> Raw byte array");
        System.out.println("MapMessage    -> Set of name-value pairs");
        System.out.println("ObjectMessage -> Serialized Java object");
        System.out.println("StreamMessage -> Stream of primitive Java types");
        System.out.println();
        System.out.println("Message headers: JMSDestination, JMSMessageID, JMSPriority");
        System.out.println("  JMSDeliveryMode (PERSISTENT/NON_PERSISTENT), JMSExpiration");
    }

    private static void demonstrateBrokerTopologies() {
        System.out.println("\n--- Broker Topologies ---");
        System.out.println("1. Standalone      -> Single broker instance");
        System.out.println("2. Master/Slave    -> Shared storage, failover");
        System.out.println("   Replicated LevelDB store with ZooKeeper coordination");
        System.out.println("3. Network of Brokers -> Brokers connected via network connectors");
        System.out.println("   Messages forwarded between brokers as needed");
        System.out.println("4. Broker Cluster  -> Multiple brokers acting as one logical unit");
    }

    private static void demonstratePersistence() {
        System.out.println("\n--- Message Persistence ---");
        System.out.println("KahaDB  = Default file-based persistent store (journal + BTree index)");
        System.out.println("  Fast writes via append-only journal");
        System.out.println("  Periodic checkpoint to BTree index");
        System.out.println();
        System.out.println("AMQ Store = Old file-based store (deprecated)");
        System.out.println("JDBC Store  = Persist to relational DB (Oracle, MySQL, Postgres)");
        System.out.println("LevelDB Store = High-performance based on Google LevelDB");
        System.out.println();
        System.out.println("Non-persistent messages exist only in memory");
    }

    private static void demonstrateNetworking() {
        System.out.println("\n--- Networking & Protocols ---");
        System.out.println("OpenWire  = Native, high-performance binary protocol");
        System.out.println("STOMP     = Simple Text Oriented Messaging Protocol");
        System.out.println("AMQP      = Advanced Message Queuing Protocol (v1.0)");
        System.out.println("MQTT      = Lightweight IoT protocol");
        System.out.println("REST/HTTP = HTTP-based messaging API");
        System.out.println();
        System.out.println("SSL/TLS support for encrypted transport");
        System.out.println("Authentication via JAAS, LDAP, or simple auth plugin");
    }
}
