# Pedagogic Guide - Liquibase

## Learning Path

### Phase 1: Fundamentals
1. Understand changelog and changeset concepts
2. Learn different changelog formats (XML, YAML, JSON, SQL)
3. Create basic change types (createTable, addColumn)

### Phase 2: Intermediate
1. Implement rollback strategies
2. Use contexts for environment-specific changes
3. Configure preconditions and failFast

### Phase 3: Advanced
1. Custom change types
2. Integration with build tools
3. Database-specific configurations

## Key Concepts

| Concept | Description |
|---------|-------------|
| ChangeSet | Unit of change with id, author, and changes |
| Changelog | Collection of changesets |
| Context | Environment filter (dev, test, prod) |
| Precondition | Condition that must be met |

## Comparison with Flyway
- Liquibase: Multiple formats, rollback support, more complex
- Flyway: SQL-only, simpler, easier to learn

## Common Pitfalls
- Duplicate changeset IDs
- Not testing rollback in development
- Mixing formats in one project