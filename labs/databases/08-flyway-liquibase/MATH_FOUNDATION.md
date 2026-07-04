# Math Foundation: Database Migrations

## Version Comparison
Migrations are partially ordered by version. Version comparison uses:
- Major.minor.patch numeric parsing
- Strings split on `.`, `_`, then compared as integers where possible
- `V2.10.1` > `V2.9.9`

## Dependency Graph
Complex projects may have migration dependencies forming a DAG:
```
V1 → V2 → V3 → V4
        ↘ V3.1 ↗
```
Concurrent migrations to branches must have linearized versions before apply.

## Checksum
Flyway uses CRC32 checksums:
```
checksum = CRC32(normalized SQL content)
```
Prevents modification of applied migrations. Probability of collision: ~1 in 2^32.

## Migration Complexity
- Linear: N migrations applied in N steps
- Repeatable: R migrations applied on content change
- Undo: N + U steps (where U is undo count, up to N)
