# Mini Project: Social Network Connectivity

Build a command-line tool that analyzes social network connectivity using DSU.

## Requirements

1. Load friendship data from a CSV file (user1, user2, timestamp)
2. Track connected friend groups using DSU
3. Answer queries: "Are user X and user Y connected?"
4. Find the earliest timestamp when all users became connected
5. Output the number of friend groups at each milestone

## Sample Data Format

```
user1,user2,timestamp
Alice,Bob,2024-01-01T10:00:00
Bob,Charlie,2024-01-01T11:00:00
David,Eve,2024-01-02T09:00:00
```

## Implementation Steps

1. Create a `FriendshipGraph` class wrapping DSU
2. Implement CSV parsing
3. Process friendships in chronological order
4. Track component count over time
5. Implement query interface
