# API Design - MENTAL_MODELS

## Mental Model 1: The Restaurant Menu
- **Menu = API**: Lists available options clearly
- **Dish = Endpoint**: Each has a clear name and description
- **Ingredients = Parameters**: What you need to specify
- **Chef = Server**: Prepares what you ordered (nothing more)
- **Waiter = Client**: Reads the menu and orders accordingly

## Mental Model 2: The Library
- **Catalog = API Documentation**: Find what you need
- **Dewey Decimal = URL Structure**: Consistent, hierarchical
- **Different sections = Endpoints**: Books, magazines, media
- **Checkout = POST**: Creates a new resource (loan)
- **Return = DELETE**: Removes the loan

## Mental Model 3: The Vending Machine
- **Buttons = Endpoints**: Clearly labeled actions
- **Payment = Authentication**: Must be authorized
- **Selection = Request Body**: What you want
- **Dispensed item = Response**: What you get
- **Error display = Error codes**: Tells you what went wrong

## REST vs GraphQL vs gRPC

| Aspect | REST | GraphQL | gRPC |
|--------|------|---------|------|
| Data fetching | Fixed | Client-defined | Fixed (streaming) |
| Protocol | HTTP/1.1 | HTTP/1.1 | HTTP/2 |
| Serialization | JSON | JSON | Protobuf (binary) |
| Typing | Optional (OpenAPI) | Strong | Strong |
| Caching | HTTP caching | Limited | Not built-in |
| Tooling | Mature | Growing | Mature in polyglot |

## API Maturity Model (Richardson)

```
Level 0: Swamp of POX (one endpoint, one method)
Level 1: Resources (multiple endpoints, one method)
Level 2: HTTP Verbs (GET, POST, PUT, DELETE)
Level 3: Hypermedia Controls (HATEOAS)
```
Most APIs should aim for Level 2. Level 3 is ideal for evolvable APIs.
