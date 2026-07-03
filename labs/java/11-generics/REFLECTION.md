# Generics — Guided Self-Reflection

## Comprehension Check

1. Before this lab, how did you write type-safe code without generics? What patterns did you use?

2. Explain type erasure in your own words. What surprised you about how Java implements generics?

3. Describe a scenario where you would use `? extends T` versus `? super T`. Can you think of a real API you've used that demonstrates PECS?

## Code Review

Review a project you've worked on (or a well-known open-source project):
- How are generics used in the codebase?
- Are there any raw types? Unchecked warnings?
- Could any duplicate classes be refactored to use generics?
- Are wildcards used correctly (PECS)?

## Design Thinking

4. Imagine designing a caching library. How would generics make it type-safe? What wildcard types would the API expose?

5. If Java introduced reified generics (runtime type info), what would change? Would you prefer erased or reified generics? Why?

## Challenges Encountered

6. What part of generics was most confusing? Wildcards? Type erasure? Bounded parameters?

7. Did you encounter any issues with heap pollution or unchecked warnings in your practice? How did you resolve them?

## Application

8. How does your understanding of generics change how you read/write method signatures? Do you now notice when an API should use wildcards?

9. How would you teach PECS to another developer? What analogy would you use?

## Future Learning

10. What questions about generics do you still have? Topics to explore:
    - Generic type inference in depth
    - Valhalla project and specialized generics
    - Reflection with ParameterizedType
    - Generic DAO/Repository pattern in Spring
