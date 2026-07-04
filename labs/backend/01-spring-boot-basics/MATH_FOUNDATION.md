# Math Foundation

Spring Boot doesn't have heavy math, but understanding conditional logic and property resolution is valuable.

## Conditional Logic
Auto-configuration uses a decision tree:

```
For each auto-configuration class C:
  For each @Conditional on C:
    Evaluate condition(C) → boolean
  If ALL conditions true → register C's beans
```

This is equivalent to logical AND of all conditions:

```
C_activated = cond_1 ∧ cond_2 ∧ ... ∧ cond_n
```

## Property Resolution Order
Property sources are layered in a priority order (last wins in some implementations):

1. Default properties
2. @PropertySource on @Configuration classes
3. Application properties (application.yml)
4. Profile-specific properties (application-{profile}.yml)
5. OS environment variables
6. Command line arguments (`--server.port=8080`)

## Random Property Values
```java
@Value("${random.int(0,100)}")
private int randomNumber;
```
Uses `RandomValuePropertySource` with uniform distribution.
