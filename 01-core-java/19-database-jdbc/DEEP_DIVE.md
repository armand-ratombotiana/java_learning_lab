# Module 19: Database Access with JDBC - Deep Dive

**Difficulty Level**: Intermediate to Advanced  
**Prerequisites**: Modules 01-18  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Introduction to JDBC](#intro)
2. [Connecting to a Database](#connecting)
3. [Executing Statements](#statements)
4. [Using PreparedStatement](#preparedstatements)
5. [Transaction Management](#transactions)

---

## 1. Introduction to JDBC <a name="intro"></a>
Java Database Connectivity (JDBC) is a standard Java API for database-independent connectivity between the Java programming language and a wide range of databases.

---

## 2. Connecting to a Database <a name="connecting"></a>
To connect to a database, you need the appropriate JDBC driver and the `DriverManager`.

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConnection {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/mydb";
        String user = "root";
        String password = "password";
        return DriverManager.getConnection(url, user, password);
    }
}
```

---

## 3. Executing Statements <a name="statements"></a>
You can use `Statement` for executing static SQL queries.

```java
try (Connection conn = getConnection();
     Statement stmt = conn.createStatement();
     ResultSet rs = stmt.executeQuery("SELECT id, name FROM users")) {
    
    while (rs.next()) {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        System.out.println("User: " + id + " - " + name);
    }
}
```

---

## 4. Using PreparedStatement <a name="preparedstatements"></a>
`PreparedStatement` is precompiled and protects against SQL injection.

```java
String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
try (Connection conn = getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
    pstmt.setString(1, "Alice");
    pstmt.setString(2, "alice@example.com");
    int rowsAffected = pstmt.executeUpdate();
    System.out.println(rowsAffected + " row(s) inserted.");
}
```

---

## 5. Transaction Management <a name="transactions"></a>
JDBC manages transactions natively. Auto-commit is enabled by default.

```java
try (Connection conn = getConnection()) {
    conn.setAutoCommit(false); // Start transaction
    
    try (PreparedStatement pstmt1 = conn.prepareStatement("UPDATE accounts SET balance = balance - 100 WHERE id = 1");
         PreparedStatement pstmt2 = conn.prepareStatement("UPDATE accounts SET balance = balance + 100 WHERE id = 2")) {
        
        pstmt1.executeUpdate();
        pstmt2.executeUpdate();
        
        conn.commit(); // Commit transaction
    } catch (SQLException e) {
        conn.rollback(); // Rollback on error
        throw e;
    }
}
```