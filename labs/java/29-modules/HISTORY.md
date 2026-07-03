# History of Modules

## Pre-JPMS Era
- **2005**: JSR 277 (Java Module System) started, later superseded
- **2008**: Project Jigsaw announced by Mark Reinhold
- **2011**: Jigsaw postponed from Java 7 to Java 8, then Java 9
- **2015**: Jigsaw proposed as JSR 376
- **Summer 2017**: Multiple release delays

## Java 9 (September 2017)
- JEP 261: Module System
- JEP 200: Modular JDK
- JDK modules defined (java.base, java.sql, java.xml, etc.)
- java.corba and java.xml.ws deprecated for removal
- module-info.java support

## Java 9+ Evolution
- Java 9: Initial release with module system
- Java 11: CORBA and JavaFX modules removed
- Java 16: Strong encapsulation of JDK internals (still breakable with --add-opens)
- Java 17: Strong encapsulation enforced by default
- Ongoing: More tools (jlink, jmod, jdeps) mature

## Key Milestones
- **JDK modules**: The JDK itself is modularized (over 70 modules)
- **java.base**: Always required, contains core language features
- **Split packages**: Two modules cannot define the same package
- **Automatic modules**: Libraries on classpath become automatic modules
