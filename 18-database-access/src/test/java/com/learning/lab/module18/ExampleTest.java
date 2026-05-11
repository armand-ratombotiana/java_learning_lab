package com.learning.lab.module18;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ExampleTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private Statement statement;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test JDBC connection establishment")
    void testConnectionEstablishment() throws SQLException {
        when(connection.isClosed()).thenReturn(false);

        assertFalse(connection.isClosed());
    }

    @Test
    @DisplayName("Test PreparedStatement with parameters")
    void testPreparedStatementWithParams() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, "John");
        ps.setString(2, "john@example.com");

        int rows = ps.executeUpdate();

        assertEquals(1, rows);
        verify(preparedStatement).setString(1, "John");
        verify(preparedStatement).setString(2, "john@example.com");
    }

    @Test
    @DisplayName("Test ResultSet navigation")
    void testResultSetNavigation() throws SQLException {
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getString("name")).thenReturn("John").thenReturn("Jane");
        when(resultSet.getInt("id")).thenReturn(1).thenReturn(2);

        List<String> names = new ArrayList<>();
        while (resultSet.next()) {
            names.add(resultSet.getString("name"));
        }

        assertEquals(2, names.size());
        assertEquals("John", names.get(0));
        assertEquals("Jane", names.get(1));
    }

    @Test
    @DisplayName("Test SQL SELECT query execution")
    void testSelectQuery() throws SQLException {
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("name")).thenReturn("John");
        when(resultSet.getString("email")).thenReturn("john@example.com");

        ResultSet rs = statement.executeQuery("SELECT * FROM users");

        assertTrue(rs.next());
        assertEquals(1L, rs.getLong("id"));
        assertEquals("John", rs.getString("name"));
    }

    @Test
    @DisplayName("Test batch processing")
    void testBatchProcessing() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeBatch()).thenReturn(new int[]{1, 1, 1});

        preparedStatement.setString(1, "Alice");
        preparedStatement.addBatch();
        preparedStatement.setString(1, "Bob");
        preparedStatement.addBatch();

        int[] results = preparedStatement.executeBatch();

        assertEquals(3, results.length);
    }

    @Test
    @DisplayName("Test transaction management")
    void testTransactionManagement() throws SQLException {
        when(connection.setAutoCommit(anyBoolean())).thenReturn(null);
        when(connection.commit()).thenReturn(null);
        when(connection.rollback()).thenReturn(null);

        connection.setAutoCommit(false);
        connection.commit();
        connection.setAutoCommit(true);

        verify(connection).setAutoCommit(false);
        verify(connection).commit();
    }

    @Test
    @DisplayName("Test savepoint transaction control")
    void testSavepoint() throws SQLException {
        Savepoint savepoint = mock(Savepoint.class);
        when(connection.setSavepoint("before_update")).thenReturn(savepoint);
        when(connection.rollback(savepoint)).thenReturn(null);

        Savepoint sp = connection.setSavepoint("before_update");
        connection.rollback(sp);

        verify(connection).setSavepoint("before_update");
        verify(connection).rollback(sp);
    }

    @Test
    @DisplayName("Test connection pooling configuration")
    void testConnectionPoolingConfig() {
        int maxPoolSize = 10;
        int minIdle = 5;
        long connectionTimeout = 30000L;

        assertEquals(10, maxPoolSize);
        assertEquals(5, minIdle);
        assertEquals(30000L, connectionTimeout);
    }

    @Test
    @DisplayName("Test PreparedStatement parameter binding")
    void testParameterBinding() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        PreparedStatement ps = connection.prepareStatement(
            "UPDATE users SET name = ? WHERE id = ?"
        );

        ps.setString(1, "Updated Name");
        ps.setInt(2, 1);

        verify(preparedStatement).setString(1, "Updated Name");
        verify(preparedStatement).setInt(2, 1);
    }

    @Test
    @DisplayName("Test delete operation")
    void testDeleteOperation() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        PreparedStatement ps = connection.prepareStatement(
            "DELETE FROM users WHERE id = ?"
        );
        ps.setInt(1, 1);

        int deleted = ps.executeUpdate();

        assertEquals(1, deleted);
    }

    static class User {
        Long id;
        String name;
        String email;

        User(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
    }
}