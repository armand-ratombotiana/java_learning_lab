# Module 19: Database Access with JDBC - Mini Project

**Project Name**: Simple Contact Manager CLI  
**Difficulty Level**: Intermediate  
**Estimated Time**: 2-3 hours

---

## 🎯 Objective
Build a command-line application that uses pure JDBC to perform CRUD (Create, Read, Update, Delete) operations on a relational database while ensuring safe transaction management and avoiding SQL injection.

## 📝 Requirements

### Core Features
1. **Database Setup**:
   - Write a Java initialization method that connects to an H2 in-memory database (or local MySQL/PostgreSQL).
   - Execute a `CREATE TABLE IF NOT EXISTS contacts (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100), phone VARCHAR(20))` statement via a plain `Statement`.

2. **CRUD Operations (PreparedStatements)**:
   - Create a `ContactDao` class.
   - Implement `void addContact(String name, String phone)` using a `PreparedStatement`.
   - Implement `void deleteContact(int id)` using a `PreparedStatement`.
   - Implement `List<Contact> getAllContacts()` using a `PreparedStatement` and a `ResultSet`.

3. **Transaction Management**:
   - Implement a method `void transferContacts(int fromId, int toId)` representing a complex operation.
   - Begin by setting `conn.setAutoCommit(false)`.
   - Update the phone number of the target, delete the original, and commit. 
   - If any `SQLException` occurs, catch it, log it, and explicitly call `conn.rollback()`.

4. **Resource Management**:
   - Use `try-with-resources` for all connections, statements, and result sets to prevent resource leaks.

---

## 💡 Solution Blueprint

```java
public class ContactDao {
    private String url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, "sa", "");
    }

    public void addContact(String name, String phone) {
        String sql = "INSERT INTO contacts (name, phone) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void executeTransaction() {
        String updateSql = "UPDATE contacts ...";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                // ... execute operations
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```