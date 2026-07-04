# Step-by-Step

1. **Enable binlog**: MySQL server-id, log_bin, binlog_format=ROW
2. **Create user**: GRANT SELECT, REPLICATION SLAVE
3. **Configure**: Debezium connector properties
4. **Start**: Kafka Connect REST API POST /connectors
5. **Consume**: Read from Kafka topic cdc.db.table
6. **Process**: Stream to data lake, warehouse, cache
