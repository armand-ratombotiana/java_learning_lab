# Event Sourcing Module - PROJECTS.md

---

# Mini-Project: Event Sourcing基础实现

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Event Store, Aggregate Root, Domain Events, Command Query Separation, Event Replay

This mini-project demonstrates event sourcing pattern using a simple bank account example. You'll implement an event store that persists domain events and allows rebuilding aggregate state by replaying events.

---

## Project Structure

```
30-event-sourcing/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── aggregate/
│   │   └── BankAccount.java
│   ├── events/
│   │   └── BankAccountEvents.java
│   ├── commands/
│   │   └── BankAccountCommands.java
│   ├── store/
│   │   └── EventStore.java
│   └── repository/
│       └── EventSourcedRepository.java
```

---

## Step 1: POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>event-sourcing-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
    </dependencies>
</project>
```

---

## Step 2: Domain Events

```java
// events/BankAccountEvents.java
package com.learning.events;

import java.time.Instant;
import java.math.BigDecimal;

public abstract class BankAccountEvent {
    private final String eventId;
    private final String aggregateId;
    private final Instant timestamp;
    
    public BankAccountEvent(String aggregateId) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.timestamp = Instant.now();
    }
    
    public String getEventId() { return eventId; }
    public String getAggregateId() { return aggregateId; }
    public Instant getTimestamp() { return timestamp; }
    
    public abstract String getEventType();
}

public class AccountCreated extends BankAccountEvent {
    private final String accountHolderName;
    private final BigDecimal initialBalance;
    
    public AccountCreated(String aggregateId, String accountHolderName, BigDecimal initialBalance) {
        super(aggregateId);
        this.accountHolderName = accountHolderName;
        this.initialBalance = initialBalance;
    }
    
    public String getAccountHolderName() { return accountHolderName; }
    public BigDecimal getInitialBalance() { return initialBalance; }
    public String getEventType() { return "ACCOUNT_CREATED"; }
}

public class MoneyDeposited extends BankAccountEvent {
    private final BigDecimal amount;
    
    public MoneyDeposited(String aggregateId, BigDecimal amount) {
        super(aggregateId);
        this.amount = amount;
    }
    
    public BigDecimal getAmount() { return amount; }
    public String getEventType() { return "MONEY_DEPOSITED"; }
}

public class MoneyWithdrawn extends BankAccountEvent {
    private final BigDecimal amount;
    
    public MoneyWithdrawn(String aggregateId, BigDecimal amount) {
        super(aggregateId);
        this.amount = amount;
    }
    
    public BigDecimal getAmount() { return amount; }
    public String getEventType() { return "MONEY_WITHDRAWN"; }
}

public class AccountClosed extends BankAccountEvent {
    private final String reason;
    
    public AccountClosed(String aggregateId, String reason) {
        super(aggregateId);
        this.reason = reason;
    }
    
    public String getReason() { return reason; }
    public String getEventType() { return "ACCOUNT_CLOSED"; }
}
```

---

## Step 3: Aggregate Root

```java
// aggregate/BankAccount.java
package com.learning.aggregate;

import com.learning.events.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BankAccount {
    private String id;
    private String accountHolderName;
    private BigDecimal balance;
    private AccountStatus status;
    private Instant createdAt;
    private Instant lastUpdatedAt;
    
    private final List<BankAccountEvent> uncommittedEvents = new CopyOnWriteArrayList<>();
    
    public enum AccountStatus {
        ACTIVE, CLOSED
    }
    
    // Reconstruction from events (for replay)
    public BankAccount(List<BankAccountEvent> events) {
        for (BankAccountEvent event : events) {
            apply(event);
        }
    }
    
    // Factory method for new accounts
    public static BankAccount create(String accountHolderName, BigDecimal initialBalance) {
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        
        String id = java.util.UUID.randomUUID().toString();
        BankAccount account = new BankAccount();
        account.id = id;
        account.accountHolderName = accountHolderName;
        account.balance = initialBalance;
        account.status = AccountStatus.ACTIVE;
        account.createdAt = Instant.now();
        account.lastUpdatedAt = Instant.now();
        
        account.addEvent(new AccountCreated(id, accountHolderName, initialBalance));
        
        return account;
    }
    
    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Cannot deposit to closed account");
        }
        
        balance = balance.add(amount);
        lastUpdatedAt = Instant.now();
        
        addEvent(new MoneyDeposited(id, amount));
    }
    
    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Cannot withdraw from closed account");
        }
        
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        
        balance = balance.subtract(amount);
        lastUpdatedAt = Instant.now();
        
        addEvent(new MoneyWithdrawn(id, amount));
    }
    
    public void close(String reason) {
        if (status == AccountStatus.CLOSED) {
            throw new IllegalStateException("Account already closed");
        }
        
        status = AccountStatus.CLOSED;
        lastUpdatedAt = Instant.now();
        
        addEvent(new AccountClosed(id, reason));
    }
    
    private void apply(BankAccountEvent event) {
        switch (event.getEventType()) {
            case "ACCOUNT_CREATED" -> {
                AccountCreated created = (AccountCreated) event;
                this.id = event.getAggregateId();
                this.accountHolderName = created.getAccountHolderName();
                this.balance = created.getInitialBalance();
                this.status = AccountStatus.ACTIVE;
                this.createdAt = event.getTimestamp();
            }
            case "MONEY_DEPOSITED" -> {
                MoneyDeposited deposit = (MoneyDeposited) event;
                this.balance = this.balance.add(deposit.getAmount());
            }
            case "MONEY_WITHDRAWN" -> {
                MoneyWithdrawn withdrawal = (MoneyWithdrawn) event;
                this.balance = this.balance.subtract(withdrawal.getAmount());
            }
            case "ACCOUNT_CLOSED" -> {
                this.status = AccountStatus.CLOSED;
            }
        }
        this.lastUpdatedAt = event.getTimestamp();
    }
    
    private void addEvent(BankAccountEvent event) {
        uncommittedEvents.add(event);
    }
    
    public List<BankAccountEvent> getUncommittedEvents() {
        return new ArrayList<>(uncommittedEvents);
    }
    
    public void clearUncommittedEvents() {
        uncommittedEvents.clear();
    }
    
    // Getters
    public String getId() { return id; }
    public String getAccountHolderName() { return accountHolderName; }
    public BigDecimal getBalance() { return balance; }
    public AccountStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastUpdatedAt() { return lastUpdatedAt; }
}
```

---

## Step 4: Event Store

```java
// store/EventStore.java
package com.learning.store;

import com.learning.events.BankAccountEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.sql.*;
import java.time.Instant;
import java.util.*;

public class EventStore {
    
    private final Connection connection;
    private final ObjectMapper objectMapper;
    
    public EventStore(Connection connection) {
        this.connection = connection;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        
        initializeTable();
    }
    
    private void initializeTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS events (
                id VARCHAR(36) PRIMARY KEY,
                aggregate_id VARCHAR(36) NOT NULL,
                event_type VARCHAR(50) NOT NULL,
                event_data TEXT NOT NULL,
                timestamp TIMESTAMP NOT NULL,
                version INT NOT NULL
            )
            """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize events table", e);
        }
    }
    
    public void append(String aggregateId, List<BankAccountEvent> events, int expectedVersion) {
        int currentVersion = getLastVersion(aggregateId);
        
        if (currentVersion != expectedVersion) {
            throw new OptimisticLockingException("Expected version conflict for aggregate " + aggregateId);
        }
        
        int version = currentVersion + 1;
        
        for (BankAccountEvent event : events) {
            try {
                String eventId = event.getEventId();
                String eventType = event.getEventType();
                String eventData = objectMapper.writeValueAsString(event);
                Timestamp timestamp = Timestamp.from(event.getTimestamp());
                
                String sql = "INSERT INTO events (id, aggregate_id, event_type, event_data, timestamp, version) VALUES (?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, eventId);
                    stmt.setString(2, aggregateId);
                    stmt.setString(3, eventType);
                    stmt.setString(4, eventData);
                    stmt.setTimestamp(5, timestamp);
                    stmt.setInt(6, version++);
                    stmt.execute();
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to append event", e);
            }
        }
    }
    
    public List<BankAccountEvent> read(String aggregateId) {
        List<BankAccountEvent> events = new ArrayList<>();
        
        String sql = "SELECT event_data FROM events WHERE aggregate_id = ? ORDER BY version";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, aggregateId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String eventData = rs.getString("event_data");
                BankAccountEvent event = objectMapper.readValue(eventData, BankAccountEvent.class);
                events.add(event);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read events", e);
        }
        
        return events;
    }
    
    public int getLastVersion(String aggregateId) {
        String sql = "SELECT MAX(version) FROM events WHERE aggregate_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, aggregateId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get version", e);
        }
        
        return 0;
    }
    
    public List<String> getAllAggregateIds() {
        List<String> ids = new ArrayList<>();
        
        String sql = "SELECT DISTINCT aggregate_id FROM events ORDER BY timestamp";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ids.add(rs.getString("aggregate_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get aggregate IDs", e);
        }
        
        return ids;
    }
}

class OptimisticLockingException extends RuntimeException {
    public OptimisticLockingException(String message) {
        super(message);
    }
}
```

---

## Step 5: Repository

```java
// repository/EventSourcedRepository.java
package com.learning.repository;

import com.learning.aggregate.BankAccount;
import com.learning.store.EventStore;
import com.learning.events.BankAccountEvent;

public class EventSourcedRepository {
    
    private final EventStore eventStore;
    
    public EventSourcedRepository(EventStore eventStore) {
        this.eventStore = eventStore;
    }
    
    public BankAccount save(BankAccount account) {
        List<BankAccountEvent> uncommittedEvents = account.getUncommittedEvents();
        
        if (!uncommittedEvents.isEmpty()) {
            int lastVersion = eventStore.getLastVersion(account.getId());
            eventStore.append(account.getId(), uncommittedEvents, lastVersion);
            account.clearUncommittedEvents();
        }
        
        return account;
    }
    
    public BankAccount findById(String id) {
        List<BankAccountEvent> events = eventStore.read(id);
        
        if (events.isEmpty()) {
            return null;
        }
        
        return new BankAccount(events);
    }
    
    public boolean existsById(String id) {
        return !eventStore.read(id).isEmpty();
    }
}
```

---

## Step 6: Main Application

```java
// Main.java
package com.learning;

import com.learning.aggregate.BankAccount;
import com.learning.repository.EventSourcedRepository;
import com.learning.store.EventStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.math.BigDecimal;
import java.sql.DriverManager;

@SpringBootApplication
public class Main {
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    
    @Bean
    public CommandLineRunner run(EventStore eventStore, EventSourcedRepository repository) {
        return args -> {
            System.out.println("=== Event Sourcing Demo ===");
            
            // Create new account
            BankAccount account = BankAccount.create("John Doe", new BigDecimal("1000.00"));
            System.out.println("Created account: " + account.getId() + " with balance: " + account.getBalance());
            
            // Save to event store
            repository.save(account);
            
            // Deposit money
            account.deposit(new BigDecimal("500.00"));
            System.out.println("After deposit: " + account.getBalance());
            repository.save(account);
            
            // Withdraw money
            account.withdraw(new BigDecimal("200.00"));
            System.out.println("After withdrawal: " + account.getBalance());
            repository.save(account);
            
            // Close account
            account.close("Customer request");
            repository.save(account);
            
            // Recreate account from event store
            System.out.println("\n--- Reconstructing account from events ---");
            BankAccount reconstructed = repository.findById(account.getId());
            System.out.println("Reconstructed account - Balance: " + reconstructed.getBalance());
            System.out.println("Reconstructed account - Status: " + reconstructed.getStatus());
        };
    }
    
    @Bean
    public EventStore eventStore() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb");
        return new EventStore(connection);
    }
    
    @Bean
    public EventSourcedRepository eventSourcedRepository(EventStore eventStore) {
        return new EventSourcedRepository(eventStore);
    }
}
```

---

## Build Instructions

```bash
cd 30-event-sourcing
mvn clean compile
mvn spring-boot:run
```

---

# Real-World Project: CQRS with Event Sourcing

## Project Overview

**Duration**: 8+ hours  
**Difficulty**: Advanced  
**Concepts Used**: CQRS Pattern, Projections, Snapshotting, Eventual Consistency, Sagas

This comprehensive project implements a complete CQRS (Command Query Responsibility Segregation) architecture with event sourcing, including read models, projections, and saga orchestration.

---

## Complete Implementation

```java
// CQRS - Command Side
package com.learning.cqrs.command

class BankAccount extends AggregateRoot {
    private BigDecimal balance;
    private AccountStatus status;
    
    public static CommandHandler<CreateAccountCommand, String> handle(CreateAccountCommand cmd) {
        return aggregate -> {
            if (cmd.initialBalance().compareTo(BigDecimal.ZERO) < 0) {
                return Result.failure(new InvalidArgumentError("Initial balance cannot be negative"));
            }
            
            aggregate.apply(new AccountCreatedEvent(cmd.aggregateId(), cmd.holderName(), cmd.initialBalance()));
            return Result.success(aggregate.id());
        };
    }
    
    public static CommandHandler<DepositCommand, Result> handle(DepositCommand cmd) {
        return aggregate -> {
            if (aggregate.status() != AccountStatus.ACTIVE) {
                return Result.failure(new InvalidStateError("Account is not active"));
            }
            
            if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
                return Result.failure(new InvalidArgumentError("Amount must be positive"));
            }
            
            aggregate.apply(new MoneyDepositedEvent(aggregate.id(), cmd.amount()));
            return Result.success(aggregate.id());
        };
    }
    
    // Private Apply Methods
    private void apply(AccountCreatedEvent event) {
        this.id = event.aggregateId();
        this.balance = event.initialBalance();
        this.status = AccountStatus.ACTIVE;
    }
    
    private void apply(MoneyDepositedEvent event) {
        this.balance = this.balance.add(event.amount());
    }
}

record CreateAccountCommand(String aggregateId, String holderName, BigDecimal initialBalance) implements Command {}
record DepositCommand(String aggregateId, BigDecimal amount) implements Command {}
```

---

## Aggregate Root Base

```java
package com.learning.aggregate

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

abstract class AggregateRoot {
    protected String id
    protected int version = 0
    
    private final List<DomainEvent> uncommittedEvents = new CopyOnWriteArrayList<>()
    
    protected <T extends DomainEvent> void apply(T event) {
        applyEvent(event)
        uncommittedEvents.add(event)
    }
    
    protected abstract void applyEvent(DomainEvent event)
    
    public List<DomainEvent> getUncommittedEvents() {
        return new ArrayList<>(uncommittedEvents)
    }
    
    public void clearUncommittedEvents() {
        uncommittedEvents.clear()
    }
    
    public int getVersion() { return version }
    public String getId() { return id }
}

interface DomainEvent {
    String getAggregateId()
    Instant getTimestamp()
    String getEventType()
}
```

---

## CQRS - Query Side (Read Model)

```java
package com.learning.cqrs.query

import jakarta.persistence.*

@Entity
@Table(name = "account_view")
class AccountView {
    @Id
    private String id
    
    @Column(name = "holder_name")
    private String holderName
    
    private BigDecimal balance
    
    @Enumerated(STRING)
    private AccountStatus status
    
    private Instant lastUpdated
    
    // Constructors, Getters, Setters
}

@Repository
class AccountProjection {
    
    @Autowired
    private EntityManager entityManager
    
    public void project(AccountCreatedEvent event) {
        AccountView view = new AccountView()
        view.setId(event.aggregateId())
        view.setHolderName(event.holderName())
        view.setBalance(event.initialBalance())
        view.setStatus(AccountStatus.ACTIVE)
        view.setLastUpdated(event.timestamp())
        
        entityManager.persist(view)
    }
    
    public void project(MoneyDeposited event) {
        AccountView view = entityManager.find(AccountView.class, event.aggregateId())
        if (view != null) {
            view.setBalance(view.getBalance().add(event.amount()))
            view.setLastUpdated(event.timestamp())
        }
    }
    
    public void project(MoneyWithdrawnEvent event) {
        AccountView view = entityManager.find(AccountView.class, event.aggregateId())
        if (view != null) {
            view.setBalance(view.getBalance().subtract(event.amount()))
            view.setLastUpdated(event.timestamp())
        }
    }
    
    public void project(AccountClosedEvent event) {
        AccountView view = entityManager.find(AccountView.class, event.aggregateId())
        if (view != null) {
            view.setStatus(AccountStatus.CLOSED)
            view.setLastUpdated(event.timestamp())
        }
    }
}
```

---

## Event Handlers

```java
package com.learning.events

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class AccountEventHandler {
    
    @Autowired
    private AccountProjection accountProjection
    
    @KafkaListener(topics = "account-events")
    public void handle(String message) {
        try {
            DomainEvent event = objectMapper.readValue(message, DomainEvent.class)
            
            switch (event) {
                case AccountCreatedEvent e -> accountProjection.project(e)
                case MoneyDepositedEvent e -> accountProjection.project(e)
                case MoneyWithdrawnEvent e -> accountProjection.project(e)
                case AccountClosedEvent e -> accountProjection.project(e)
                default -> logger.warn("Unknown event type: {}", event.getClass())
            }
        } catch (Exception e) {
            logger.error("Error processing event", e)
        }
    }
}
```

---

## Snapshotting

```java
// Snapshot Strategy
package com.learning.snapshot

class SnapshotService {
    
    private final EventStore eventStore
    private final SnapshotRepository snapshotRepository
    private final ObjectMapper objectMapper
    
    void createSnapshot(String aggregateId, AggregateRoot aggregate, int snapshotThreshold = 10) {
        int currentVersion = eventStore.getLastVersion(aggregateId)
        
        if (currentVersion % snapshotThreshold == 0) {
            Snapshot snapshot = new Snapshot(
                aggregateId,
                currentVersion,
                objectMapper.writeValueAsString(aggregate),
                Instant.now()
            )
            
            snapshotRepository.save(snapshot)
        }
    }
    
    AggregateRoot restoreSnapshot(String aggregateId) {
        Snapshot snapshot = snapshotRepository.findLatest(aggregateId)
        
        if (snapshot == null) {
            return null
        }
        
        return objectMapper.readValue(snapshot.getData(), aggregateType())
    }
}
```

---

## Build Instructions (Real-World)

```bash
cd 30-event-sourcing

# Setup Kafka for event distribution
docker run -p 2181:2181 -p 9092:9092 -e KAFKA_ADVERTISED_HOST_NAME=localhost confluentinc/cp-kafka

# Run application
mvn spring-boot:run

# Check read model
curl http://localhost:8080/api/accounts
```

---

## Key Concepts Summary

| Concept | Description |
|---------|-------------|
| Event Store | Persistence layer for domain events |
| Aggregate Root | Entity that applies events to rebuild state |
| Domain Events | Immutable records of state changes |
| Snapshotting | Performance optimization for long event streams |
| CQRS | Separating read and write models |
| Projections | Materialized views from events |
| Saga | Long-running business transaction |

---

## Summary

This module demonstrates:

1. **Event Sourcing**: Persisting state as a sequence of events
2. **Aggregate Reconstruction**: Rebuilding aggregate state from events
3. **Event Store**: Database for storing events
4. **CQRS**: Separate command and query models
5. **Projections**: Materialized views from event stream
6. **Snapshots**: Optimizing replay performance

These patterns enable complete audit trails, temporal queries, and flexible system evolution.