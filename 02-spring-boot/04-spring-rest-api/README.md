# Spring REST API

Build RESTful APIs with Spring Web.

## Overview

- REST controller implementation
- CRUD operations
- Request/response handling
- Error handling
- JSON serialization

## Key Concepts

- **@RestController** - REST endpoint
- **@RequestMapping** - URL mapping
- **@GetMapping, @PostMapping** - HTTP methods
- **@RequestBody** - JSON to object
- **ResponseEntity** - HTTP response

## Running

```bash
mvn spring-boot:run
```

## Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/resources | List all |
| GET | /api/resources/{id} | Get by ID |
| POST | /api/resources | Create |
| PUT | /api/resources/{id} | Update |
| DELETE | /api/resources/{id} | Delete |

## Dependencies

- spring-boot-starter-web

## HTTP Status Codes

- 200 OK - Success
- 201 Created - Resource created
- 400 Bad Request - Validation error
- 404 Not Found - Resource missing
- 500 Internal Error - Server error

## Version

Spring Boot 3.3.0