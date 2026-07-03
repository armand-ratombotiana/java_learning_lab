# Architecture — Reflection

## Reflection in Framework Architecture
```
JUnit / TestNG
    │
    ├── @Test discovery: Class.forName + getMethods + isAnnotationPresent
    ├── Test invocation: method.invoke(testInstance)
    └── Assert checking: no reflection needed

Spring DI Container
    │
    ├── @Component scan: classpath scanning + Class.forName
    ├── @Inject/@Autowired: field.set(instance, dependency)
    └── @Transactional: Proxy.newProxyInstance + handler

Jackson / Gson
    │
    ├── Deserialization: Constructor.newInstance + field.set
    ├── Serialization: field.get(instance)
    └── @JsonProperty: annotation-based name mapping
```

## Reflective Service Locator
```
                  Service Locator
                  ┌────────────┐
Client ──► getService("email") │
                  │  Class.forName("EmailService")
                  │  constructor.newInstance()
                  │  return cached/new instance
                  └────────────┘
```

## Proxy-Based AOP
```
Client → Proxy ←→ Advice [Before] → Real Service → Advice [After]
           │                                    │
           └──────── InvocationHandler ─────────┘
                  intercepts method calls
```
