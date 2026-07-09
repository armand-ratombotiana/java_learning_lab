# Math Foundation — Spring Boot Internals

## Complexity Analysis

### Startup Sequence Complexity

| Phase | Complexity | Description |
|-------|-----------|-------------|
| Class scanning | O(n) | n = classpath entries |
| Configuration parsing | O(k) | k = @Configuration classes |
| Conditional evaluation | O(c × m) | c = configurations, m = conditions each |
| Bean definition registration | O(b) | b = total bean definitions |

### DispatcherServlet Request

| Step | Complexity | Description |
|------|-----------|-------------|
| getHandler() | O(h) | h = HandlerMapping chain length |
| Argument resolution | O(a) | a = ArgumentResolvers tried |
| Serialization | O(s) | s = object size |
| View resolution | O(v) | v = ViewResolvers tried |

### Actuator

| Endpoint | Complexity | Description |
|----------|-----------|-------------|
| /health | O(i) | i = health indicators |
| /metrics | O(c) | c = registered metrics |
| /beans | O(b) | b = bean definitions |
| /mappings | O(m) | m = handler mappings |

## WebApplicationType Detection

```
SERVLET if:
  isPresent("jakarta.servlet.Servlet")
  && !isReactive()

REACTIVE if:
  isPresent("org.springframework.web.reactive.DispatcherHandler")

NONE if:
  neither
```

## Conditional Annotation Processing

Condition evaluation uses a matching algorithm:
1. For `@ConditionalOnClass`: `ClassNameFilter.matches()` checks each class name
2. For `@ConditionalOnBean`: Bean factory lookup, O(log n) via hash map
3. For `@ConditionalOnProperty`: Property lookup, O(1) per property

## Content Negotiation Resolution

Media type resolution from `Accept` header:
1. Split header by `,` (comma-separated)
2. Each media type parsed with quality factor
3. Media types sorted by quality (highest first)
4. First type that matches a configured converter is selected