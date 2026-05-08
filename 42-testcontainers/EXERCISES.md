# Exercises - Testcontainers

## Exercise 1: Database Container
Test with real database:

1. Configure PostgreSQL container in tests
2. Run migrations on container startup
3. Write integration test for repository
4. Verify test isolation between classes

## Exercise 2: Multi-Container Setup
Test service integration:

1. Set up PostgreSQL + Redis containers
2. Configure application to use containers
3. Test caching layer with real Redis
4. Verify container networking

## Exercise 3: Custom Container
Test with custom image:

1. Create GenericContainer with custom image
2. Map ports for accessibility
3. Wait for readiness condition
4. Execute commands inside container

## Exercise 4: Docker Compose
Test with multiple services:

1. Define docker-compose.yml for test setup
2. Use docker-compose containers in tests
3. Verify all services are healthy
4. Test cross-service communication

## Exercise 5: Module Reuse
Optimize test performance:

1. Configure singleton container pattern
2. Share container across test methods
3. Implement container restart between classes
4. Measure and compare test times

## Bonus Challenge
Build a test base class that automatically starts PostgreSQL, MongoDB, and Kafka containers for any integration test. Include automatic migration and cleanup.