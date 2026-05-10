# CQRS Projects - Module 66

This module covers Command Query Responsibility Segregation and Event Sourcing patterns.

## Mini-Project: Simple CQRS Implementation (2-4 hours)

### Overview
Build a basic CQRS implementation with separate command and query models for an order system.

### Project Structure
```
cqrs-demo/
├── src/main/java/com/learning/cqrs/
│   ├── commands/
│   │   ├── CreateOrderCommand.java
│   │   └── handlers/
│   ├── queries/
│   │   ├── GetOrderQuery.java
│   │   └── handlers/
│   ├── models/
│   │   ├── OrderCommand.java
│   │   └── OrderView.java
│   └── infrastructure/
│       └── InMemoryStore.java
├── pom.xml
└── run.sh
```

### Implementation
```java
// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>cqrs-demo</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
</project>

// Command Handler
package com.learning.cqrs.handlers;

import com.learning.cqrs.models.OrderCommand;

public interface CommandHandler<C, R> {
    R handle(C command);
}

// Query Handler
package com.learning.cqrs.handlers;

import com.learning.cqrs.models.OrderView;

public interface QueryHandler<Q, R> {
    R handle(Q query);
}

// CreateOrderCommand.java
package com.learning.cqrs.commands;

import java.util.List;

public class CreateOrderCommand {
    private final String customerId;
    private final List<OrderItemDto> items;
    
    public CreateOrderCommand(String customerId, List<OrderItemDto> items) {
        this.customerId = customerId;
        this.items = items;
    }
    
    public String getCustomerId() { return customerId; }
    public List<OrderItemDto> getItems() { return items; }
}

class OrderItemDto {
    private final String productId;
    private final int quantity;
    private final double unitPrice;
    
    public OrderItemDto(String productId, int quantity, double unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
}

// OrderCommandHandler.java
package com.learning.cqrs.handlers;

import com.learning.cqrs.commands.CreateOrderCommand;
import com.learning.cqrs.models.OrderCommand;
import com.learning.cqrs.models.OrderItemCommand;
import java.util.UUID;

public class OrderCommandHandler implements CommandHandler<CreateOrderCommand, String> {
    
    private final CommandStore commandStore;
    
    public OrderCommandHandler(CommandStore commandStore) {
        this.commandStore = commandStore;
    }
    
    @Override
    public String handle(CreateOrderCommand command) {
        String orderId = UUID.randomUUID().toString();
        
        OrderCommand orderCommand = new OrderCommand(orderId, command.getCustomerId());
        
        for (var item : command.getItems()) {
            orderCommand.addItem(new OrderItemCommand(
                item.getProductId(), 
                item.getQuantity(), 
                item.getUnitPrice()
            ));
        }
        
        commandStore.save(orderCommand);
        
        return orderId;
    }
}

// Query Handlers
package com.learning.cqrs.queries;

public class GetOrderQuery {
    private final String orderId;
    
    public GetOrderQuery(String orderId) {
        this.orderId = orderId;
    }
    
    public String getOrderId() { return orderId; }
}

class GetAllOrdersQuery {
    private final String customerId;
    
    public GetAllOrdersQuery(String customerId) {
        this.customerId = customerId;
    }
}

// OrderQueryHandler.java
package com.learning.cqrs.handlers;

import com.learning.cqrs.queries.GetOrderQuery;
import com.learning.cqrs.queries.GetAllOrdersQuery;
import com.learning.cqrs.models.OrderView;
import java.util.List;

public class OrderQueryHandler implements 
    QueryHandler<GetOrderQuery, OrderView>,
    QueryHandler<GetAllOrdersQuery, List<OrderView>> {
    
    private final QueryStore queryStore;
    
    public OrderQueryHandler(QueryStore queryStore) {
        this.queryStore = queryStore;
    }
    
    @Override
    public OrderView handle(GetOrderQuery query) {
        return queryStore.findById(query.getOrderId());
    }
    
    @Override
    public List<OrderView> handle(GetAllOrdersQuery query) {
        return queryStore.findByCustomerId(query.getCustomerId());
    }
}

// Models
package com.learning.cqrs.models;

import java.util.ArrayList;
import java.util.List;

public class OrderCommand {
    private final String orderId;
    private final String customerId;
    private final List<OrderItemCommand> items = new ArrayList<>();
    
    public OrderCommand(String orderId, String customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
    }
    
    public void addItem(OrderItemCommand item) {
        items.add(item);
    }
    
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public List<OrderItemCommand> getItems() { return items; }
}

class OrderItemCommand {
    private final String productId;
    private final int quantity;
    private final double unitPrice;
    
    public OrderItemCommand(String productId, int quantity, double unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
}

package com.learning.cqrs.models;

import java.util.List;

public class OrderView {
    private String orderId;
    private String customerId;
    private String status;
    private List<OrderItemView> items;
    private double total;
    private String createdAt;
    
    public OrderView() {}
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<OrderItemView> getItems() { return items; }
    public void setItems(List<OrderItemView> items) { this.items = items; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

class OrderItemView {
    private String productId;
    private int quantity;
    private double unitPrice;
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
}
```

---

## Real-World Project: CQRS with Event Sourcing (8+ hours)

### Overview
Build a comprehensive CQRS system with event sourcing for a banking application.

### Project Structure
```
banking-cqrs/
├── domain/
│   ├── events/
│   ├── aggregates/
│   └── repositories/
├── commands/
│   ├── handlers/
│   └── validators/
├── queries/
│   ├── handlers/
│   └── projections/
├── infrastructure/
│   ├── eventstore/
│   └── readstore/
└── pom.xml
```

### Implementation
```java
// Account.java (Aggregate with Event Sourcing)
package com.learning.banking.aggregate;

import com.learning.banking.events.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountAggregate {
    private String accountId;
    private String customerId;
    private double balance;
    private String status;
    private int version;
    private final List<DomainEvent> uncommittedEvents = new ArrayList<>();
    
    private AccountAggregate() {}
    
    public static AccountAggregate create(String customerId) {
        AccountAggregate account = new AccountAggregate();
        account.accountId = UUID.randomUUID().toString();
        account.customerId = customerId;
        account.balance = 0;
        account.status = "ACTIVE";
        
        account.addEvent(new AccountCreatedEvent(
            account.accountId, customerId, System.currentTimeMillis()
        ));
        
        return account;
    }
    
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        balance += amount;
        
        addEvent(new MoneyDepositedEvent(
            accountId, amount, balance, System.currentTimeMillis()
        ));
    }
    
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        if (balance < amount) {
            throw new IllegalStateException("Insufficient balance");
        }
        
        balance -= amount;
        
        addEvent(new MoneyWithdrawnEvent(
            accountId, amount, balance, System.currentTimeMillis()
        ));
    }
    
    public void transfer(String toAccountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        if (balance < amount) {
            throw new IllegalStateException("Insufficient balance");
        }
        
        withdraw(amount);
        
        addEvent(new TransferInitiatedEvent(
            accountId, toAccountId, amount, System.currentTimeMillis()
        ));
    }
    
    private void addEvent(DomainEvent event) {
        uncommittedEvents.add(event);
        version++;
    }
    
    public List<DomainEvent> getUncommittedEvents() {
        return new ArrayList<>(uncommittedEvents);
    }
    
    public void clearUncommittedEvents() {
        uncommittedEvents.clear();
    }
    
    public void applyEvent(DomainEvent event) {
        if (event instanceof AccountCreatedEvent e) {
            this.accountId = e.getAccountId();
            this.customerId = e.getCustomerId();
            this.balance = 0;
            this.status = "ACTIVE";
        } else if (event instanceof MoneyDepositedEvent e) {
            this.balance = e.getNewBalance();
        } else if (event instanceof MoneyWithdrawnEvent e) {
            this.balance = e.getNewBalance();
        }
        
        version++;
    }
}

// Domain Events
package com.learning.banking.events;

public interface DomainEvent {}

class AccountCreatedEvent implements DomainEvent {
    private final String accountId;
    private final String customerId;
    private final long timestamp;
    
    public AccountCreatedEvent(String accountId, String customerId, long timestamp) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.timestamp = timestamp;
    }
    
    public String getAccountId() { return accountId; }
    public String getCustomerId() { return customerId; }
    public long getTimestamp() { return timestamp; }
}

class MoneyDepositedEvent implements DomainEvent {
    private final String accountId;
    private final double amount;
    private final double newBalance;
    private final long timestamp;
    
    public MoneyDepositedEvent(String accountId, double amount, 
                               double newBalance, long timestamp) {
        this.accountId = accountId;
        this.amount = amount;
        this.newBalance = newBalance;
        this.timestamp = timestamp;
    }
    
    public String getAccountId() { return accountId; }
    public double getAmount() { return amount; }
    public double getNewBalance() { return newBalance; }
    public long getTimestamp() { return timestamp; }
}

class MoneyWithdrawnEvent implements DomainEvent {
    private final String accountId;
    private final double amount;
    private final double newBalance;
    private final long timestamp;
    
    public MoneyWithdrawnEvent(String accountId, double amount, 
                               double newBalance, long timestamp) {
        this.accountId = accountId;
        this.amount = amount;
        this.newBalance = newBalance;
        this.timestamp = timestamp;
    }
    
    public String getAccountId() { return accountId; }
    public double getAmount() { return amount; }
    public double getNewBalance() { return newBalance; }
    public long getTimestamp() { return timestamp; }
}

class TransferInitiatedEvent implements DomainEvent {
    private final String fromAccountId;
    private final String toAccountId;
    private final double amount;
    private final long timestamp;
    
    public TransferInitiatedEvent(String fromAccountId, String toAccountId,
                                  double amount, long timestamp) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.timestamp = timestamp;
    }
    
    public String getFromAccountId() { return fromAccountId; }
    public String getToAccountId() { return toAccountId; }
    public double getAmount() { return amount; }
    public long getTimestamp() { return timestamp; }
}

// Event Sourced Repository
package com.learning.banking.repository;

import com.learning.banking.aggregate.AccountAggregate;
import com.learning.banking.events.DomainEvent;
import java.util.*;

public class EventSourcedAccountRepository {
    
    private final Map<String, List<DomainEvent>> eventStore = new HashMap<>();
    
    public void save(AccountAggregate aggregate) {
        List<DomainEvent> events = aggregate.getUncommittedEvents();
        
        if (!events.isEmpty()) {
            eventStore.computeIfAbsent(
                aggregate.getAccountId(), 
                k -> new ArrayList<>()
            ).addAll(events);
            
            aggregate.clearUncommittedEvents();
        }
    }
    
    public Optional<AccountAggregate> findById(String accountId) {
        List<DomainEvent> events = eventStore.get(accountId);
        
        if (events == null || events.isEmpty()) {
            return Optional.empty();
        }
        
        AccountAggregate aggregate = new AccountAggregate();
        
        for (DomainEvent event : events) {
            aggregate.applyEvent(event);
        }
        
        return Optional.of(aggregate);
    }
}

// Command Handlers
package com.learning.banking.commands;

import com.learning.banking.aggregate.AccountAggregate;
import com.learning.banking.repository.EventSourcedAccountRepository;

public class CommandHandlers {
    
    private final EventSourcedAccountRepository repository;
    
    public CommandHandlers(EventSourcedAccountRepository repository) {
        this.repository = repository;
    }
    
    public String handle(CreateAccountCommand command) {
        AccountAggregate account = AccountAggregate.create(command.getCustomerId());
        repository.save(account);
        return account.getAccountId();
    }
    
    public void handle(DepositCommand command) {
        AccountAggregate account = repository.findById(command.getAccountId())
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        account.deposit(command.getAmount());
        repository.save(account);
    }
    
    public void handle(WithdrawCommand command) {
        AccountAggregate account = repository.findById(command.getAccountId())
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        account.withdraw(command.getAmount());
        repository.save(account);
    }
}

class CreateAccountCommand {
    private final String customerId;
    
    public CreateAccountCommand(String customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerId() { return customerId; }
}

class DepositCommand {
    private final String accountId;
    private final double amount;
    
    public DepositCommand(String accountId, double amount) {
        this.accountId = accountId;
        this.amount = amount;
    }
    
    public String getAccountId() { return accountId; }
    public double getAmount() { return amount; }
}

class WithdrawCommand {
    private final String accountId;
    private final double amount;
    
    public WithdrawCommand(String accountId, double amount) {
        this.accountId = accountId;
        this.amount = amount;
    }
    
    public String getAccountId() { return accountId; }
    public double getAmount() { return amount; }
}

// Query Projections
package com.learning.banking.projections;

import java.util.*;

public class AccountViewProjection {
    private final Map<String, AccountView> accounts = new HashMap<>();
    
    public void on(AccountCreatedEvent event) {
        AccountView view = new AccountView();
        view.setAccountId(event.getAccountId());
        view.setCustomerId(event.getCustomerId());
        view.setBalance(0);
        view.setStatus("ACTIVE");
        accounts.put(event.getAccountId(), view);
    }
    
    public void on(MoneyDepositedEvent event) {
        AccountView view = accounts.get(event.getAccountId());
        if (view != null) {
            view.setBalance(event.getNewBalance());
        }
    }
    
    public void on(MoneyWithdrawnEvent event) {
        AccountView view = accounts.get(event.getAccountId());
        if (view != null) {
            view.setBalance(event.getNewBalance());
        }
    }
    
    public Optional<AccountView> findById(String accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }
    
    public List<AccountView> findByCustomerId(String customerId) {
        return accounts.values().stream()
            .filter(v -> v.getCustomerId().equals(customerId))
            .toList();
    }
}

class AccountView {
    private String accountId;
    private String customerId;
    private double balance;
    private String status;
    
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

### Build and Run
```bash
mvn clean compile
java -cp target/classes com.learning.cqrs.CqrsDemoApplication
```

### Learning Outcomes
- Implement CQRS pattern
- Build event sourcing
- Create command and query handlers
- Design read projections
- Handle domain events
- Build scalable architectures