# Mini-Project: Custom Actuator Dashboard

Build a web dashboard that displays Spring Boot actuator information for multiple services.

## Requirements

1. Create a Spring Boot application with a custom MVC controller that:
   - Fetches `/actuator/health` from multiple configured services
   - Fetches `/actuator/metrics` for key metrics (jvm.memory.used, system.cpu.usage)
   - Displays a HTML dashboard
2. Register custom metrics:
   - Custom counter for page views
   - Custom gauge for database pool size
3. Register a custom endpoint `/actuator/service-status` that aggregates status

## Tech Stack

- Spring Boot 3.x
- Thymeleaf for HTML dashboard
- Spring Boot Actuator
- RestTemplate or WebClient for service calls

## Deliverables

- Dashboard showing: service name, health status, memory usage, uptime
- Auto-refresh every 10 seconds
- Custom health indicator for the dashboard service itself