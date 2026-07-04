# Why Relational Databases Exist

Relational databases exist to solve the problem of **persistent, structured, and consistent data storage** with ad-hoc query capability.

## Problems Before RDBMS
- **Flat files**: No structure, no relationships, no concurrent access
- **Hierarchical/Network DBs**: Rigid navigation paths, complex programming
- **No isolation**: Applications built their own file-based storage with bugs

## Codd's Vision
E.F. Codd (IBM, 1970) proposed the relational model to:
- Separate **logical** data structure from **physical** storage
- Provide a **declarative** query language (SQL)
- Ensure **data independence** – applications unaffected by storage changes
- Enable **set-based operations** rather than record-at-a-time navigation

## Why Java Developers Need This
- JDBC and JPA are abstractions over the relational model
- ORM mapping requires understanding table→object relationships
- Transaction management (Spring `@Transactional`) leverages ACID
- Connection pooling assumes relational semantics
