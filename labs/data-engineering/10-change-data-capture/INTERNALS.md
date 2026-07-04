# CDC Internals

## Binlog Reading
Debezium positions as MySQL replica, requests binlog stream from position, parses events (WriteRows, UpdateRows, DeleteRows), converts to Kafka events.

## Offset Management
Debezium stores offsets in Kafka topic for resume capability.

## Snapshot Process
1. Determine snapshot mode
2. Read binlog position
3. Export table data
4. Emit snapshot events (op: 'r')
5. Start streaming from recorded position
