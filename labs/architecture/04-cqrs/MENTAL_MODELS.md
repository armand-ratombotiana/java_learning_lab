# Mental Models for CQRS

## The Library Analogy
Write side is like the librarian who catalogs books (commands). Read side is like the card catalog and search system (queries). They use different systems optimized for their purpose.

## The Git Repository Model
Git commands (commit, push) are like CQRS commands - they change state. Git queries (log, diff, status) read state without changes. Git's internal storage (objects) differs from what users see.

## The Brain Hemisphere Model
Write side is like the left brain (analytical, sequential). Read side is like the right brain (holistic, pattern matching). Both work with the same information but process it differently.

## The Event Photography Model
Commands are like taking a photo - they capture a moment (immutable event). Queries are like looking at an album - you view processed, organized presentations of those moments.

```java
// Write model - like camera capture
@Entity
public class OrderEvent {
    @Id private String eventId;
    private String orderId;
    private String eventType;
    private LocalDateTime timestamp;
    // Immutable once written
}

// Read model - like curated album
@Document
public class OrderDashboardView {
    @Id private String orderId;
    private String customerName;
    private String status;
    private int itemCount;
    private BigDecimal total;
    // Computed for display convenience
}
```
