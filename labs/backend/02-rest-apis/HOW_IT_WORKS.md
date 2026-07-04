# How REST APIs Work

## Request/Response Cycle

1. Client sends HTTP request with method, URL, headers, body
2. Spring DispatcherServlet receives request
3. HandlerMapping determines which @RestController method handles it
4. Method executes with deserialized parameters
5. ResponseEntity or return value is serialized to JSON/XML
6. HTTP Response sent back with status code and headers

## Content Negotiation
`java
@GetMapping(value = "/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
// Client specifies Accept header -> server produces matching format
`

## Parameter Binding
`java
@GetMapping("/users")
public List<User> getUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size) { }
`

## Request Body Deserialization
Jackson ObjectMapper converts JSON to Java objects:
- @RequestBody triggers MessageConverter
- Jackson reads JSON, creates object graph
- Validation occurs via @Valid
- Object passed to controller method
