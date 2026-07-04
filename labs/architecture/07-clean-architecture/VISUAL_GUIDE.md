# Visual Guide to Clean Architecture

## Layer Diagram
```
+------------------------------------------------------------+
|                    Frameworks & Drivers                      |
|  +------------------------------------------------------+  |
|  |              Interface Adapters                       |  |
|  |  +------------------------------------------------+  |  |
|  |  |      Application Business Rules                |  |  |
|  |  |  +------------------------------------------+  |  |  |
|  |  |  |   Enterprise Business Rules               |  |  |  |
|  |  |  |   (Entities)                              |  |  |  |
|  |  |  +------------------------------------------+  |  |  |
|  |  +------------------------------------------------+  |  |
|  +------------------------------------------------------+  |
+------------------------------------------------------------+
```

## Control Flow
```
Request
  |
  v
[Controller] (Adapter) --> [InputBoundary] (Interface)
                                |
                                v
                        [UseCaseInteractor]
                                |
                        +-------+-------+
                        |               |
                        v               v
                  [Entity]        [OutputBoundary] (Interface)
                                        |
                                        v
                                  [Presenter] (Adapter)
                                        |
                                        v
                                  [Response Model]
                                        |
                                        v
                                  [View/Client]
```

## Dependency Direction
```
Entity Layer:
  - No imports from outer layers
  - Only standard Java library

Use Case Layer:
  - Can import Entity layer
  - Defines input/output boundaries as interfaces

Adapter Layer:
  - Can import Entity and Use Case layers
  - Implements boundaries interfaces
  - Contains technology-specific code

Framework Layer:
  - Top-level configuration
  - Contains Spring Boot, database config
  - Wires everything together
```
