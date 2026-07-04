# Why Distributed Transactions Exist

## Business Need
Modern applications span multiple services and databases. A single business operation (e.g., "place order") often requires updates to inventory, orders, payments, and shipping systems.

## Without Distributed Transactions
- Partial failures leave system in inconsistent state
- Manual reconciliation required
- Data corruption in multi-service operations

## Use Cases
- **E-commerce**: Order → Payment → Inventory → Shipping
- **Banking**: Transfer between accounts in different systems
- **Booking**: Flight + Hotel + Car reservation
