# Real-World Project: Hot-Reload Plugin System

## Overview
Build a plugin system that loads, unloads, and hot-reloads Java plugins at runtime using custom class loaders. This is the pattern used by application servers (Tomcat, JBoss) and IDE plugin systems.

## Architecture
```
PluginManager
  ├── ClassLoader per plugin (child-first delegation)
  ├── Plugin interface (must be implemented by all plugins)
  ├── PluginConfig (YAML-based dependency declarations)
  └── WatcherService (file change watcher for .class/.jar changes)
```

## Key Features
1. **Isolation** — each plugin gets its own ClassLoader, preventing class conflicts
2. **Dependency resolution** — plugins can depend on other plugins
3. **Hot-reload** — watch a directory for file changes; when a `.class` file changes, create a new ClassLoader and reload
4. **Versioning** — support multiple versions of the same plugin
5. **Lifecycle** — `init()`, `start()`, `stop()`, `destroy()` hooks

## Implementation Details
- Use child-first delegation: check plugin ClassLoader before parent
- `URLClassLoader` for loading plugin JARs
- `java.nio.file.WatchService` for file change detection
- `ServiceLoader` pattern for plugin discovery via `META-INF/services`
- Weak references for plugin instances to allow garbage collection

## Evaluation Criteria
- Plugins can be added/removed without restarting the JVM
- No ClassCastException from stale class references
- Memory leaks are prevented (classes unload when ClassLoader is GC'd)
- Plugin A can depend on Plugin B's API

## Deliverables
- `PluginManager.java`
- `PluginClassLoader.java`
- `PluginWatcher.java`
- Example plugins
- Integration test with hot-reload scenario
