# Architecture: Enums in Java

## Enum Class Hierarchy
```
java.lang.Enum<E>
    └── Color (your enum)
            └── Color.RED (instance)
            └── Color.GREEN (instance)
            └── Color.BLUE (instance)

Enum<E> provides:
├── name()        → String
├── ordinal()     → int
├── compareTo(E)  → ordinal comparison
├── equals(Object) → reference equality
├── hashCode()    → identity hash code
├── toString()    → name (can override)
└── valueOf(Class, String) → lookup
```

## Enum Usage Patterns in Architecture

### Strategy Pattern with Enum
```java
public enum Compressor {
    GZIP {
        byte[] compress(byte[] data) { ... }
        byte[] decompress(byte[] data) { ... }
    },
    SNAPPY {
        byte[] compress(byte[] data) { ... }
        byte[] decompress(byte[] data) { ... }
    };
    abstract byte[] compress(byte[] data);
    abstract byte[] decompress(byte[] data);
}
```

### State Machine with Enum
```java
public enum OrderState {
    NEW {
        OrderState next() { return PROCESSING; }
    },
    PROCESSING {
        OrderState next() { return SHIPPED; }
    },
    SHIPPED {
        OrderState next() { return DELIVERED; }
    },
    DELIVERED {
        OrderState next() { return this; }  // Terminal state
    };
    abstract OrderState next();
}
```

### Enum with Database Mapping
```java
public enum UserRole {
    ADMIN(1),
    EDITOR(2),
    VIEWER(3);
    
    private final int dbId;
    
    UserRole(int dbId) { this.dbId = dbId; }
    
    public static UserRole fromDbId(int dbId) {
        for (UserRole role : values()) {
            if (role.dbId == dbId) return role;
        }
        throw new IllegalArgumentException("Unknown role: " + dbId);
    }
}
```

## When to Use Enums vs Alternatives

| Use Case | Solution | Reason |
|----------|----------|--------|
| Fixed set of constants | Enum | Type-safe, singleton, switch support |
| Dynamic set of values | String/Integer constants | Values change at runtime |
| Group related constants | Enum with fields | Fields, methods per constant |
| Bit flags | EnumSet | Efficient bit operations |
| Singleton | Enum INSTANCE | Serialization/reflection safe |
| State machine | Enum with methods | Polymorphic behavior per state |
| Numeric constants | int/long | Performance (no object overhead) |
