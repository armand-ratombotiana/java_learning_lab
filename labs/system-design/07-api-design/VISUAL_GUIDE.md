# API Design - VISUAL GUIDE

## RESTful API Structure

```
/api/v1/
├── products/
│   ├── GET               (list products)
│   ├── POST              (create product)
│   └── {id}/
│       ├── GET           (get product)
│       ├── PUT           (update product)
│       ├── PATCH         (partial update)
│       ├── DELETE        (delete product)
│       └── reviews/
│           ├── GET       (list reviews)
│           └── POST      (create review)
├── orders/
│   ├── GET
│   ├── POST
│   └── {id}/
│       ├── GET
│       └── cancel/       (action)
│           └── POST
└── customers/
    └── ...
```

## Request/Response Flow

```
Client                              Server
  │                                    │
  │── GET /api/v1/products?page=0 ────►│
  │                                    │── Find products
  │                                    │── Count total
  │◄── 200 OK ─────────────────────────│
  │    {                               │
  │      "content": [...],             │
  │      "page": 0,                    │
  │      "totalPages": 50,             │
  │      "totalElements": 1000         │
  │    }                               │
  │                                    │
  │── POST /api/v1/products ──────────►│
  │    { "name": "...", ... }          │── Validate
  │                                    │── Save
  │◄── 201 Created ────────────────────│
  │    Location: /products/123         │
  │    { "id": "123", ... }            │
  │                                    │
  │── GET /api/v1/products/999 ───────►│
  │                                    │── Not found
  │◄── 404 Not Found ──────────────────│
  │    { "code": "NOT_FOUND",          │
  │      "message": "Product 999 ..."} │
```

## GraphQL vs REST

```
REST:
  GET /users/123                  ──► { id, name, email }
  GET /users/123/orders          ──► [ { id, ... } ]
  GET /orders/456/items          ──► [ { product, qty } ]
  3 round trips, over-fetching

GraphQL:
  POST /graphql
  {
    user(id: "123") {
      name
      orders {
        items { product { name } quantity }
      }
    }
  }
  1 round trip, exact data
```

## API Gateway Pattern

```
                      ┌─────────────┐
                      │   Clients   │
                      └──────┬──────┘
                             │
                      ┌──────▼──────┐
                      │ API Gateway  │
                      │              │
                      │ Auth       │
                      │ Rate Limit │
                      │ Routing    │
                      │ Caching    │
                      │ Logging    │
                      └──┬───┬───┬──┘
                         │   │   │
              ┌──────────┘   │   └──────────┐
              ▼              ▼              ▼
         ┌────────┐  ┌────────┐  ┌────────┐
         │Product │  │ Order  │  │Payment │
         │Service │  │Service │  │Service │
         └────────┘  └────────┘  └────────┘
```
