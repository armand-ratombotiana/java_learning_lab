# Exercises: TestContainers

## Basic Exercises

1. **Container Setup**: Run TestContainers with PostgreSQL
   - Start a PostgreSQL container
   - Verify connection using JDBC

2. **Database Tests**: Write tests using test database
   - Test CRUD operations against container
   - Verify data persistence

3. **Service Testing**: Test external services
   - Use generic containers for Redis, MongoDB
   - Test service integration

4. **Container Lifecycle**: Manage container lifecycle
   - Use `@Container` annotation
   - Configure reuse options

5. **Network Configuration**: Set up networking
   - Configure network aliases
   - Test service-to-service communication

## Intermediate Exercises

6. **Custom Containers**: Create custom container configurations
   - Build images with Dockerfile
   - Initialize with test data

7. **Module Testing**: Test database modules
   - Run Liquibase/Flyway migrations
   - Verify schema creation

## Advanced Exercises

8. **Performance Testing**: Optimize container reuse
   - Configure Ryuk cleanup
   - Use Docker daemon caching