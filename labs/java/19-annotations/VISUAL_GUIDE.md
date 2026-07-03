# Visual Guide — Annotations

```
Annotation Lifecycle:

┌─────────────────────────────────────────────────────────┐
│ Source Code          Class File         Runtime         │
│                        .class                           │
│ @Deprecated         ┌──────────────┐                   │
│ class Old { } ──►  │ annotations  │ ──► getAnnotation()
│                     │              │                   │
│ @Retention(SOURCE)  │  Not stored  │  N/A              │
│ @Retention(CLASS)   │  Stored      │  Not accessible   │
│ @Retention(RUNTIME) │  Stored      │  Accessible       │
│                     └──────────────┘                   │
└─────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│  Custom Annotation Definition                          │
│                                                        │
│  @Retention(RUNTIME)  ◄── meta-annotation (how long)   │
│  @Target(METHOD)      ◄── meta-annotation (where)      │
│  @interface Auditable {                                │
│      String action();        ◄── attribute              │
│      String user() default "system"; ◄── default        │
│  }                                                      │
│                                                        │
│  Usage:                                                 │
│  @Auditable(action = "DELETE_USER", user = "admin")    │
│  void deleteUser(Long id) { ... }                      │
└────────────────────────────────────────────────────────┘
```
