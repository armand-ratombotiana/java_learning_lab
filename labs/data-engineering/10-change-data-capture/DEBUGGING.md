# Debugging

## Connector Status
```bash
curl http://localhost:8083/connectors/inventory-connector/status
```

## Lag
```bash
kafka-consumer-groups --describe --group connect-connector
```

## Restart Connector
```bash
curl -X POST http://localhost:8083/connectors/inventory-connector/restart
```
