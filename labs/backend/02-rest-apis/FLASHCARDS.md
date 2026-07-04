# REST API Flashcards

Q: What is REST?
A: Representational State Transfer - architectural style for web APIs

Q: Key constraints of REST?
A: Stateless, cacheable, uniform interface, layered, client-server, code on demand

Q: @RestController vs @Controller?
A: @RestController = @Controller + @ResponseBody (no view resolution)

Q: How to read query parameters?
A: @RequestParam("name") String name

Q: How to read path variables?
A: @PathVariable("id") Long id

Q: What is ResponseEntity?
A: Full control over HTTP response: status, headers, body

Q: How to handle exceptions globally?
A: @ControllerAdvice with @ExceptionHandler methods

Q: 400 vs 404 vs 409?
A: 400=bad request, 404=not found, 409=conflict

Q: What is HATEOAS?
A: Hypermedia as Engine of Application State - links in API responses

Q: What is content negotiation?
A: Client specifies Accept header, server responds with matching format (JSON/XML)
