# Distributed Transactions: Architecture

## SAGA Architecture (Choreography)

```
Order Service
    │
    ├──► Payment Service (debited event)
    │         │
    │         ├──► Inventory Service (reserved event)
    │         │         │
    │         │         └──► Shipping Service (shipped event)
    │         │
    │         │  On failure:
    │         └──► Order Service (payment_failed event)
    │                    │
    │                    └──► Compensate
    │
    └──► (listens to events)
```

## XA Architecture

```
Application
    │
    ├──► Transaction Manager
    │         │
    │         ├──► XA Resource (Database 1)
    │         ├──► XA Resource (Database 2)
    │         └──► XA Resource (Message Queue)
    │
    └──► Transaction Log
```
