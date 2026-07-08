package com.databases.cockroachdb;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DistributedSQLClientTest {
    @Test void shouldCreateClient() {
        var client = new DistributedSQLClient("jdbc:postgresql://localhost:26257/test", "root", "");
        assertNotNull(client);
        client.close();
    }

    @Test void shouldHandleDDL() {
        var client = new DistributedSQLClient("jdbc:postgresql://localhost:26257/test", "root", "");
        assertDoesNotThrow(() -> client.executeDDL("CREATE TABLE IF NOT EXISTS test (id INT PRIMARY KEY)"));
        client.close();
    }
}
