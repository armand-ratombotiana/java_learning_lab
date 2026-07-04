# Mental Models for Distributed Transactions

## The Voting Model (2PC)
- Coordinator is the election official
- Phase 1: "Are you ready to commit?" (polling)
- Phase 2: "The result is YES" (announcement)

## The Restaurant Model
- 2PC: Everyone must agree on dessert before leaving
- SAGA: Each course has a backup plan (cancellation insurance)

## The Airplane Boarding Model
- **2PC**: Everyone must be at the gate before boarding
- **SAGA**: Board in groups, can roll back if needed
- **3PC**: Pre-boarding check, then boarding, then departure

## The Insurance Model (SAGA)
Each step has a corresponding "undo" action:
- Book flight → Cancel flight
- Charge card → Refund card
- Send confirmation → Withdraw confirmation
