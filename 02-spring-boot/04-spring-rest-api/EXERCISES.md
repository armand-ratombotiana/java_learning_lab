# Spring REST API - Exercises

## Lab Exercises

### Exercise 1: Run Application
```bash
mvn spring-boot:run
```
- Test all CRUD endpoints
- Use Postman or curl

### Exercise 2: REST Controller
- Review REST controller implementation
- Note HTTP method mappings
- Understand request/response handling

### Exercise 3: Error Handling
- Implement global exception handling
- Add `@ControllerAdvice`
- Return proper error responses

### Exercise 4: Validation
- Add `@Valid` to request bodies
- Use Bean Validation annotations
- Handle validation errors

### Exercise 5: Content Negotiation
- Support JSON and XML
- Use `@Produces` / `@Consumes`
- Test different Accept headers

### Exercise 6: API Documentation
- Add SpringDoc OpenAPI
- Access `/swagger-ui.html`
- Generate OpenAPI spec

## Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

## Testing Endpoints
```bash
curl http://localhost:8080/api/resources
curl -X POST -H "Content-Type: application/json" \
  -d '{"field":"value"}' http://localhost:8080/api/resources
```