# API Design - ARCHITECTURE

## Full API Architecture

```
┌────────────────────────────────────────────────────────────┐
│                    Client Applications                      │
│  ┌────────┐  ┌────────┐  ┌────────┐  ┌────────────────┐  │
│  │ Web    │  │ Mobile │  │ 3rd    │  │ Internal       │  │
│  │ App    │  │ App    │  │ Party  │  │ Services       │  │
│  └───┬────┘  └───┬────┘  └───┬────┘  └───────┬────────┘  │
│      │           │           │               │          │
└──────┼───────────┼───────────┼───────────────┼──────────┘
       │           │           │               │
       │           │           │               │
┌──────▼───────────▼───────────▼───────────────▼──────────┐
│                     API Gateway                          │
│                                                         │
│  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐          │
│  │ Auth   │ │ Rate   │ │ Route  │ │ Cache  │          │
│  │        │ │ Limit  │ │        │ │        │          │
│  └────────┘ └────────┘ └────────┘ └────────┘          │
│                                                         │
│  ┌──────────────────────────────────────────────────┐  │
│  │            API Version Router                    │  │
│  │  /api/v1/* → v1 controllers                     │  │
│  │  /api/v2/* → v2 controllers                     │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────┘
       │           │           │               │
┌──────▼───┐ ┌────▼────┐ ┌───▼──────┐ ┌──────▼───┐
│Product   │ │ Order   │ │ Customer │ │ Payment  │
│Service   │ │ Service │ │ Service  │ │ Service  │
└──────────┘ └─────────┘ └──────────┘ └──────────┘
```

## API Protocol Selection

| Protocol | Use Case | Request Style | Performance |
|----------|----------|--------------|-------------|
| REST | General purpose | CRUD operations | Good |
| GraphQL | Complex queries, mobile | Custom queries | Good (no over-fetch) |
| gRPC | Internal services, streaming | Binary, typed calls | Excellent |
| WebSocket | Real-time updates | Persistent connection | Low latency |
| Webhook | Event notifications | Callbacks | Asynchronous |

## API Gateway Deployment

```
┌──────────────────────────────┐
│  Multiple API Gateways       │
│  (for high availability)     │
│                              │
│  ┌──────────┐ ┌──────────┐  │
│  │ Gateway 1 │ │ Gateway 2 │ │
│  │ (active)  │ │ (active)  │ │
│  └──────────┘ └──────────┘  │
│                              │
│  Shared Redis for            │
│  rate limit state            │
└──────────────────────────────┘
```

## API Documentation Structure

| Endpoint | Method | Description | Auth |
|----------|--------|-------------|------|
| /api/v1/products | GET | List products | Public |
| /api/v1/products/{id} | GET | Get product | Public |
| /api/v1/products | POST | Create product | Bearer |
| /api/v1/products/{id} | PUT | Update product | Bearer |
| /api/v1/products/{id} | DELETE | Delete product | Bearer+Admin |
| /api/v1/orders | POST | Place order | Bearer |
| /api/v1/orders/{id} | GET | Get order | Bearer |
