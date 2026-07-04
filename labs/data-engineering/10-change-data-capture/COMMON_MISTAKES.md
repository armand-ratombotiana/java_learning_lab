# Common Mistakes

1. **No Schema History**: In-memory loses schema on restart
2. **Short Binlog Retention**: binlog expires before CDC processes
3. **No Schema Evolution Handling**: DDL breaks pipeline
4. **Single Task**: Can't keep up with large tables
