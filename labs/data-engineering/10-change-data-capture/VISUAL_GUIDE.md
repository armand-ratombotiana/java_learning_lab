# Visual Guide

## CDC Flow
```
[MySQL: binlog] -> [Debezium Connector] -> [Kafka: cdc.db.orders]
                                           -> [Flink Stream]
                                           -> [S3 Sink]
                                           -> [Elasticsearch]
```

## Event Examples
```
INSERT: op:c, after:{id:1001,name:"Jane"}
UPDATE: op:u, before:{name:"Jane"}, after:{name:"Jane Smith"}
DELETE: op:d, before:{id:1001,name:"Jane"}, after:null
```
