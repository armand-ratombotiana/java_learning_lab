# INTERVIEW — Module Reflection

## Company-Specific Focus

### Oracle
- `ModuleDescriptor` — exports, opens, requires, uses, provides
- `ModuleLayer.boot().modules()` — introspection of boot layer

### Google
- `--add-opens` in microservice architectures
- Reflection with modules vs pre‑Java‑9

### Amazon
- Internal library isolation via custom module layers
- `ModuleLayer.defineModulesWithOneLoader()`

## Common Questions
1. What is the difference between `exports` and `opens`?
2. How does `addOpens` affect security?
3. Can unnamed modules reflect into named modules?
