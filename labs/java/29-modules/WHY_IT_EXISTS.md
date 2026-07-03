# Why Modules Exist

## The Problems Before JPMS
- **Classpath hell**: No explicit dependencies, version conflicts detected only at runtime
- **Weak encapsulation**: Public classes are accessible to everyone, preventing internal refactoring
- **No reliable configuration**: Missing dependencies cause ClassNotFoundException at runtime
- **JAR hell**: Multiple versions of the same library on classpath cause unpredictable behavior
- **Security**: Internal APIs (like com.sun.*) were accessible to application code
- **Startup performance**: Classpath scanning was slow

## The Solution
JPMS provides:
- **Reliable configuration**: Dependencies are declared and verified at compile time
- **Strong encapsulation**: Only exported packages are accessible
- **Module path**: Explicit module graph replaces the ad-hoc classpath
- **Service loading**: ServiceLoader with module declarations
- **Migration path**: Unnamed modules allow gradual migration
