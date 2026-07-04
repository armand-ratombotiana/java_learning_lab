# Distributed ID Generation: Architecture

## Snowflake Service Architecture

```
Service A ──┐
Service B ──┼──▶ Snowflake Generator (library)
Service C ──┘    (embedded, no network)

vs.

Service A ──▶ ID Service ──▶ Snowflake
Service B ──▶ (standalone)    Generator
Service C ──▶
```

## ZooKeeper Worker Allocation

```
Service starts → requests worker ID from ZooKeeper
ZooKeeper → assigns unique worker ID from pool
Service → generates Snowflake IDs with assigned worker ID
Service stops → releases worker ID back to pool
```

## Multi-Region ID Architecture

```
Region US:   1XXX XXXX XXXX XXXX (region bits)
Region EU:   2XXX XXXX XXXX XXXX
Region APAC: 3XXX XXXX XXXX XXXX
```
