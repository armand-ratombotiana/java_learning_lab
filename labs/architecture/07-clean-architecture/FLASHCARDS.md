# Clean Architecture Flashcards

## Q: What is the dependency rule?
**A:** Source code dependencies can only point inward, toward the highest-level policies.

## Q: What is an entity?
**A:** An object containing enterprise-wide business rules that rarely changes.

## Q: What is a use case?
**A:** Application-specific business rules that orchestrate entity behavior.

## Q: What is an interface adapter?
**A:** Code that converts data between formats convenient for use cases and external systems.

## Q: What is a presenter?
**A:** An adapter that formats output data for the specific delivery mechanism.

## Q: What is a gateway?
**A:** An interface that the use case uses to access external systems.

## Q: What is screaming architecture?
**A:** Architecture where the package structure immediately reveals what the system does.

## Q: What is the difference between entity and use case?
**A:** Entities are enterprise-wide rules; use cases are application-specific rules that use entities.

## Q: What framework is typically in the outermost layer?
**A:** Frameworks like Spring, JPA, and database drivers.

## Q: What is ArchUnit?
**A:** A library for testing Java architecture rules, including Clean Architecture layering.
