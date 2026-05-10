package com.learning.lab.module18.solution;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.sql.*;
import java.util.List;

public class Test {

    @Test void testJdbcConnectionUrl() { Solution.JdbcConnection conn = new Solution.JdbcConnection("jdbc:mysql://localhost", "user", "pass"); assertEquals("jdbc:mysql://localhost", conn.getUrl()); }
    @Test void testHikariDataSourceCreation() { Solution.HikariDataSource ds = new Solution.HikariDataSource("jdbc:h2:mem:test", "sa", "", 10, 5); assertNotNull(ds); }
    @Test void testHikariDataSourceClose() { Solution.HikariDataSource ds = new Solution.HikariDataSource("jdbc:h2:mem:test", "sa", "", 10, 5); ds.close(); }
    @Test void testUserRowMapper() throws Exception { Solution.UserRowMapper mapper = new Solution.UserRowMapper(); Connection conn = DriverManager.getConnection("jdbc:h2:mem:test", "sa", ""); Statement stmt = conn.createStatement(); stmt.execute("CREATE TABLE users (id INT, name VARCHAR, email VARCHAR)"); stmt.execute("INSERT INTO users VALUES (1, 'John', 'john@test.com')"); ResultSet rs = stmt.executeQuery("SELECT * FROM users"); rs.next(); Solution.User u = mapper.mapRow(rs, 1); assertEquals("John", u.getName()); conn.close(); }
    @Test void testProductRowMapper() throws Exception { Solution.ProductRowMapper mapper = new Solution.ProductRowMapper(); Connection conn = DriverManager.getConnection("jdbc:h2:mem:test", "sa", ""); Statement stmt = conn.createStatement(); stmt.execute("CREATE TABLE products (id INT, name VARCHAR, price DECIMAL)"); stmt.execute("INSERT INTO products VALUES (1, 'Widget', 9.99)"); ResultSet rs = stmt.executeQuery("SELECT * FROM products"); rs.next(); Solution.Product p = mapper.mapRow(rs, 1); assertEquals("Widget", p.getName()); conn.close(); }
    @Test void testJdbcTemplateCreation() { Solution.HikariDataSource ds = new Solution.HikariDataSource("jdbc:h2:mem:test", "sa", "", 10, 5); Solution.JdbcTemplate template = new Solution.JdbcTemplate(ds); assertNotNull(template); }
    @Test void testJdbcTransactionManagerCreation() { Solution.HikariDataSource ds = new Solution.HikariDataSource("jdbc:h2:mem:test", "sa", "", 10, 5); Solution.JdbcTransactionManager tx = new Solution.JdbcTransactionManager(ds); assertNotNull(tx); }
    @Test void testJdbcTransactionManagerBegin() { Solution.HikariDataSource ds = new Solution.HikariDataSource("jdbc:h2:mem:test", "sa", "", 10, 5); Solution.JdbcTransactionManager tx = new Solution.JdbcTransactionManager(ds); tx.begin(); assertTrue(tx.isInTransaction()); }
    @Test void testJdbcTransactionManagerCommit() { Solution.HikariDataSource ds = new Solution.HikariDataSource("jdbc:h2:mem:test", "sa", "", 10, 5); Solution.JdbcTransactionManager tx = new Solution.JdbcTransactionManager(ds); tx.begin(); tx.commit(); assertFalse(tx.isInTransaction()); }
    @Test void testJdbcTransactionManagerRollback() { Solution.HikariDataSource ds = new Solution.HikariDataSource("jdbc:h2:mem:test", "sa", "", 10, 5); Solution.JdbcTransactionManager tx = new Solution.JdbcTransactionManager(ds); tx.begin(); tx.rollback(); assertFalse(tx.isInTransaction()); }
    @Test void testUserEntitySetters() { Solution.User u = new Solution.User(); u.setId(1L); u.setName("John"); u.setEmail("john@test.com"); assertEquals("John", u.getName()); }
    @Test void testProductEntitySetters() { Solution.Product p = new Solution.Product(); p.setId(1L); p.setName("Widget"); p.setPrice(9.99); assertEquals(9.99, p.getPrice()); }
    @Test void testPoolConfigDefaults() { Solution.PoolConfig config = new Solution.PoolConfig(); assertEquals(10, config.getMaxPoolSize()); assertEquals(5, config.getMinIdle()); }
    @Test void testPoolConfigSetters() { Solution.PoolConfig config = new Solution.PoolConfig(); config.setMaxPoolSize(20); config.setMinIdle(10); assertEquals(20, config.getMaxPoolSize()); }
    @Test void testIsolationLevelConstants() { assertEquals(Connection.TRANSACTION_READ_UNCOMMITTED, Solution.IsolationLevel.READ_UNCOMMITTED); assertEquals(Connection.TRANSACTION_READ_COMMITTED, Solution.IsolationLevel.READ_COMMITTED); }
    @Test void testRowMapperInterface() { assertNotNull(new Solution.UserRowMapper()); assertNotNull(new Solution.ProductRowMapper()); }
    @Test void testDataSourceInterface() { Solution.HikariDataSource ds = new Solution.HikariDataSource("jdbc:h2:mem:test", "sa", "", 10, 5); assertTrue(ds instanceof Solution.DataSource); }
    @Test void testConnectionPooling() { Solution.HikariDataSource ds = new Solution.HikariDataSource("jdbc:h2:mem:test", "sa", "", 10, 2); assertNotNull(ds); }
    @Test void testTransactionManagerInterface() { Solution.HikariDataSource ds = new Solution.HikariDataSource("jdbc:h2:mem:test", "sa", "", 10, 5); Solution.JdbcTransactionManager tx = new Solution.JdbcTransactionManager(ds); assertTrue(tx instanceof Solution.TransactionManager); }
}