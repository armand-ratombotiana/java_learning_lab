# INTERVIEW — Proxy Pattern

## Company-Specific Focus

### Google
- Dynamic proxy vs compile‑time weaving (AspectJ)
- `Proxy.isProxyClass()`, `Proxy.getInvocationHandler()`

### Amazon
- Spring AOP — proxy‑based vs AspectJ
- CGLIB proxies for class‑based targets (no interface needed)

### Meta
- Proxy pattern for API rate limiting
- How Hibernate uses proxies for lazy loading

## Common Questions
1. Why does `Proxy` require an interface?
2. What is the class name pattern of generated proxies (`$Proxy0`)?
3. CGLIB `Enhancer` vs `java.lang.reflect.Proxy` — trade‑offs
