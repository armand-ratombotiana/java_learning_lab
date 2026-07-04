# Distributed ID Generation: Theory

## Requirements for Distributed IDs

### Uniqueness
IDs must be globally unique across all nodes.

### Monotonicity (Optional)
IDs should increase over time for ordered operations.

### K-Ordered (Optional)
IDs sortable by time for range queries.

### Compactness
Short IDs for storage and URL efficiency.

## Common Approaches

### UUID (128-bit)
- Version 1: Time-based + MAC address
- Version 4: Random
- Version 7: Time-ordered random

### Snowflake (64-bit)
```
| 1 bit sign | 41 bits timestamp | 10 bits worker | 12 bits sequence |
```

### Database Sequences
- Auto-increment columns in database
- Batch allocation for performance

### Hybrid Approaches
- Combine timestamp + node ID + sequence
- Custom encoding for specific requirements
