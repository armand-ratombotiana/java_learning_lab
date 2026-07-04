# Refactoring: No Pool → HikariCP

## Without Connection Pool (Anti-Pattern)

### Before
```java
@Service
public class UserService {
    @Value("${db.url}") private String url;
    @Value("${db.user}") private String user;
    @Value("${db.password}") private String password;

    public User findById(Long id) throws SQLException {
        // Creates a NEW connection EVERY request!
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? mapUser(rs) : null;
        }
    }
}
```

### After
```java
@Service
public class UserService {
    private final DataSource dataSource;

    public UserService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public User findById(Long id) throws SQLException {
        try (Connection conn = dataSource.getConnection();  // from pool!
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? mapUser(rs) : null;
        }
    }
}
```

## DBCP/c3p0 → HikariCP Migration
```properties
# Before (DBCP)
spring.datasource.type=org.apache.tomcat.jdbc.pool.DataSource

# After (HikariCP)
spring.datasource.type=com.zaxxer.hikari.HikariDataSource
```
Simply changing the type is often sufficient in Spring Boot.
