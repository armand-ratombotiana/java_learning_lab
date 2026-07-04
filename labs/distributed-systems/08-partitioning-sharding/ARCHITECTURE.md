# Partitioning: Architecture

## Proxy-Based Sharding

```
Client ──▶ Proxy ──▶ Shard Router ──┬── Shard 1
                                     ├── Shard 2
                                     ├── Shard 3
                                     └── Shard N
```

## Vitess Architecture

```
Application
    │
    └──▶ vtgate (Proxy)
            │
            ├──▶ Shard 0 ──▶ MySQL replica set
            ├──▶ Shard 1 ──▶ MySQL replica set
            └──▶ Shard 2 ──▶ MySQL replica set
            │
    topsd (Configuration)
```

## Application-Level Sharding

```
Order Service
    │
    ├──▶ Shard for US customers
    ├──▶ Shard for EU customers
    └──▶ Shard for APAC customers
```
