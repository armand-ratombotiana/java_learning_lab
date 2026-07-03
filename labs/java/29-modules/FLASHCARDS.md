# Flashcards: Modules

## Card 1: module-info.java
Declares module name, dependencies (requires), exports, opens, services.

## Card 2: exports
Makes packages visible to other modules.

## Card 3: requires
Declares module dependencies. Verified at compile and startup time.

## Card 4: requires transitive
Re-exports dependency to consumers. Used when the dependency's types appear in exports.

## Card 5: opens
Opens package for reflection. Needed by frameworks (Hibernate, Spring, Jackson).

## Card 6: provides/uses
Service provider declaration and consumption.

## Card 7: jlink
Creates custom runtime images with only required modules.

## Card 8: Module Path
Replaces classpath for modular apps. Dependencies verified at startup.

## Card 9: Automatic Module
Non-modular JAR on module path becomes an automatic module (reads all modules).

## Card 10: Split Packages
Two modules cannot define classes in the same package.
