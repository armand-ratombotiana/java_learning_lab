# Module 27: Spring REST API - Mini Project

**Project Name**: Secure Task Manager API  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Design a robust, production-ready RESTful API implementing best practices including proper HTTP status codes, Data Transfer Objects (DTOs), input validation, and global exception handling.

## 📝 Requirements

### Core Features

1. **Entities & DTOs**:
   - Create a `Task` entity with fields: `id` (Long), `title` (String), `description` (String), `completed` (boolean).
   - Create a `TaskCreateDto` used for incoming requests (it should not have an ID or completed status).
   - Create a `TaskResponseDto` used for outgoing responses.

2. **Input Validation**:
   - On the `TaskCreateDto`, use `jakarta.validation.constraints` to enforce:
     - `title` cannot be blank and must be between 3 and 50 characters.
     - `description` cannot be blank.

3. **REST Controller**:
   - Create a `TaskController` mapped to `/api/v1/tasks`.
   - Implement:
     - `GET /` -> Returns `ResponseEntity<List<TaskResponseDto>>` (Status: 200 OK).
     - `GET /{id}` -> Returns `ResponseEntity<TaskResponseDto>`. If not found, throws a custom `ResourceNotFoundException`.
     - `POST /` -> Accepts `@Valid @RequestBody TaskCreateDto`. Returns `ResponseEntity<TaskResponseDto>`. Add a `Location` header to the response pointing to the new resource. (Status: 201 Created).
     - `PUT /{id}` -> Updates an existing task. Returns 200 OK.
     - `DELETE /{id}` -> Deletes a task. Returns `ResponseEntity<Void>` (Status: 204 No Content).

4. **Global Exception Handling**:
   - Create a `@RestControllerAdvice` class.
   - Handle `ResourceNotFoundException` and return a standard JSON error payload with status `404 Not Found`.
   - Handle `MethodArgumentNotValidException` (triggered when validation fails) and return a custom JSON payload listing the specific fields that failed validation and their error messages, with status `400 Bad Request`.

---

## 💡 Solution Blueprint

1. **DTO & Validation**:
   ```java
   public class TaskCreateDto {
       @NotBlank(message = "Title is required")
       @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
       private String title;
       
       @NotBlank(message = "Description is required")
       private String description;
       // Getters & Setters
   }
   ```

2. **Controller (POST Example)**:
   ```java
   @PostMapping
   public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskCreateDto dto) {
       TaskResponseDto created = taskService.create(dto);
       URI location = ServletUriComponentsBuilder.fromCurrentRequest()
           .path("/{id}")
           .buildAndExpand(created.getId())
           .toUri();
       return ResponseEntity.created(location).body(created);
   }
   ```

3. **Global Exception Handler**:
   ```java
   @RestControllerAdvice
   public class GlobalExceptionHandler {

       @ExceptionHandler(ResourceNotFoundException.class)
       public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of("error", ex.getMessage()));
       }

       @ExceptionHandler(MethodArgumentNotValidException.class)
       public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
           Map<String, String> errors = new HashMap<>();
           ex.getBindingResult().getAllErrors().forEach((error) -> {
               String fieldName = ((FieldError) error).getField();
               String errorMessage = error.getDefaultMessage();
               errors.put(fieldName, errorMessage);
           });
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
       }
   }
   ```