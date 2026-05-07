package com.learning.vertx.database;

public class DatabaseIntegrationLab {

    public static void main(String[] args) {
        System.out.println("=== Vert.x Database Integration Lab ===\n");

        System.out.println("1. Vert.x SQL Client (Reactive):");
        System.out.println("   PgPool pool = PgPool.pool(connectOptions, poolOptions);");
        System.out.println("   pool.query(\"SELECT * FROM users\").execute()");
        System.out.println("       .onSuccess(rows -> {");
        System.out.println("           for (Row row : rows) {");
        System.out.println("               System.out.println(row.getString(\"name\"));");
        System.out.println("           }");
        System.out.println("       })");
        System.out.println("       .onFailure(err -> System.err.println(err.getMessage()));");

        System.out.println("\n2. Prepared Statements:");
        System.out.println("   pool.preparedQuery(");
        System.out.println("       \"INSERT INTO users (name, email) VALUES ($1, $2) RETURNING id\")");
        System.out.println("       .execute(Tuple.of(\"Alice\", \"alice@example.com\"))");
        System.out.println("       .onSuccess(row -> System.out.println(\"Created: \" + row.iterator().next().getLong(0)));");

        System.out.println("\n3. Transaction Support:");
        System.out.println("   pool.withTransaction(conn -> {");
        System.out.println("       return conn.preparedQuery(\"UPDATE accounts SET balance = balance - 100\")");
        System.out.println("           .execute();");
        System.out.println("   });");

        System.out.println("\n4. Redis Client:");
        System.out.println("   RedisClient redis = RedisClient.create(vertx, redisOptions);");
        System.out.println("   redis.set(\"key\", \"value\").onSuccess(v -> {");
        System.out.println("       redis.get(\"key\").onSuccess(val -> {");
        System.out.println("           System.out.println(\"Got: \" + val);");
        System.out.println("       });");
        System.out.println("   });");

        System.out.println("\n5. MongoDB Client:");
        System.out.println("   MongoClient mongo = MongoClient.create(vertx, config);");
        System.out.println("   mongo.find(\"users\", new JsonObject().put(\"name\", \"Alice\"))");
        System.out.println("       .onSuccess(results -> {");
        System.out.println("           results.forEach(System.out::println);");
        System.out.println("       });");

        System.out.println("\n6. Connection Pool:");
        System.out.println("   PoolOptions poolOptions = new PoolOptions()");
        System.out.println("       .setMaxSize(10);");
        System.out.println("   PgPool pool = PgPool.pool(connectOptions, poolOptions);");

        System.out.println("\n7. Reactive vs Traditional:");
        System.out.println("   Traditional JDBC: Blocking I/O, thread-per-connection");
        System.out.println("   Vert.x SQL Client: Non-blocking, event-loop based");
        System.out.println("   Both work with: PostgreSQL, MySQL, MariaDB, MSSQL");

        System.out.println("\n=== Vert.x Database Integration Lab Complete ===");
    }
}