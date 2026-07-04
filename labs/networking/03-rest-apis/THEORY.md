# REST APIs - Theory

## REST Architectural Constraints

1. **Client-Server**: Separation of concerns
2. **Stateless**: Each request contains all necessary information
3. **Cacheable**: Responses must define cacheability
4. **Uniform Interface**: Standardized resource identification, manipulation via representations, self-descriptive messages, HATEOAS
5. **Layered System**: Client cannot tell if connected directly to end server
6. **Code on Demand** (optional): Server can extend client functionality

## Resource Design Principles
- Use nouns, not verbs: /users not /getUsers
- Use plural nouns for collections: /users, /orders
- Use hierarchical structure: /users/{id}/orders/{orderId}
- Use query parameters for filtering, sorting: /users?role=admin&sort=name

## HTTP Methods for CRUD
| Method | Action | Idempotent | Collection | Item |
|--------|--------|-----------|------------|------|
| GET | Read | Yes | List items | Get item |
| POST | Create | No | Create item | - |
| PUT | Replace | Yes | Replace collection | Replace item |
| PATCH | Partial update | No | - | Update item |
| DELETE | Delete | Yes | Delete collection | Delete item |

## Java Implementation
```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @GetMapping
    public List<Product> getAll(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size) {
        return productService.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@Valid @RequestBody Product product) {
        return productService.save(product);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @Valid @RequestBody Product product) {
        return productService.update(id, product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}
```

## Content Negotiation
```java
@GetMapping(value = "/{id}", produces = {"application/json", "application/xml"})
public Product getProduct(@PathVariable Long id) {
    return productService.findById(id).orElseThrow();
}
// Client sends: Accept: application/json
// Server responds: Content-Type: application/json
```
