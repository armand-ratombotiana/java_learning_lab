# Why Distributed ID Generation Exists

## Problem
- Auto-increment IDs require single database
- UUIDs are large (128 bits)
- Generated IDs must be unique across services
- IDs sometimes need ordering by time

## Solution
Distributed ID generators provide:
1. **Global uniqueness** without coordination
2. **Performance**: Generate millions of IDs per second
3. **Ordering**: Time-sortable IDs
4. **Compactness**: 64-bit IDs fit in long integers

## Use Cases
- Database primary keys
- Order/tracking numbers
- Event/message IDs
- Trace IDs for distributed logging
