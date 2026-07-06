# Visual Guide — Java Logging (Lab 34)

## Logback Appender Chain Diagram

```
    Logger (com.myapp)
         │
         │  log.info("User {} logged in", id)
         ▼
    ┌────────────┐
    │   Filter   │  (optional: ThresholdFilter, EvaluatorFilter)
    └────┬───────┘
         │ (passes)
         ▼
    ┌────────────┐        ┌───────────────────┐
    │  Appender  │───────►│   Layout/Encoder   │
    │ (Console)  │        │  PatternLayout     │
    └────────────┘        │  "%d %-5level ..." │
                          └─────────┬─────────┘
                                    │
                                    ▼
                             ┌──────────────┐
                             │    stdout    │
                             └──────────────┘

         │
         ▼
    ┌────────────┐        ┌──────────────────┐
    │  Appender  │───────►│   RollingPolicy   │
    │  (File)    │        │  SizeAndTimeBased │
    └────────────┘        └────────┬─────────┘
                                   │
                                   ▼
                            ┌──────────────┐
                            │  app.log     │
                            │  app.2026-   │
                            │  07-06.0.gz  │
                            └──────────────┘
```

- **Loggers** are hierarchical: `com.myapp.service` inherits from `com.myapp`.
- **Appenders** write to destinations (console, file, DB, socket).
- **Layouts** format the output; `PatternLayout` is the most common.
- **Filters** can accept, deny, or neutral-evaluate events before appending.

## Log Levels Pyramid

```
       ┌─────┐
       │FATAL│   (highest — system crash)
       ├─────┤
       │ERROR│   (operation failed, needs attention)
       ├─────┤
       │ WARN│   (unexpected but non-critical)
       ├─────┤
       │ INFO│   (normal operational messages)
       ├─────┤
       │DEBUG│   (detailed diagnostic info)
       ├─────┤
       │ TRACE│  (finest-grained, e.g. every method entry/exit)
       └─────┘   (lowest)
```

- Configure the root level in `logback.xml`; more specific loggers can override.
- Production typically runs at `INFO`; debug/trace enabled only during troubleshooting.
