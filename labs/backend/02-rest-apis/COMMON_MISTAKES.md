# Common Mistakes in REST APIs

## 1. Returning Domain Entities Directly
```java
// WRONG: Exposes internal structure, circular references, security issues
@GetMapping("/user/{id}")
public User getUser(@PathVariable Long id) { ... }

// CORRECT: Use DTOs
@GetMapping("/user/{id}")
public UserDTO getUser(@PathVariable Long id) { ... }
```

## 2. Wrong HTTP Methods
```java
// WRONG: Using GET for deletion
@GetMapping("/deleteUser/{id}")

// CORRECT: Use DELETE
@DeleteMapping("/{id}")
```

## 3. Ignoring Status Codes
```java
// WRONG: Always 200
return ResponseEntity.ok(null);

// CORRECT: Use appropriate codes
return ResponseEntity.noContent().build(); // 204
return ResponseEntity.notFound().build();  // 404
```

## 4. Not Validating Input
```java
// WRONG: Missing @Valid
@PostMapping
public User createUser(@RequestBody User user) { ... }

// CORRECT: With validation
@PostMapping
public User createUser(@Valid @RequestBody User user) { ... }
```

## 5. Inconsistent Error Format
Always use a consistent ErrorResponse structure across all endpoints.

## 6. Exposing Stack Traces
```java
// WRONG: Internal details leaked
return ResponseEntity.status(500).body(ex.getStackTrace());

// CORRECT: Generic message
return ResponseEntity.status(500).body("An internal error occurred");
```
