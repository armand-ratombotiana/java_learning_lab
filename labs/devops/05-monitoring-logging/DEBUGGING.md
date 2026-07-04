# Monitoring & Logging Debugging

## Prometheus Debugging
```powershell
# Check target status
http://localhost:9090/targets

# Check configuration
http://localhost:9090/config

# PromQL query debugging
http://localhost:9090/graph

# Check TSDB status
http://localhost:9090/tsdb-status

# Check logs
docker logs prometheus
```

## Elasticsearch Debugging
```powershell
# Cluster health
curl http://localhost:9200/_cluster/health?pretty

# Index status
curl http://localhost:9200/_cat/indices?v

# Search logs
curl -X GET "http://localhost:9200/_search?q=status:500&pretty"

# Check logs
docker logs elasticsearch
```

## Grafana Debugging
- Check data source connectivity (Configuration → Data Sources → Test)
- Inspect dashboard JSON (Dashboard settings → JSON Model)
- Enable Grafana server logging
- Check browser console for frontend errors

## Common Issues
- **No data in Grafana**: Check Prometheus target up status, correct query syntax.
- **Alert not firing**: Check evaluation_interval, rule syntax, for: duration.
- **Kibana no data**: Check Elasticsearch connectivity, index pattern, date field.
- **Filebeat not shipping**: Check log path, permissions, output configuration.
