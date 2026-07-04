# Layered Architecture Reference

## Layer Structure
```
src/main/java/com/company/app/
  controller/              # Presentation layer
    UserController.java
    ProductController.java
  service/                 # Business layer
    UserService.java
    ProductService.java
  repository/              # Persistence layer
    UserRepository.java
    ProductRepository.java
  entity/                  # Domain objects
    User.java
    Product.java
  dto/                     # Data transfer objects
    UserDto.java
    ProductDto.java
  config/                  # Configuration
    SecurityConfig.java
    WebConfig.java
  exception/               # Error handling
    GlobalExceptionHandler.java
    ResourceNotFoundException.java
  util/                    # Utilities
    DateUtils.java
    StringUtils.java
```

## Layer Dependency Rules
| Layer | Can Depend On | Cannot Depend On |
|-------|--------------|------------------|
| Controller | Service, DTO, Config | Repository, Entity |
| Service | Repository, Entity, DTO | Controller |
| Repository | Entity | Controller, Service |

## Technology Stack
| Layer | Technology |
|-------|-----------|
| Presentation | Spring MVC, REST, Jackson |
| Business | Spring @Service, @Transactional |
| Persistence | Spring Data JPA, Hibernate |
| Cross-Cutting | Spring AOP, Spring Security |
| Testing | JUnit, Mockito, Testcontainers |
