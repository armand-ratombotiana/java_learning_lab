# DDD Security

## Domain Security Patterns

### Authorization in Domain Layer
```java
@Entity
public class Order {
    public void cancel(CustomerId requestingCustomer) {
        if (!this.customerId.equals(requestingCustomer)) {
            throw new UnauthorizedException("Only the order owner can cancel");
        }
        this.status = OrderStatus.CANCELLED;
    }
}
```

### Role-Based Domain Behavior
```java
@Entity
public class Order {
    public void approve(EmployeeId approver, ApproverRole role) {
        if (!role.canApproveOrders()) {
            throw new InsufficientPermissionsException(role);
        }
        if (this.status != OrderStatus.SUBMITTED) {
            throw new InvalidStateException();
        }
        this.status = OrderStatus.APPROVED;
        registerEvent(new OrderApproved(id, approver));
    }
}
```

### Validation as Domain Concern
```java
@Value
public class Email {
    String value;

    public Email {
        if (!value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidEmailException(value);
        }
    }
}
```

## Infrastructure Security
- Encrypt sensitive value objects at rest
- Audit domain events for compliance
- Validate input at domain boundaries
- Use anticorruption layer for external input sanitization
