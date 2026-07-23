# Mock Interview Transcript: Build Tools

## Interviewer: Senior SWE, Microsoft
## Candidate: Mid-level Java developer
## Time: 25 minutes
## Focus: Maven, Gradle, build lifecycle, dependency management

---

**Q1: Compare Maven and Gradle.**

**Candidate**: Maven uses XML, is convention-based, and has a strict lifecycle. Gradle uses Groovy/Kotlin DSL, is flexible, and supports incremental builds. Maven is predictable but verbose. Gradle is faster (incremental compilation, build cache, daemon) but has a steeper learning curve. Maven dominates in enterprises; Gradle dominates in Android and modern projects.

**Interviewer**: Explain the Maven build lifecycle phases.

**Candidate**: Default lifecycle: `validate` → `compile` → `test` → `package` → `verify` → `install` → `deploy`. Each phase runs all prior phases. `compile` compiles source code. `test` runs unit tests. `package` creates JAR/WAR. `install` puts artifact in local repository. `deploy` uploads to remote repository.

**Interviewer**: How does Maven dependency resolution work (transitive dependencies)?

**Candidate**: Maven reads `pom.xml` of each dependency (transitively). Conflicts: (1) "Nearest wins" — the shortest path to a dependency is used. (2) If same depth, first declaration wins. (3) `exclusions` can manually remove transitive dependencies. `dependency:tree` shows the full tree. `dependency:analyze` finds unused/undeclared deps.

**Interviewer**: How does Gradle's dependency resolution differ?

**Candidate**: Gradle uses: (1) Conflict resolution — by default, newest version wins (not shortest path like Maven). (2) Rich versions — strict, required, preferred versions. (3) Dependency constraints — can force versions. (4) Module metadata — Gradle uses its own metadata format (`.module`) with better capability declarations. (5) `force = true` in Gradle overrides all.

**Interviewer**: What is a Bill of Materials (BOM)?

**Candidate**: A BOM is a `pom.xml` that uses `<dependencyManagement>` to declare dependency versions without actually including them. Downstream projects import it in `<dependencyManagement>`. This ensures consistent versions across a large project. Spring Boot provides `spring-boot-dependencies` BOM, and Jakarta EE provides a BOM.

**Interviewer**: How do you handle multi-module projects?

**Candidate**: Maven: parent `pom.xml` with `<modules>` sections. Parent defines common dependencies and plugins. Module projects reference the parent. `mvn install` builds from parent directory, building all modules. Gradle: `settings.gradle` lists included projects, root `build.gradle` configures subprojects via `allprojects` and `subprojects`.

**Interviewer**: How does the Gradle build cache work?

**Candidate**: Gradle's build cache stores task outputs (compiled classes, tests, JARs). If a task's inputs haven't changed, Gradle reuses cached output. Local cache (`~/.gradle/caches/`) speeds up repeated builds. Remote cache (shared via HTTP) speeds up CI — if CI built it, developers get the cached result. Cache keys are content-based hashes of inputs.

**Interviewer**: What is `provided` scope in Maven? When would you use it?

**Candidate**: `provided` scope means the dependency is needed to compile but is provided by the runtime environment (like a servlet container). It won't be included in the packaged WAR/JAR. Example: servlet API in a web app (provided by Tomcat). In Gradle: `compileOnly` is the equivalent.

**Interviewer**: Final: How would you migrate from Maven to Gradle?

**Candidate**: (1) Generate Gradle wrapper: `gradle wrapper`. (2) Translate `pom.xml` to `build.gradle` (plugins → plugins block, dependencies → dependencies block). (3) Use Gradle's `init` task for initial conversion (partial). (4) Configure multi-module in `settings.gradle`. (5) Run `gradle build` and compare output with Maven's. (6) Set up build cache. (7) Migrate CI pipeline. Common issues: annotation processors, code generation, test configuration differences.

---

## Feedback

**Strengths**:
- Good Maven vs Gradle comparison
- Lifecycle phase knowledge
- Dependency resolution for both tools
- BOM and multi-module understanding

**Areas for Improvement**:
- Could discuss Gradle Kotlin DSL advantages
- Mention Gradle configuration avoidance API

**Score**: 3.5/5 — Solid build tool knowledge
