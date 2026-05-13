# Debugging REST API Issues

## Common Failure Scenarios

### 404 Errors

404 errors occur for several reasons beyond just the endpoint not existing. The most common is path mapping mismatch—your controller may use `@RequestMapping("/users")` but the actual request goes to `/user` or `/api/user`. Check the controller's base path and ensure it matches client expectations.

Missing `@ResponseBody` or returning `String` without it can cause issues. In older Spring versions, returning a String from a controller attempted to resolve a view, causing 404 if no view resolver matched. Spring Boot with default JSON converters usually handles this correctly, but it's still worth verifying.

Content negotiation can cause 404 when the client requests a format you don't support. If the Accept header is `application/xml` but you only produce JSON, Spring returns 406 (Not Acceptable) or falls back to default behavior.

### Validation Failures

Bean validation errors often produce 400 Bad Request, but the error messages can be confusing. The default Spring validation response is a generic error structure—you may need to configure a custom error response format. Missing `@Valid` on the controller method parameter results in validation being skipped silently.

Nested object validation requires `@Valid` on each nested object. Without it, nested validation constraints are not evaluated. Also remember that validation groups allow you to validate different constraints in different scenarios—using the wrong group results in no validation happening.

Date and number format errors are common but often produce cryptic messages. The `DateTimeFormat` annotation must match the incoming format exactly. If your API receives ISO-8601 but expects a custom format, parsing fails with an unhelpful exception.

### Stack Trace Examples

**Missing handler for path:**
```
WARNING: No matching handler method found for servlet request: path '/api/users', method 'POST', parameters multi-valued map
    at org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping.getHandler(RequestMappingInfoHandlerMapping.java:135)
```

**Validation failure response:**
```
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "errors": [
        {
            "field": "email",
            "defaultMessage": "must be a well-formed email address"
        }
    ]
}
```

**JsonMappingException:**
```
com.fasterxml.jackson.databind.exc.InvalidFormatException: Cannot deserialize value of type 'java.time.LocalDate' from String "2024/01/15"
```

## Debugging Techniques

### Diagnosing 404 Issues

Enable DEBUG logging for `org.springframework.web` to see which paths are being matched and which controllers are registered. This reveals if your controllers are even being detected by component scanning.

Verify that your controller is in a package that Spring Boot's component scan includes. The default scans from the package containing the main class. If your controller is in a subpackage that isn't scanned, it won't be registered.

Check for method-level path overrides—sometimes `@GetMapping("/specific")` overrides the class-level path incorrectly. Also verify HTTP method matches (GET vs POST vs PUT).

### Handling Validation Problems

Use a custom `Validator` to add custom validation logic and debug constraint evaluation. The `BindingResult` object contains detailed validation results—inspect it directly to see all errors.

Configure a global exception handler with `@ControllerAdvice` to log validation errors before returning. This helps you see exactly what constraints failed and what values were submitted.

Use `@JsonFormat` and `@DateTimeFormat` carefully—ensure they match on both serialization (output) and deserialization (input). Using `@JsonFormat` only covers Jackson serialization but not Spring's parameter binding.

## Best Practices

Always use `@Valid` on `@RequestBody` parameters to trigger validation. Without it, validation is skipped and invalid data enters your system. Use `@Validated` at the class level to enable method-level validation.

Return consistent error response structures. Define a standard error response class and use it everywhere—clients shouldn't have to parse different formats for different error types.

Document your API with OpenAPI (Swagger) to catch contract mismatches early. Generate client code from the specification to ensure both client and server use the same data types.

Use `Optional<T>` for query parameters and handle missing parameters explicitly. This prevents silent defaults and makes the API behavior clearer.