# How Saga Pattern Works

## Choreography-based Saga Flow
```
Order Service          Inventory Service       Payment Service
     |                       |                       |
     |--OrderCreated-------->|                       |
     |                       |--ProcessPayment------>|
     |                       |<--PaymentProcessed----|
     |                       |                       |
     |<--InventoryReserved---|                       |
     |                       |                       |
     |--OrderConfirmed------>|                       |
     |                       |                       |
  (On failure)               |                       |
     |                       |                       |
     |<--ReservationFailed---|                       |
     |                       |                       |
     |--CancelOrder--------->|                       |
```

## Orchestration-based Saga Flow
```
Orchestrator       Order Svc      Inventory Svc    Payment Svc
     |                 |               |               |
     |--CreateOrder--->|               |               |
     |<--OrderCreated--|               |               |
     |                                 |               |
     |--ReserveInv-------------------->|               |
     |<--InvReserved-------------------|               |
     |                                               |
     |--ProcessPayment------------------------------>|
     |<--PaymentProcessed----------------------------|
     |                 |               |               |
     |--ConfirmOrder--->|               |               |
     |<--OrderConfirmed-|               |               |
     |                 |               |               |
  (Compensation)       |               |               |
     |                 |               |               |
     |--CancelOrder--->|               |               |
     |--ReleaseInv-------------------->|               |
```

## State Machine
```
INITIALIZED -> RESERVING_INVENTORY -> PROCESSING_PAYMENT -> CONFIRMED
                          |                    |
                          v                    v
              RESERVATION_FAILED          PAYMENT_FAILED
                          |                    |
                          v                    v
                    CANCELLING_ORDER       RELEASING_INVENTORY
                          |                    |
                          +---------+----------+
                                    |
                                    v
                              CANCELLED
```
