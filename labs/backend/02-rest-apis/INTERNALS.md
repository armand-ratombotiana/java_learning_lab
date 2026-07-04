# Internals of REST in Spring MVC

## RequestMappingHandlerMapping
- Maps @RequestMapping annotations to HandlerMethod objects
- Builds a lookup structure for fast dispatch
- Supports patterns, headers, params, produces/consumes conditions

## RequestResponseBodyMethodProcessor
- Reads request body using HttpMessageConverter
- Writes response body using HttpMessageConverter
- Supports JSON (Jackson), XML (JAXB), text, binary

## HttpMessageConverter Chain
1. MappingJackson2HttpMessageConverter (JSON)
2. MappingJackson2XmlHttpMessageConverter (XML)
3. StringHttpMessageConverter (text/plain)
4. FormHttpMessageConverter (form data)
5. ByteArrayHttpMessageConverter (application/octet-stream)

## ExceptionHandlerExceptionResolver
- Handles @ExceptionHandler methods in @ControllerAdvice
- Maps exceptions to ResponseEntity with appropriate status codes

## ContentNegotiationManager
- Determines requested media type from Accept header
- Falls back to file extension (.json, .xml)
- Configured via WebMvcConfigurer.configureContentNegotiation()
