# Deep Dive — DispatcherServlet Request Handling Chain

## DispatcherServlet Request Lifecycle

The `DispatcherServlet` is the front controller in Spring MVC. Every HTTP request passes through it. Here's the complete processing chain:

### 1. doService()

Entry point that prepares request attributes:

```java
@Override
protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
    // Preserve previous attributes for subsequent includes
    Map<String, Object> attributesSnapshot = null;
    if (WebUtils.isIncludeRequest(request)) {
        attributesSnapshot = new HashMap<>();
        Enumeration<?> attrNames = request.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String attrName = (String) attrNames.nextElement();
            attributesSnapshot.put(attrName, request.getAttribute(attrName));
        }
    }

    // Make framework objects available as request attributes
    request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
    request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, this.localeResolver);
    request.setAttribute(THEME_RESOLVER_ATTRIBUTE, this.themeResolver);
    request.setAttribute(THEME_SOURCE_ATTRIBUTE, getThemeSource());

    // Flash map support
    if (this.flashMapManager != null) {
        FlashMap inputFlashMap = this.flashMapManager.retrieveAndUpdate(request, response);
        if (inputFlashMap != null) {
            request.setAttribute(INPUT_FLASH_MAP_ATTRIBUTE, inputFlashMap);
        }
        request.setAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
    }

    try {
        doDispatch(request, response);
    } finally {
        // Restore attributes if include request
    }
}
```

### 2. doDispatch() — The Heart of MVC

```java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) {
    HttpServletRequest processedRequest = request;
    HandlerExecutionChain mappedHandler = null;
    boolean multipartRequestParsed = false;

    try {
        ModelAndView mv = null;
        Exception dispatchException = null;

        try {
            // Step 1: Handle multipart requests (file uploads)
            processedRequest = checkMultipart(request);
            multipartRequestParsed = processedRequest != request;

            // Step 2: Determine handler for the request
            mappedHandler = getHandler(processedRequest);
            if (mappedHandler == null) {
                noHandlerFound(processedRequest, response);
                return;
            }

            // Step 3: Find handler adapter for the handler
            HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

            // Step 4: Process last-modified header (if supported)
            String method = request.getMethod();
            boolean isGet = "GET".equals(method);
            if (isGet || "HEAD".equals(method)) {
                long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
                if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
                    return;
                }
            }

            // Step 5: Pre-processing interceptors
            if (!mappedHandler.applyPreHandle(processedRequest, response)) {
                return;
            }

            // Step 6: Actually invoke the handler (controller method)
            mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

            // Step 7: Apply default view name if needed
            if (asyncManager.isConcurrentHandlingStarted()) {
                return;
            }

            // Step 8: Post-processing interceptors
            mappedHandler.applyPostHandle(processedRequest, response, mv);

        } catch (Exception ex) {
            dispatchException = ex;
        } catch (Throwable err) {
            dispatchException = new NestedServletException("Handler dispatch failed", err);
        }

        // Step 9: Process result (view resolution or response writing)
        processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);

    } catch (Exception ex) {
        triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
    } catch (Throwable err) {
        triggerAfterCompletion(processedRequest, response, mappedHandler,
            new NestedServletException("Handler processing failed", err));
    } finally {
        if (async) {
            // Cleanup for async requests
        } else {
            request.removeAttribute(INPUT_FLASH_MAP_ATTRIBUTE);
        }
        if (multipartRequestParsed) {
            cleanupMultipart(processedRequest);
        }
    }
}
```

## HandlerMapping Hierarchy

HandlerMapping determines which handler (controller method) handles the request.

### HandlerMapping Interface

```java
public interface HandlerMapping {
    @Nullable
    HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
}
```

### HandlerMapping Implementations

| Implementation | Priority Order | Description |
|---------------|---------------|-------------|
| `RequestMappingHandlerMapping` | 0 | Maps `@RequestMapping` annotated methods |
| `BeanNameUrlHandlerMapping` | 2 | Maps beans named with URL pattern (e.g., `/users/**`) |
| `RouterFunctionMapping` | 3 | Maps functional WebMvc.fn routes |
| `WebMvcEndpointHandlerMapping` | 4 | Maps actuator endpoints |
| `SimpleUrlHandlerMapping` | Integer.MAX - 1 | Maps explicit URL patterns to handlers |
| `WelcomePageHandlerMapping` | Integer.MAX | Maps `/` to welcome page |

### RequestMappingHandlerMapping Internals

```java
public class RequestMappingHandlerMapping extends RequestMappingInfoHandlerMapping {
    // Maintains a mapping registry:
    private final MappingRegistry mappingRegistry = new MappingRegistry();

    // Internal class stores the request → handler mapping
    class MappingRegistry {
        private final Map<RequestMappingInfo, HandlerMethod> mappingLookup = new LinkedHashMap<>();
        private final MultiValueMap<String, RequestMappingInfo> urlLookup = new LinkedMultiValueMap<>();
        private final Map<String, List<HandlerMethod>> nameLookup = new ConcurrentHashMap<>();
        private final Map<HandlerMethod, CorsConfiguration> corsLookup = new ConcurrentHashMap<>();
    }

    // Lookup algorithm:
    // 1. Match URL path patterns (AntPathMatcher)
    // 2. Match HTTP method (GET, POST, etc.)
    // 3. Match consumes/produces media types
    // 4. Match parameters and headers
    // 5. Sort by specificity (most specific wins)
    // 6. Return the best match
}
```

### Pattern Matching (AntPathMatcher)

```
/                    → matches root
/users               → exact match
/users/*             → one path segment
/users/**            → zero or more segments
/users/{id}          → path variable
/users/{id:[0-9]+}   → path variable with regex
/users/**/details    → deep path
```

### Custom HandlerMapping

```java
@Component
public class VersionHandlerMapping extends RequestMappingHandlerMapping {
    private static final String VERSION_HEADER = "X-API-Version";

    @Override
    protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) {
        String version = request.getHeader(VERSION_HEADER);
        if (version != null) {
            request.setAttribute("apiVersion", version);
        }
        return super.lookupHandlerMethod(lookupPath, request);
    }
}
```

## HandlerAdapter Strategy Pattern

### HandlerAdapter Interface

```java
public interface HandlerAdapter {
    boolean supports(Object handler);
    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;
    long getLastModified(HttpServletRequest request, Object handler);
}
```

### Built-in HandlerAdapters

| Adapter | Handles | Invocation Method |
|---------|---------|------------------|
| `RequestMappingHandlerAdapter` | `HandlerMethod` (from `@RequestMapping`) | Reflection with `InvocableHandlerMethod` |
| `HttpRequestHandlerAdapter` | `HttpRequestHandler` | `handleRequest()` |
| `SimpleControllerHandlerAdapter` | `Controller` (old-style) | `handleRequest()` |
| `SimpleServletHandlerAdapter` | `javax.servlet.Servlet` (Jakarta equivalent) | `service()` |

### RequestMappingHandlerAdapter Internals

```java
public class RequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter
        implements BeanFactoryAware, InitializingBean {

    // Resolvers: convert request → method arguments
    private List<HandlerMethodArgumentResolver> customArgumentResolvers;
    private HandlerMethodArgumentResolverComposite argumentResolvers;

    // Return value handlers: convert method result → response
    private List<HandlerMethodReturnValueHandler> customReturnValueHandlers;
    private HandlerMethodReturnValueHandlerComposite returnValueHandlers;

    // Message converters: serialize objects
    private List<HttpMessageConverter<?>> messageConverters;

    @Override
    public void afterPropertiesSet() {
        initControllerAdviceCache();
        initArgumentResolvers();
        initReturnValueHandlers();
        initMessageConverters();
    }
}
```

## HandlerMethodArgumentResolver Strategy Pattern

### Resolver Chain

When a controller method is invoked, each parameter is resolved by iterating through the resolver chain:

```java
// HandlerMethodArgumentResolverComposite.resolveArgument()
for (HandlerMethodArgumentResolver resolver : this.argumentResolvers) {
    if (resolver.supportsParameter(parameter)) {
        return resolver.resolveArgument(parameter, mavContainer, request, binderFactory);
    }
}
throw new IllegalArgumentException("Unsupported parameter");
```

### Built-in Argument Resolvers (Ordered)

| # | Resolver | Handles | Annotation |
|---|----------|---------|------------|
| 1 | `PathVariableMapMethodArgumentResolver` | `Map<String, String>` | `@PathVariable` |
| 2 | `PathVariableMethodArgumentResolver` | Any type | `@PathVariable` |
| 3 | `MatrixVariableMapMethodArgumentResolver` | `Map<String, String>` | `@MatrixVariable` |
| 4 | `MatrixVariableMethodArgumentResolver` | Any type | `@MatrixVariable` |
| 5 | `RequestParamMethodArgumentResolver` | Simple types | `@RequestParam` |
| 6 | `RequestParamMapMethodArgumentResolver` | `Map<String, ?>` | `@RequestParam` |
| 7 | `SessionStatusMethodArgumentResolver` | `SessionStatus` | None |
| 8 | `SessionAttributeMethodArgumentResolver` | Any type | `@SessionAttribute` |
| 9 | `RequestHeaderMethodArgumentResolver` | Simple types, `Map` | `@RequestHeader` |
| 10 | `RequestHeaderMapMethodArgumentResolver` | `Map<String, String>` | `@RequestHeader` |
| 11 | `ServletCookieValueMethodArgumentResolver` | `Cookie` | `@CookieValue` |
| 12 | `ServletRequestMethodArgumentResolver` | `HttpServletRequest`, `HttpServletResponse`, etc. | None |
| 13 | `ServletResponseMethodArgumentResolver` | `HttpServletResponse`, `OutputStream`, `Writer` | None |
| 14 | `ModelMethodProcessor` | `Model`, `Map`, `ModelMap` | None |
| 15 | `ErrorsMethodArgumentResolver` | `Errors`, `BindingResult` | None |
| 16 | `ModelAttributeMethodProcessor` | Complex types | `@ModelAttribute` |
| 17 | `RequestResponseBodyMethodProcessor` | Any type | `@RequestBody` |
| 18 | `HttpEntityMethodProcessor` | `RequestEntity`, `ResponseEntity` | None |
| 19 | `RedirectAttributesMethodArgumentResolver` | `RedirectAttributes` | None |
| 20 | `PrincipalMethodArgumentResolver` | `Principal` | None |
| 21 | `LocaleContextResolverMethodArgumentResolver` | `Locale` | None |
| 22 | `PathVariableMethodArgumentResolver` (duplicate) | `Controller` impl | None |

### Custom Argument Resolver

```java
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
            && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        return request.getAttribute("currentUser");
    }
}
```

Registration:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserArgumentResolver());
    }
}
```

## HandlerMethodReturnValueHandler Strategy Pattern

### Return Value Handler Chain

```java
// HandlerMethodReturnValueHandlerComposite.handleReturnValue()
for (HandlerMethodReturnValueHandler handler : this.returnValueHandlers) {
    if (handler.supportsReturnType(returnType)) {
        handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        return;
    }
}
throw new IllegalArgumentException("Unsupported return type");
```

### Built-in Return Value Handlers (Ordered)

| # | Handler | Handles Return Type | What It Does |
|---|---------|-------------------|--------------|
| 1 | `ModelAndViewMethodReturnValueHandler` | `ModelAndView` | Sets the model and view |
| 2 | `ModelMethodProcessor` | `Model` | Sets model attributes |
| 3 | `ViewMethodReturnValueHandler` | `View`, `String` (view name) | Resolves view name |
| 4 | `ResponseBodyHandlerReturnValueHandler` | Any with `@ResponseBody` | Serializes to response |
| 5 | `ResponseEntityHandlerReturnValueHandler` | `ResponseEntity` | Sets headers, status, body |
| 6 | `ModelAttributeMethodProcessor` | Any with `@ModelAttribute` | Adds to model |
| 7 | `MapMethodProcessor` | `Map` | Adds as model attributes |
| 8 | `CallableMethodReturnValueHandler` | `Callable<?>` | Async handler |
| 9 | `DeferredResultMethodReturnValueHandler` | `DeferredResult<?>` | Async handler |
| 10 | `ListenableFutureReturnValueHandler` | `ListenableFuture<?>` | Async handler |
| 11 | `CompletableFutureReturnValueHandler` | `CompletableFuture<?>` | Async handler |
| 12 | `ViewNameMethodReturnValueHandler` | `String` (view name) | Resolves to view |
| 13 | `StreamingResponseBodyReturnValueHandler` | `StreamingResponseBody` | Streams response |
| 14 | `ResponseBodyEmitterReturnValueHandler` | `ResponseBodyEmitter` | SSE/streaming |

### @ResponseBody Processing

When `@ResponseBody` or `@RestController` is used, `RequestResponseBodyMethodProcessor` handles serialization:

```java
public class RequestResponseBodyMethodProcessor extends AbstractMessageConverterMethodProcessor {
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) {
        mavContainer.setRequestHandled(true);
        ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
        ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);

        // Content negotiation to determine media type
        try {
            writeWithMessageConverters(returnValue, returnType, inputMessage, outputMessage);
        } catch (HttpMediaTypeNotAcceptableException e) {
            // Handle not acceptable
        }
    }
}
```

### Custom Return Value Handler

```java
public class CsvReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(ResponseCsv.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) {
        mavContainer.setRequestHandled(true);
        HttpServletResponse response = (HttpServletResponse) webRequest.getNativeResponse();
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=data.csv");

        try (PrintWriter writer = response.getWriter()) {
            // Convert object list to CSV
            List<?> items = (List<?>) returnValue;
            writer.println("id,name,email");
            for (Object item : items) {
                writer.println(convertToCsv(item));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```

## ViewResolver Resolution

### ViewResolver Chain

Types of ViewResolvers (ordered):

```java
public interface ViewResolver {
    View resolveViewName(String viewName, Locale locale) throws Exception;
}
```

| ViewResolver | Priority | Description |
|-------------|----------|-------------|
| `BeanNameViewResolver` | Low | Resolves `View` beans by bean name |
| `UrlBasedViewResolver` (Thymeleaf) | Medium | Resolves by URL template |
| `InternalResourceViewResolver` | Low | JSP resolver |
| `ContentNegotiatingViewResolver` | Highest | Delegates to other resolvers based on media type |

### ThymeleafViewResolver Internals

```java
// ThymeleafViewResolver extends UrlBasedViewResolver
// viewName = "users/list" → template: users/list.html
// viewName = "redirect:/home" → redirect prefix
// viewName = "forward:/home" → forward prefix
```

## HttpMessageConverter

### Converter Interface

```java
public interface HttpMessageConverter<T> {
    boolean canRead(Class<?> clazz, MediaType mediaType);
    boolean canWrite(Class<?> clazz, MediaType mediaType);
    List<MediaType> getSupportedMediaTypes();
    T read(Class<? extends T> clazz, HttpInputMessage inputMessage);
    void write(T t, MediaType contentType, HttpOutputMessage outputMessage);
}
```

### Built-in Message Converters

| Converter | Media Types | Class Support | Order |
|-----------|-------------|---------------|-------|
| `ByteArrayHttpMessageConverter` | `*/*` | `byte[]` | Low |
| `StringHttpMessageConverter` | `text/*`, `*/*` | `String` | Low |
| `MappingJackson2HttpMessageConverter` | `application/json`, `application/*+json` | Any | Medium |
| `MappingJackson2XmlHttpMessageConverter` | `application/xml`, `text/xml` | Any | Medium |
| `SourceHttpMessageConverter<Source>` | `application/xml`, `text/xml` | `DOMSource`, `SAXSource`, `StreamSource` | Low |
| `BufferedImageHttpMessageConverter` | `image/*`, `application/octet-stream` | `BufferedImage` | Low |
| `ResourceHttpMessageConverter` | `application/octet-stream`, `*/*` | `Resource` | Low |
| `ProtobufHttpMessageConverter` | `application/x-protobuf` | `Message` | Low |
| `AtomFeedHttpMessageConverter` | `application/atom+xml` | SyndFeed | Low |
| `RssChannelHttpMessageConverter` | `application/rss+xml` | Channel | Low |

### Write Selection Algorithm

```java
// AbstractMessageConverterMethodProcessor.writeWithMessageConverters()
// 1. Get the return type class
// 2. Get requested media types from ContentNegotiationManager
// 3. For each requested media type:
//    a. For each message converter:
//       - If converter.canWrite(returnType, mediaType) → use it
// 4. If no match found: throw HttpMediaTypeNotAcceptableException
// 5. Write with selected converter
```

### Jackson Serialization Internals

```java
// MappingJackson2HttpMessageConverter.write()
protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) {
    ObjectMapper objectMapper = getObjectMapper();
    JsonGenerator generator = objectMapper.getFactory().createGenerator(outputMessage.getBody());

    // Serialization features applied:
    // - SerializationFeature.INDENT_OUTPUT (pretty print if enabled)
    // - SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
    // - SerializationFeature.FAIL_ON_EMPTY_BEANS
    // - DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
    // - MapperFeature.DEFAULT_VIEW_INCLUSION

    ObjectWriter writer = objectMapper.writer();
    if (object instanceof TypeReference) {
        writer = writer.forType(((TypeReference<?>) object).getType());
    }
    writer.writeValue(generator, object);
}
```

## Interceptor Chain

### HandlerInterceptor Lifecycle

```java
public interface HandlerInterceptor {
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
    }

    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                            @Nullable ModelAndView modelAndView) {}

    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                 @Nullable Exception ex) {}
}
```

### Execution Order

For a chain of 3 interceptors (A, B, C):

```
preHandle A → preHandle B → preHandle C → handler → postHandle C → postHandle B → postHandle A → afterCompletion A → afterCompletion B → afterCompletion C
```

If B.preHandle() returns false:
```
preHandle A → preHandle B (returns false) → afterCompletion A
```

## Async Request Processing

### DeferredResult Pattern

```java
@GetMapping("/async")
public DeferredResult<String> asyncHandler() {
    DeferredResult<String> result = new DeferredResult<>(5000L); // timeout

    // Executor processes in separate thread
    executorService.submit(() -> result.setResult("Processed"));

    return result;
}
```

### Callable Pattern

```java
@GetMapping("/callable")
public Callable<String> callableHandler() {
    return () -> {
        Thread.sleep(1000);
        return "Processed by " + Thread.currentThread().getName();
    };
}
```

## Error Handling in Dispatcher Chain

### Exception Handler Resolution

1. `HandlerExceptionResolver` chain (ordered):
   - `ExceptionHandlerExceptionResolver` — `@ExceptionHandler` methods
   - `ResponseStatusExceptionResolver` — `@ResponseStatus` exceptions
   - `DefaultHandlerExceptionResolver` — Spring's default exceptions

2. Resolution process:

```java
// DispatcherServlet.processDispatchResult()
protected void processDispatchResult(HttpServletRequest request, HttpServletResponse response,
                                     HandlerExecutionChain mappedHandler, ModelAndView mv, Exception exception) {
    if (exception != null) {
        // Try to resolve with HandlerExceptionResolver chain
        ModelAndView exceptionMv = processHandlerException(request, response, mappedHandler, exception);
        // If exception resolved, use it as the model
        mv = exceptionMv;
    }

    // Did the handler choose a view?
    if (mv != null && !mv.wasCleared()) {
        render(mv, request, response);  // View resolution
    } else {
        // Response already written (e.g., @ResponseBody with error)
    }
}
```