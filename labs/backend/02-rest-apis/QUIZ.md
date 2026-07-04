# REST API Quiz

1. What HTTP method is idempotent but NOT safe?
2. What status code should POST return on success?
3. What annotation combines @ResponseBody with @Controller?
4. How do you extract a URL path segment?
5. What status code indicates resource not found?
6. How do you handle exceptions globally in Spring MVC?
7. What is content negotiation in REST?
8. Which annotation enables request body validation?
9. How do you customize HTTP headers in the response?
10. What does 201 Created require in the Location header?

## Answers
1. PUT (idempotent, modifies state)
2. 201 Created
3. @RestController
4. @PathVariable
5. 404 Not Found
6. @ControllerAdvice + @ExceptionHandler
7. Server selects response format based on Accept header
8. @Valid
9. ResponseEntity with headers
10. URI of the newly created resource
