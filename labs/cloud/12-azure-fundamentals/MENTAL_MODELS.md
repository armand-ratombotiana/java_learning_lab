# Mental Models - Azure Fundamentals

## 1. The Workshop Model
**Azure Fundamentals** is like a well-organized garage workshop:
- **Tools**: APIs and libraries (specialized wrenches)
- **Workbench**: Service runtime (workspace)
- **Storage**: Database and cache (tool chest)
- **Blueprint**: Configuration and docs (plans)

## 2. The Restaurant Kitchen Model
Request processing is like a restaurant:
- **Host**: Load balancer seats customers (routes)
- **Chef**: Core service processes orders (business logic)
- **Pantry**: Database stores ingredients (data)
- **Expeditor**: Monitoring tracks progress (observability)

## 3. The Circuit Board Model
Components connect like a circuit:
- **Traces**: Current flowing (request processing)
- **Capacitors**: Caches smooth spikes
- **Resistors**: Rate limiters control flow
- **Fuses**: Circuit breakers prevent cascade failures

## 4. The Library Model
Composition is like a library system:
- **Catalog**: Service registry
- **Stacks**: Service versions
- **Checkout**: Request handling
- **Return**: Response generation

## 5. The Postal Service Model
Communication works like mail:
- **Addresses**: Endpoints and queues
- **Envelopes**: Request/response payloads
- **Post Office**: Message broker/event bus
- **Tracking**: Distributed tracing
