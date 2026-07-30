# Interview Questions — Clean Architecture

## Q1: What is the Dependency Rule?
**A:** Source code dependencies can only point inward. Nothing in an inner circle can know about something in an outer circle.

## Q2: How do you cross boundaries in Clean Architecture?
**A:** Use interfaces (boundaries) defined in the inner layer, implemented in the outer layer. The inner layer calls the interface; the outer layer provides the implementation.

## Q3: Clean Architecture vs Hexagonal — what's the difference?
**A:** Clean Architecture has multiple concentric layers (entities, use cases, adapters, frameworks) with explicit roles. Hexagonal has two sides (inbound/outbound) with ports and adapters. Both enforce dependency inversion. Clean is more prescriptive about layer responsibilities.

## Q4: Where do DTOs live?
**A:** At the interface adapter layer. They translate between outer-layer formats (JSON, DB rows) and inner-layer models (entities, request/response models).
