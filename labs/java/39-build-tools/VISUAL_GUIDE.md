# Visual Guide — Build Tools (Lab 39)

## Maven Lifecycle Phases Diagram

Maven has three built-in lifecycles: `default` (main), `clean`, `site`. The `default` lifecycle phases execute in order:

```
   validate ──► compile ──► test ──► package ──► verify ──► install ──► deploy
      │           │          │          │          │           │           │
      ▼           ▼          ▼          ▼          ▼           ▼           ▼
  Check      Compile    Run unit  Create    Run         Copy jar   Deploy to
  project    source     tests     jar/war   integration to local   remote
  is correct code                /ear       checks     repository  repository
```

- Each phase is a **lifecycle stage**, not a task. Plugins bind goals to phases.
- `mvn compile` runs all phases up to and including `compile`.
- `mvn clean install` runs `clean` lifecycle then `default` up to `install`.
- Common plugin bindings: `maven-compiler-plugin` → compile, `maven-surefire-plugin` → test, `maven-jar-plugin` → package.

## Gradle Build Flow

```
   ┌──────────────────────────────┐
   │  Initialization Phase         │
   │  - Locate settings.gradle     │
   │  - Create Project instances   │
   └─────────────┬────────────────┘
                 │
                 ▼
   ┌──────────────────────────────┐
   │  Configuration Phase         │
   │  - Execute build.gradle      │
   │  - Create Task graph (DAG)   │
   │  - Configure plugins/exts    │
   └─────────────┬────────────────┘
                 │
                 ▼
   ┌──────────────────────────────┐
   │  Execution Phase             │
   │  - Run tasks in order        │
   │    (depends-on determines    │
   │     execution order)         │
   └──────────────────────────────┘
```

- **Gradle is task-based** (vs Maven's phase-based). Tasks declare dependencies with `dependsOn`.
- **Build cache** reuses outputs from previous builds if inputs haven't changed.
- **Incremental builds**: only re-execute tasks whose inputs changed.
- Java plugin adds tasks: `compileJava`, `processResources`, `classes`, `jar`, `test`, etc.
- `gradle tasks` shows all available tasks; `gradle build` runs the full pipeline.
