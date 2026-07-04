# Math Foundation for Helm

N/A — Helm is primarily a packaging and templating tool with no significant mathematics.

## Version Comparison
- **Semver 2.0**: Chart versions follow semantic versioning (MAJOR.MINOR.PATCH).
- **Version constraints**: `>=1.2.3`, `~>2.0.0`, `^1.0.0` for dependency resolution.

## Template Functions (Basic)
- **String manipulation**: `upper`, `lower`, `trim`, `repeat`, `substr`.
- **Numeric**: `add`, `sub`, `mul`, `div`, `mod`, `max`, `min`.
- **Logic**: `and`, `or`, `not`, `eq`, `ne`, `lt`, `gt`.

## Dependency Resolution
- Dependency graph (DAG) for charts with multiple dependencies.
- Conflict resolution for overlapping version constraints.
- Transitive dependency flattening.

No advanced mathematics beyond basic semver and arithmetic operations.
