# Mental Models

## The "Spring Boot Onion"
1. **Outer layer**: Application code (your controllers, services)
2. **Auto-configured layer**: Spring Boot auto-wires beans based on classpath
3. **Starter layer**: Unified dependency descriptors
4. **Core layer**: Spring Framework (DI, AOP, MVC)
5. **Embedded container**: Tomcat/Jetty running inside your app

## The "Convention Over Configuration" Model
Spring Boot says: "Follow these conventions and everything just works."

- Source code in `src/main/java`
- Resources in `src/main/resources`
- Static content in `src/main/resources/static`
- Templates in `src/main/resources/templates`
- Main class in root package (component scanning starts here)

## The "Opinionated Defaults" Model
Spring Boot pre-configures sensible defaults but lets you override everything:

```
Starter added → Auto-configuration kicks in → Default beans created → You customize via properties
```
