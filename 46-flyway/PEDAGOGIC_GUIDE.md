# Pedagogic Guide - Flyway

## Learning Path

### Phase 1: Fundamentals
1. Understand version-based migration concept
2. Learn migration naming conventions (V, R, U prefixes)
3. Practice creating simple schema migrations

### Phase 2: Intermediate
1. Configure Flyway with different databases
2. Implement validation and checksum verification
3. Use undo and repair operations

### Phase 3: Advanced
1. Integrate with CI/CD pipelines
2. Multi-environment migration management
3. Team collaboration with shared migrations

## Key Concepts

| Concept | Description |
|---------|-------------|
| Versioned Migration | Applied once, ordered by version number |
| Repeatable Migration | Re-applied when checksum changes |
| Baseline | Mark existing database as migrated |
| Clean | Drop all objects in schemas |

## Common Pitfalls
- Mixing migration styles (always use one approach)
- Manual schema edits (always use migrations)
- No backup before migration in production

## Next Steps
- Learn Liquibase for XML/YAML-based migrations
- Compare migration strategies for teams