# DDD Flashcards

## Q: What is a bounded context?
**A:** A boundary around a domain model where specific terms and rules apply consistently.

## Q: What is an aggregate?
**A:** A cluster of domain objects treated as a single unit with a root entity ensuring consistency.

## Q: What is a value object?
**A:** An immutable object defined by its attributes, not identity. Two equal value objects are interchangeable.

## Q: What is an entity?
**A:** An object with a distinct identity that persists over time and across state changes.

## Q: What is a domain event?
**A:** A record of something that happened in the domain that domain experts care about.

## Q: What is ubiquitous language?
**A:** A shared, rigorous language used by both domain experts and developers in discussions, code, and documentation.

## Q: What is a repository?
**A:** A mechanism that provides collection-like access to aggregates, encapsulating persistence logic.

## Q: What is a domain service?
**A:** A stateless service that encapsulates domain logic that doesn't naturally fit in an entity or value object.

## Q: What is event storming?
**A:** A workshop technique for domain discovery using sticky notes to model domain events and processes.

## Q: What is specification pattern?
**A:** A pattern for encapsulating business rules into reusable, combinable predicate objects.
