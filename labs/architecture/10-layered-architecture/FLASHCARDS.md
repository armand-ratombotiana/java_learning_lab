# Layered Architecture Flashcards

## Q: What are the standard layers?
**A:** Presentation, Business, Persistence, and Cross-Cutting.

## Q: What is a layer violation?
**A:** When code in an upper layer directly depends on a lower layer that is not the adjacent layer.

## Q: What goes in the presentation layer?
**A:** Controllers, DTOs, request/response handling, validation.

## Q: What goes in the business layer?
**A:** Services, business logic, validation, transaction management.

## Q: What goes in the persistence layer?
**A:** Repositories, DAOs, entities, data access logic.

## Q: What is a DTO?
**A:** Data Transfer Object used to transfer data between layers without exposing internal entities.

## Q: What is N-Tier?
**A:** Physical separation of layers across different machines/servers.

## Q: What is N-Layer?
**A:** Logical separation of layers within the same application.

## Q: What is a cross-cutting concern?
**A:** Functionality that applies across all layers (logging, security, transactions).

## Q: What is the main limitation of layered architecture?
**A:** Can lead to "big ball of mud" without discipline and may couple to database design.
