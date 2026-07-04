# Saga Pattern Architecture Reference

## System Architecture
```
                    +------------------+
                    |  Saga Manager    |
                    |  (Orchestrator)  |
                    +--------+---------+
                             |
           +-----------------+-----------------+
           |                 |                 |
    +------v------+  +------v------+  +------v--------+
    | Order       |  | Inventory   |  | Payment       |
    | Service     |  | Service     |  | Service       |
    | - create    |  | - reserve   |  | - charge      |
    | - confirm   |  | - release   |  | - refund      |
    | - cancel    |  |             |  |               |
    +------+------+  +------+------+  +-------+-------+
           |                 |                 |
    +------v------+  +------v------+  +-------v-------+
    | Order DB    |  | Inventory   |  | Payment DB    |
    | (Postgres)  |  | DB (Redis)  |  | (Postgres)    |
    +-------------+  +-------------+  +---------------+
```

## Saga Store
| Field | Description | Type |
|-------|-------------|------|
| saga_id | Unique saga identifier | UUID |
| saga_type | Type of saga (OrderSaga, PaymentSaga) | String |
| state | Current saga state (JSON) | JSON |
| last_updated | Last activity timestamp | Timestamp |
| timeout | Saga expiry time | Timestamp |
| compensating | Whether compensation is in progress | Boolean |

## Event Flow
| Step | Command | Event on Success | Event on Failure |
|------|---------|-----------------|------------------|
| 1 | CreateOrder | OrderCreated | - |
| 2 | ReserveInventory | InventoryReserved | InventoryReservationFailed |
| 3 | ProcessPayment | PaymentProcessed | PaymentFailed |
| 4 | ConfirmOrder | OrderConfirmed | - |

## Compensation Map
| Failed Step | Compensation |
|-------------|-------------|
| Step 2 (Inventory) | Cancel order (no inventory reserved) |
| Step 3 (Payment) | Release inventory + cancel order |
| Step 4 (Confirm) | Full compensation (refund + release + cancel) |
