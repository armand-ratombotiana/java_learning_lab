# History of Java Annotations

## Evolution Timeline

### Java 1.4 (2002) - The Beginning
- Introduced basic annotations (`@Deprecated`, `@Override`, `@SuppressWarnings`)
- Limited but provided foundation for metadata in Java

### Java 5 (2004) - Major Overhaul
- Full annotation support added to the language
- Introduced meta-annotations (`@Target`, `@Retention`, `@Documented`, `@Inherited`)
- Created `java.lang.annotation` package
- Enabled custom annotation creation

### Java 6 (2006) - Enhancements
- Pluggable annotation processing API (JSR 269)
- Enabled compile-time annotation processors
- Support for meta-annotations on annotation types

### Java 8 (2014) - Repeatable Annotations
- Added `@Repeatable` meta-annotation
- Allowed the same annotation to be applied multiple times to same element
- Introduced type annotations (type-use)

### Java 9 (2017) - Module Integration
- Improved annotation processing in modular systems
- Better handling of annotations in module-info.java

### Java 14+ (2020+) - Records and Sealed Classes
- Annotations enhanced for new language features
- Better inference for type annotations

## Framework Annotation Evolution

| Period | Framework | Notable Annotations |
|--------|-----------|---------------------|
| 2006 | Spring 2.0 | @Transactional, @Required |
| 2006 | JPA 1.0 | @Entity, @Table, @Column |
| 2008 | JUnit 4 | @Test, @Before, @After |
| 2013 | Spring 4.0 | @RestController, @GetMapping |
| 2017 | JUnit 5 | @DisplayName, @Nested |
| 2019 | Jakarta EE | Migration from javax to jakarta |

## Key Milestones

1. **JSR 175** - Specification for annotations
2. **JSR 269** - Pluggable annotation processing API
3. **JEP 119** - Implementation of JSR-269
4. **JEP 113** - Type annotations specification