# Abstraction & Interfaces — Code Deep Dive

## Example 1: Abstract Class

```java
abstract class Database {
    protected String connectionString;
    
    public Database(String connectionString) {
        this.connectionString = connectionString;
    }
    
    // Abstract methods — must be implemented by subclasses
    public abstract void connect();
    public abstract void disconnect();
    public abstract void executeQuery(String sql);
    
    // Concrete method — shared implementation
    public void runMigration() {
        System.out.println("Running migration on " + connectionString);
        connect();
        executeQuery("CREATE TABLE IF NOT EXISTS migrations (id INT)");
        disconnect();
    }
    
    // Static method — utility
    public static Database getDatabase(String type, String connStr) {
        return switch (type.toLowerCase()) {
            case "mysql" -> new MySQLDatabase(connStr);
            case "postgres" -> new PostgresDatabase(connStr);
            default -> throw new IllegalArgumentException("Unknown DB type: " + type);
        };
    }
}

class MySQLDatabase extends Database {
    public MySQLDatabase(String connStr) { super(connStr); }
    
    @Override
    public void connect() { System.out.println("Connecting to MySQL: " + connectionString); }
    
    @Override
    public void disconnect() { System.out.println("Disconnecting from MySQL"); }
    
    @Override
    public void executeQuery(String sql) { System.out.println("MySQL executing: " + sql); }
}

class PostgresDatabase extends Database {
    public PostgresDatabase(String connStr) { super(connStr); }
    
    @Override
    public void connect() { System.out.println("Connecting to PostgreSQL: " + connectionString); }
    
    @Override
    public void disconnect() { System.out.println("Disconnecting from PostgreSQL"); }
    
    @Override
    public void executeQuery(String sql) { System.out.println("PostgreSQL executing: " + sql); }
}

public class AbstractDemo {
    public static void main(String[] args) {
        Database db = Database.getDatabase("mysql", "jdbc:mysql://localhost:3306/mydb");
        db.runMigration();
        
        Database db2 = Database.getDatabase("postgres", "jdbc:postgresql://localhost:5432/mydb");
        db2.runMigration();
    }
}
```

## Example 2: Interfaces with Default Methods

```java
interface Logger {
    // Abstract — must implement
    void log(String message);
    
    // Default — optional override
    default void logInfo(String message) {
        log("[INFO] " + message);
    }
    
    default void logError(String message) {
        log("[ERROR] " + message);
    }
    
    default void logWarning(String message) {
        log("[WARN] " + message);
    }
    
    // Static utility
    static Logger consoleLogger() {
        return message -> System.out.println(message);
    }
    
    static Logger fileLogger(String filename) {
        return message -> {
            // File writing logic here
            System.out.println("Writing to " + filename + ": " + message);
        };
    }
}

class SimpleLogger implements Logger {
    @Override
    public void log(String message) {
        System.out.println(System.currentTimeMillis() + " " + message);
    }
    // Inherits logInfo, logError, logWarning defaults
}

class CustomLogger implements Logger {
    @Override
    public void log(String message) {
        System.out.println("CUSTOM: " + message);
    }
    
    @Override
    public void logError(String message) {
        log("[CRITICAL] " + message);  // Override default
    }
    // logInfo and logWarning still use defaults
}

public class InterfaceDemo {
    public static void main(String[] args) {
        Logger simple = new SimpleLogger();
        simple.logInfo("Application started");
        simple.logError("Null pointer detected");
        
        Logger custom = new CustomLogger();
        custom.logWarning("Memory usage high");
        custom.logError("Database connection failed");
        
        // Static factory methods
        Logger console = Logger.consoleLogger();
        console.logInfo("Console test");
        
        Logger file = Logger.fileLogger("/var/log/app.log");
        file.logInfo("File test");
    }
}
```

## Example 3: Multiple Interface Implementation

```java
interface Readable {
    String read();
}

interface Writable {
    void write(String data);
}

interface Storable extends Readable, Writable {
    // Combines both interfaces — inherits all abstract methods
    default void backup() {
        String data = read();
        System.out.println("Backing up: " + data);
        // write to backup location
    }
}

class FileStore implements Storable {
    private String content = "";
    
    @Override
    public String read() {
        System.out.println("Reading from file");
        return content;
    }
    
    @Override
    public void write(String data) {
        System.out.println("Writing to file: " + data);
        this.content = data;
    }
    // backup() inherited from Storable
}

class DatabaseStore implements Readable, Writable {
    @Override
    public String read() {
        System.out.println("Reading from database");
        return "db data";
    }
    
    @Override
    public void write(String data) {
        System.out.println("Writing to database: " + data);
    }
}

public class MultiInterfaceDemo {
    public static void main(String[] args) {
        Storable file = new FileStore();
        file.write("Hello, World!");
        System.out.println("Read: " + file.read());
        file.backup();
        
        Readable reader = new DatabaseStore();
        String data = reader.read();
        System.out.println("Got: " + data);
    }
}
```

## Example 4: Functional Interface with Lambda

```java
@FunctionalInterface
interface StringTransformer {
    String transform(String input);
    
    // Can have default and static methods
    default String transformAndLog(String input) {
        System.out.println("Transforming: " + input);
        return transform(input);
    }
    
    static StringTransformer reverse() {
        return s -> new StringBuilder(s).reverse().toString();
    }
    
    static StringTransformer uppercase() {
        return String::toUpperCase;
    }
}

public class FunctionalInterfaceDemo {
    public static void main(String[] args) {
        // Different lambda implementations of same interface
        StringTransformer reverser = s -> new StringBuilder(s).reverse().toString();
        StringTransformer upper = String::toUpperCase;
        StringTransformer exclaim = s -> s + "!!!";
        StringTransformer combined = s -> upper.transform(exclaim.transform(s));
        
        System.out.println(reverser.transform("hello"));
        System.out.println(upper.transform("hello"));
        System.out.println(combined.transform("hello"));
        
        // Using static factory methods
        StringTransformer factoryReverse = StringTransformer.reverse();
        System.out.println(factoryReverse.transform("hello"));
        
        // Using default method
        System.out.println(factoryReverse.transformAndLog("world"));
    }
}
```
