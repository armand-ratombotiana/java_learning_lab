# Saga Pattern Theory & Intuition

## 💡 The Distributed Transaction Problem
In a monolithic application with a single database, transactions are easy. If you want to create an order, reserve inventory, and charge a credit card, you wrap all three database calls in a single `BEGIN TRANSACTION` and `COMMIT`. If anything fails, the database handles the `ROLLBACK`, guaranteeing ACID properties.

In a Microservices architecture, the Order Service, Inventory Service, and Payment Service all have their own isolated databases. You cannot use a standard database transaction across them.

### Why not Two-Phase Commit (2PC)?
Historically, distributed systems used 2PC (via XA transactions). A coordinator asks all services to "prepare" (lock their databases). If all agree, it tells them to "commit".
- **The Problem**: 2PC is synchronous and blocking. If the Payment Service is slow, it locks the Inventory database, bringing the entire system to a halt. It is fundamentally incompatible with the scalability goals of microservices.

## 🔄 The Solution: The Saga Pattern
A Saga is a sequence of **Local Transactions**. Each local transaction updates the database within a single microservice and publishes an event or message to trigger the next local transaction in the saga.

If a local transaction fails because it violates a business rule (e.g., the credit card is declined), the saga executes a series of **Compensating Transactions** that undo the changes made by the preceding local transactions.

### The Saga Flow (E-Commerce Example)
1. **Order Service**: Creates an Order in `PENDING` state.
2. **Inventory Service**: Reserves the items. (Success)
3. **Payment Service**: Attempts to charge the card. (**FAILS** - Insufficient Funds)
4. **Inventory Service (Compensation)**: Releases the reserved items back to stock.
5. **Order Service (Compensation)**: Marks the Order as `CANCELLED`.

The system is *eventually consistent*. For a brief moment, the inventory was reserved for a failed order, but the compensating transactions healed the system.