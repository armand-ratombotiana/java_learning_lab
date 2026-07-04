# Theory: API Documentation

## OpenAPI Specification
OpenAPI 3.1 is a standard, language-agnostic interface for REST APIs.

### Key Components
- **Info**: API title, version, description
- **Paths**: Endpoints and operations
- **Components**: Reusable schemas, parameters
- **Security**: Authentication schemes
- **Tags**: Grouping and organization

### SpringDoc OpenAPI
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

### Documentation Approaches
1. **SpringDoc**: Auto-generates from annotations
2. **Spring REST Docs**: Test-driven documentation
3. **Combined**: SpringDoc + REST Docs for best results

## Benefits
- Interactive API exploration (Swagger UI)
- Auto-generated client libraries
- API contract enforcement
- Developer portal integration
