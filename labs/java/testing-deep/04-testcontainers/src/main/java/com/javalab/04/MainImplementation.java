package com.javalab.04;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MainImplementation {
    
    public static class User { 
        public int id; 
        public String name; 
        public String email;
        public User() {}
        public User(int id, String name, String email) { this.id = id; this.name = name; this.email = email; }
    }
    
    public void createTable(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY, name VARCHAR(100), email VARCHAR(100))");
        }
    }
    
    public void insertUser(Connection conn, User user) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO users VALUES (?, ?, ?)")) {
            ps.setInt(1, user.id); ps.setString(2, user.name); ps.setString(3, user.email);
            ps.executeUpdate();
        }
    }
    
    public List<User> getAllUsers(Connection conn) throws SQLException {
        List<User> users = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
            while (rs.next()) users.add(new User(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
        }
        return users;
    }
}
