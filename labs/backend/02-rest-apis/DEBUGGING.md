# Debugging REST APIs

## Enable Request Logging
```properties
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.web.servlet.mvc.method.annotation=DEBUG
```

## View All Mappings
```bash
curl http://localhost:8080/actuator/mappings | jq
```

## Common Issues

### 415 Unsupported Media Type
```bash
# Fix: Add Content-Type header
curl -X POST -H "Content-Type: application/json" -d '{}' http://...
```

### 400 Bad Request
Check validation annotations and request body format:
```properties
logging.level.org.springframework.web=TRACE
```

### 404 Not Found
- Check URL path vs @RequestMapping value
- Check component scan includes controller package
- Check @PathVariable("name") matches path variable name

### 405 Method Not Allowed
- Verify HTTP method annotation matches (GET vs POST)

## Testing Tools
- Postman/Insomnia for manual testing
- WireMock for mocking external APIs
- MockMvc for integration testing
- TestRestTemplate for full integration tests
