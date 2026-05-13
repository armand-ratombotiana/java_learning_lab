# Debugging Consul Issues

## Common Failure Scenarios

### Connection Problems

Applications cannot connect to Consul, failing to retrieve configuration or register services. The Consul agent may not be running, network connectivity may be blocked, or the wrong address/port is configured.

The "connection refused" error means the Consul agent isn't accepting connections. Verify the agent is running with `consul members` and the HTTP API port (default 8500) is accessible. The "no route to host" error indicates network routing issues—check firewall rules and that Consul binds to the correct interface.

Stack trace examples:

**Connection refused:**
```
com.ecwid.consul.transport.DefaultHttpTransport: Request to http://localhost:8500/v1/agent/service/register failed
    at com.ecwid.consul.transport.DefaultHttpTransport.getResponse(DefaultHttpTransport.java:68)
Caused by: java.net.ConnectException: Connection refused
    at java.net.PlainSocketImpl.socketConnect(Native Method)
```

**Timeout:**
```
com.ecwid.consul.transport.TransportException: Request to http://localhost:8500/v1/kv/myapp/config timed out
    at com.ecwid.consul.transport.DefaultHttpTransport.getResponse(DefaultHttpTransport.java:68)
```

### Service Discovery Failures

Applications can't find services through Consul—requests fail with service not found errors. The service may not be registered, health checks are failing, or DNS queries aren't resolving properly.

The "dial tcp: lookup service-name.service.consul on 127.0.0.11:53: no such host" error indicates the Consul DNS isn't being used. Applications must query Consul's DNS on port 8600 to resolve .service.consul names.

**Service not registered:**
```
com.ecwid.consul.agent.AgentResponse: 404: Unknown service: my-service
    at com.ecwid.consul.agent.AgentClient.makeGetRequest(AgentClient.java:156)
```

**Health check failing:**
```
com.ecwid.consul.transport.HttpResponse: 500: consul身体健康检查 failed with code 503
```

### Configuration Retrieval Issues

Applications fail to read configuration from Consul's key-value store. The key path may be wrong, the value format may be incorrect, or ACL tokens don't have permission.

The "permission denied" error occurs when ACLs are enabled but the token lacks read access to the key. The "key not found" error means the exact key path doesn't exist—check for trailing slashes and case sensitivity.

**ACL permission denied:**
```
com.ecwid.consul.transport.HttpResponse: 403: ACL permission denied
    at com.ecwid.consul.transport.DefaultHttpTransport.getResponse(DefaultHttpTransport.java:68)
```

**Key not found:**
```
com.ecwid.consul.transport.HttpResponse: 404: Not Found
    at com.ecwid.consul.transport.DefaultHttpTransport.getResponse(DefaultHttpTransport.java:68)
```

## Debugging Techniques

### Verifying Agent Status

Check the Consul agent is running and healthy:

```bash
# Check agent status
consul info

# List cluster members
consul members

# Check Raft peer status
consul operator raft list-peers

# Verify HTTP API accessibility
curl http://localhost:8500/v1/status/leader
```

The agent should show as "alive" in members output. If it's "failed", restart the agent or check logs. Verify the leader is elected—cluster can't function without a leader.

### Service Registration Debugging

Verify services are registered and healthy:

```bash
# List registered services
curl http://localhost:8500/v1/catalog/services

# Get service details
curl http://localhost:8500/v1/catalog/service/my-service

# Get service health
curl http://localhost:8500/v1/health/service/my-service

# Register service manually (for testing)
curl -X PUT -d @service.json http://localhost:8500/v1/agent/service/register

# Deregister service
curl -X PUT http://localhost:8500/v1/agent/service/deregister/my-service
```

Check service health in Consul UI or via API. Services must pass all health checks to be returned by discovery queries. Health checks can be script-based, HTTP-based, or TCP-based.

### Key-Value Debugging

Verify configuration keys exist and have correct values:

```bash
# List keys in a folder
curl http://localhost:8500/v1/kv/?keys=true&separator=/

# Get specific key
curl http://localhost:8500/v1/kv/myapp/config

# Get raw value (no base64)
curl http://localhost:8500/v1/kv/myapp/config?raw

# Recursive listing
curl http://localhost:8500/v1/kv/myapp/?recurse=true
```

Check ACL policies if using Consul with ACLs enabled. Tokens need read access to the key path. Values are base64-encoded—decode them to see the actual content.

### DNS Resolution Testing

Test service discovery via DNS:

```bash
# Query service via DNS
dig @127.0.0.1 -p 8600 my-service.service.consul

# Query with SRV records (includes ports)
dig @127.0.0.1 -p 8600 my-service.service.consul SRV

# Check DNS port is listening
netstat -an | grep 8600
```

Applications must use the Consul DNS server (typically 127.0.0.1:8600) for .service.consul lookups. Standard DNS (port 53) won't resolve Consul service names.

### Health Check Debugging

Monitor health check status:

```bash
# Check individual service health
curl http://localhost:8500/v1/health/service/my-service?passing=true

# List all checks
curl http://localhost:8500/v1/health/state/any

# Get specific check details
curl http://localhost:8500/v1/health/check/my-service
```

Failed checks indicate application issues—check application logs for the health endpoint. The check type (HTTP, script, TTL) determines what causes failures.

## Best Practices

Use connection pooling and configure appropriate timeouts. Implement retry logic for transient failures. Use graceful degradation when Consul is unavailable. Monitor Consul metrics for cluster health. Enable ACLs and use specific policies rather than broad tokens.