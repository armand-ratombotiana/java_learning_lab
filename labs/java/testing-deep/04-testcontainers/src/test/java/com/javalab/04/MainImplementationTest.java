package com.javalab.04;
import org.junit.jupiter.api.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

class MainImplementationTest {
    private MainImplementation repo;
    @BeforeEach void setUp() { repo = new MainImplementation(); }
    @Test @DisplayName("Should create table and insert users")
    void testDatabaseOperations() throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test")) {
            repo.createTable(conn);
            repo.insertUser(conn, new MainImplementation.User(1, "Alice", "alice@test.com"));
            repo.insertUser(conn, new MainImplementation.User(2, "Bob", "bob@test.com"));
            var users = repo.getAllUsers(conn);
            assertEquals(2, users.size());
            assertEquals("Alice", users.get(0).name);
        }
    }
}
