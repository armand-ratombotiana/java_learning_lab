# Module 27: Spring REST API - Quizzes

---

## Q1: Controller Annotations
What is the difference between `@Controller` and `@RestController` in Spring?

A) `@Controller` can only return JSON, while `@RestController` can return HTML.
B) `@RestController` is a convenience annotation that combines `@Controller` and `@ResponseBody`.
C) They are exactly the same.
D) `@RestController` is used for database interactions.

**Answer**: B
**Explanation**: `@RestController` automatically applies `@ResponseBody` to all methods in the class, instructing Spring to serialize the return object directly to the HTTP response body (usually as JSON), rather than attempting to resolve a view template (like Thymeleaf).

---

## Q2: Path vs Query Parameters
Which annotation should be used to extract the value `42` from the URI `/api/users/42`?

A) `@RequestParam`
B) `@QueryParam`
C) `@PathVariable`
D) `@HeaderParam`

**Answer**: C
**Explanation**: `@PathVariable` is used to extract values from the URI path itself. `@RequestParam` is used to extract query parameters (e.g., `/api/users?id=42`).

---

## Q3: Global Exception Handling
Which annotation is used to create a class that handles exceptions globally across all controllers?

A) `@GlobalExceptionHandler`
B) `@ExceptionController`
C) `@RestControllerAdvice` (or `@ControllerAdvice`)
D) `@WebFilter`

**Answer**: C
**Explanation**: `@RestControllerAdvice` allows you to consolidate your `@ExceptionHandler` methods into a single, global component, ensuring consistent error responses across the entire REST API.