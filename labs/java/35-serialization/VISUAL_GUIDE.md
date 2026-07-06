# Visual Guide — Java Serialization (Lab 35)

## Serialization / Deserialization Flow

```
   ┌──────────────────┐
   │  Java Object     │
   │  (in heap)       │
   │                  │
   │  User            │
   │   - name: "Alice"│
   │   - age: 30      │
   │   - transient: x │ (skipped)
   └────────┬─────────┘
            │
            │ ObjectOutputStream.writeObject()
            ▼
   ┌──────────────────┐
   │  Byte Stream     │
   │  (serialized     │
   │   representation)│
   │                  │
   │  AC ED 00 05 ... │  (magic bytes 0xACED)
   └────────┬─────────┘
            │
            │ (file, network, message queue...)
            ▼
   ┌──────────────────┐
   │  Byte Stream     │
   │  (same bytes)    │
   └────────┬─────────┘
            │
            │ ObjectInputStream.readObject()
            ▼
   ┌──────────────────┐
   │  Java Object     │
   │  (new heap copy) │
   │                  │
   │  User            │
   │   - name: "Alice"│
   │   - age: 30      │
   │   - transient:   │ (default value)
   └──────────────────┘
```

- Class must implement `Serializable` (marker interface).
- `serialVersionUID` ensures class compatibility across versions.
- `transient` fields are not serialized; `static` fields are never serialized.
- Custom logic via `writeObject()` / `readObject()` private methods.

## Comparison Table — Serialization Approaches

| Feature           | Java Native | JSON (Jackson) | Protocol Buffers |
|-------------------|-------------|----------------|------------------|
| Format            | Binary      | Text           | Binary           |
| Human-readable    | No          | Yes            | No               |
| Schema required   | No (implicit)| No             | Yes (.proto)     |
| Cross-language    | Java only   | Yes            | Yes              |
| Performance       | Fast        | Slow           | Fastest          |
| Size              | Medium      | Large          | Small            |
| Versioning        | UID-based   | Field names    | Field numbers    |

Use Java native for simple intra-JVM persistence; JSON for REST APIs; Protobuf/Avro for high-throughput inter-service communication.
