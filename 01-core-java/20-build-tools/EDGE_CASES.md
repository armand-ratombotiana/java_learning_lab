# Module 20: Build Tools (Maven & Gradle) - Edge Cases & Pitfalls

---

## Pitfall 1: Dependency Conflicts (Jar Hell)

### ❌ Wrong
Not managing transitive dependencies can lead to different versions of the same library being loaded at runtime, causing `NoSuchMethodError` or `ClassNotFoundException`.

### ✅ Correct
In Maven, use the `<dependencyManagement>` section in parent POMs or explicitly define `<exclusions>` to exclude conflicting transitive dependencies. In Gradle, use resolution strategies or `exclude group: '...', module: '...'`.

---

## Pitfall 2: Bypassing the Build Lifecycle

### ❌ Wrong
Creating custom scripts or complex ant-run plugins to perform standard tasks like compiling or packaging instead of using built-in phases or proper plugins.

### ✅ Correct
Bind plugins to specific lifecycle phases. If using Gradle, create custom tasks and define explicit `dependsOn` relationships to ensure the task graph is executed in the correct order.

---

## Pitfall 3: Caching Stale Artifacts

### ❌ Wrong
Relying on `-SNAPSHOT` versions in production environments or wondering why a newly deployed snapshot isn't being pulled down by developers.

### ✅ Correct
Use `-U` (`mvn clean install -U`) to force updates of snapshots in Maven. In Gradle, configure cache expiration for changing modules. Never use snapshot versions for released production artifacts; always use fixed release versions.