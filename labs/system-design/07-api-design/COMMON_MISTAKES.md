# API Design - COMMON MISTAKES

## 1. Verbs in URLs
```java
// WRONG
POST /api/createProduct
POST /api/getProducts
GET  /api/deleteProduct?id=123

// RIGHT
POST   /api/products          (create)
GET    /api/products           (list)
DELETE /api/products/{id}      (delete)
```

## 2. Inconsistent Naming
```java
// WRONG: Mix of camelCase and snake_case
GET /api/getUserOrders          // camelCase URL
Response: { "user_id": 123 }    // snake_case body

// RIGHT: Consistent casing
GET /api/users/123/orders
Response: { "userId": 123 }
```

## 3. No Error Response Standard
```java
// WRONG: Different formats per endpoint
{ "error": "not found" }
{ "status": 404, "message": "User not found", "code": "USR_404" }
"User not found"  // plain text

// RIGHT: Consistent format
{ "code": "NOT_FOUND", "message": "User with id 123 not found", "instance": "/api/users/123" }
```

## 4. Returning 200 for Errors
All errors should use appropriate HTTP status codes. 200 always means success.

## 5. Ignoring Idempotency
POST to create an order multiple times creates multiple orders. Use idempotency keys.

## 6. Too Chatty APIs
```java
// WRONG: Multiple round trips for related data
GET /users/123
GET /users/123/orders
GET /orders/456/items

// RIGHT: Include related data
GET /users/123?include=orders.items
// Or use GraphQL
```

## 7. No Pagination
Returning unlimited results causes server overload and poor client performance.

## 8. Nested Resources Too Deep
```java
// WRONG: Deep nesting
GET /api/v1/orgs/123/projects/456/sprints/789/tasks/321

// RIGHT: Flatten with query params
GET /api/v1/tasks?sprintId=789&projectId=456&orgId=123
```

## 9. Exposing Internal IDs
Don't expose database auto-increment IDs. Use UUIDs or public IDs.

## 10. Missing Rate Limiting
APIs without rate limits are vulnerable to abuse and accidental overload.
