# REST API Exercises

## Exercise 1: CRUD API for Products
Build a REST API for products with:
- GET /api/products (paginated, 20 per page)
- GET /api/products/{id}
- POST /api/products (with validation)
- PUT /api/products/{id}
- DELETE /api/products/{id}
- @ControllerAdvice for error handling
- Proper HTTP status codes

## Exercise 2: Search and Filter
Add search and filter capabilities:
- GET /api/products?category=electronics
- GET /api/products/search?q=keyword
- GET /api/products?sort=price,desc
- GET /api/products?page=0&size=10&sort=name,asc

## Exercise 3: HATEOAS
Add hypermedia links to responses using Spring HATEOAS.

## Exercise 4: API Versioning
Implement both URL-based (/v1/, /v2/) and header-based versioning.

## Exercise 5: Async REST
Use @Async with CompletableFuture for long-running operations.
