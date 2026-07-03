# Architecture — Virtual Threads

## Three-Tier Virtual Thread Architecture
```
[Request Handler] ── virtual thread per request ──► [Service Layer]
       │                                                   │
       │                                                   ▼
       │                                          [DB/IO calls block, yield]
       │                                                   │
       └───────────────────────────────────────────────────┘
                   Virtual thread resumes when IO completes
```

## Where Virtual Threads Excel
- **IO-bound tasks** — web requests, database calls, HTTP clients
- **Many concurrent connections** — chat servers, streaming
- **Synchronous APIs** — legacy blocking code becomes scalable

## Where They Don't
- **CPU-bound tasks** — use platform threads (ForkJoinPool)
- **Long-running synchronized blocks** — pinning defeats purpose
- **Real-time systems** — primitive virtual thread scheduling (no priority)

## Integration with Existing Frameworks
- Spring Boot: `spring.threads.virtual.enabled=true`
- Tomcat: NIO connector can dispatch to virtual threads
- JDBC: blocking drivers work transparently
