# Visual Guide: Modules

## Module Dependency Graph
```
[myapp] ──requires──> [database-lib] ──requires transitive──> [java.sql]
    │                        │
    └──requires──> [common-lib] ──requires──> [jackson]
                        │
                        └──opens──> [myapp.model]
```

## Module Path Structure
```
Module Path:
  mods/
  ├── com.example.myapp/          # Named module
  │   ├── module-info.class
  │   └── com/example/myapp/*.class
  ├── org.lib.database/           # Named module
  └── old-library.jar              # Automatic module
```

## jlink Custom Runtime
```
Distribution without jlink:
  jdk-21/ (full JDK, ~300MB)
  myapp.jar

Distribution with jlink:
  myapp-runtime/
  ├── bin/
  ├── lib/
  └── conf/
  (~40MB, only needed modules)
```

## Service Loading Flow
```
Module A (consumer)         Module B (provider)
  uses Service               provides Service with Impl
       │                             │
       └──────── ServiceLoader ──────┘
                          │
                    Service instance
```

## Module Resolution Waterfall
```
Root Modules
    ↓
Find modules on module path
    ↓
Read module-info from each module
    ↓
Build dependency graph
    ↓
Verify no cycles
    ↓
Check split packages
    ↓
Compute transitive closure
    ↓
Create Configuration
    ↓
Create ModuleLayer
```
