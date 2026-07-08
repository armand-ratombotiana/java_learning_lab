# Refactoring - Multi-Cloud

## Current State Issues

### 1. God Class Pattern
Main service class has grown too large.

**Solution**: Apply Single Responsibility Principle:
- Extract configuration to separate class
- Extract metrics to dedicated reporter
- Extract business logic to handlers

### 2. Duplicate Code
Similar logic appears in multiple places.

**Solution**: Extract common utilities:
`java
public final class ValidationUtils {
    public static void requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(message);
        }
    }
}
`

### 3. Tight Coupling
Direct instantiation makes testing difficult.

**Solution**: Use dependency injection:
`java
// Before
public class Service { private Database db = new Database(); }

// After
public class Service {
    private final Database db;
    public Service(Database db) { this.db = db; }
}
`

## Migration Strategy
1. Write characterization tests to lock behavior
2. Extract one responsibility at a time
3. Run full test suite after each extraction
4. Remove deprecated code after migration
5. Document changes in CHANGELOG

## Target State
- Clean separation of concerns
- All dependencies injectable
- Test coverage > 80%
- Consistent error handling
- Structured logging throughout
