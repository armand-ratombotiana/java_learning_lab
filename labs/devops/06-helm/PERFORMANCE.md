# Helm Performance

## Chart Optimization
- **Template size**: Large templates slow rendering; split complex charts into subcharts.
- **Dependency management**: Minimize subchart count; use flat values structure.
- **Chart size**: Exclude unnecessary files (tests, CI config, docs) from packaged chart.
- **Schema validation**: Use values.schema.json for validation (catches errors early).

## Release Performance
- **Release count**: Many releases in one namespace slow `helm list`; use namespaces per app.
- **Secret storage**: Releases stored as large Secrets; many revisions consume etcd space.
- **Upgrade speed**: Charts with 100+ resources can take 30+ seconds.

## CI/CD Integration
- Cache charts locally (`helm dependency build` in CI cache).
- Use `--post-renderer` for external tools (kustomize, kyverno).
- Lint and template render in parallel with other CI stages.
- Use OCI registries for faster chart downloads (avoid pulling index.yaml).

## Common Bottlenecks
- Large number of hooks (each hook is a separate operation).
- Complex template logic (nested `range`/`with` blocks).
- Large chart archives (>10MB) slow download and extraction.
- Slow dependency resolution for charts with many transitive dependencies.
