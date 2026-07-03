# Code Deep Dive: Enums

## Enum-Based State Machine
```java
public enum OrderState {
    NEW {
        OrderState next() { return PROCESSING; }
    },
    PROCESSING {
        OrderState next() { return SHIPPED; }
    },
    SHIPPED {
        OrderState next() { return DELIVERED; }
    },
    DELIVERED {
        OrderState next() { return this; }
    },
    CANCELLED {
        OrderState next() { return this; }
    };
    
    abstract OrderState next();
    public boolean canCancel() { return this == NEW || this == PROCESSING; }
}
```

## Enum with Strategy Pattern
```java
public enum DiscountStrategy {
    NONE { double apply(double price) { return price; } },
    MEMBER { double apply(double price) { return price * 0.9; } },
    VIP { double apply(double price) { return price * 0.8; } },
    CLEARANCE { double apply(double price) { return price * 0.5; } };
    
    abstract double apply(double price);
}
```

## EnumMap Configuration
```java
public enum LogLevel { DEBUG, INFO, WARN, ERROR }

public class LoggerConfig {
    private final EnumMap<LogLevel, List<Appender>> appenders = new EnumMap<>(LogLevel.class);
    
    public void addAppender(LogLevel level, Appender appender) {
        appenders.computeIfAbsent(level, k -> new ArrayList<>()).add(appender);
    }
    
    public void log(LogLevel level, String message) {
        List<Appender> apps = appenders.get(level);
        if (apps != null) {
            apps.forEach(a -> a.append(level, message));
        }
    }
}
```

## Enum Singleton
```java
public enum DatabaseConnectionPool {
    INSTANCE;
    
    private final HikariDataSource dataSource;
    
    DatabaseConnectionPool() {
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost/mydb");
        dataSource.setMaximumPoolSize(10);
    }
    
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
```
