package com.learning.lab.module16.solution;

import java.util.*;
import java.time.LocalDateTime;

public class Solution {

    // REST Controller
    public static class RestController {
        private final String basePath;

        public RestController(String basePath) {
            this.basePath = basePath;
        }

        public String getBasePath() {
            return basePath;
        }
    }

    // Request Mapping
    public @interface GetMapping {
        String value() default "";
    }

    public @interface PostMapping {
        String value() default "";
    }

    public @interface PutMapping {
        String value() default "";
    }

    public @interface DeleteMapping {
        String value() default "";
    }

    public @interface PatchMapping {
        String value() default "";
    }

    // Request Parameters
    public @interface RequestParam {
        String value() default "";
        boolean required() default true;
    }

    public @interface PathVariable {
        String value() default "";
    }

    public @interface RequestBody {}

    public @interface RequestHeader {
        String value() default "";
    }

    public @interface CookieValue {
        String value() default "";
    }

    // DTO - Data Transfer Objects
    public static class UserDTO {
        private Long id;
        private String name;
        private String email;
        private int age;
        private LocalDateTime createdAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    public static class ProductDTO {
        private Long id;
        private String name;
        private String description;
        private double price;
        private int stock;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }
    }

    // Validation
    public @interface Valid {}

    public @interface NotNull {}
    public @interface NotEmpty {}
    public @interface NotBlank {}
    public @interface Size {
        int min() default 0;
        int max() default Integer.MAX_VALUE;
    }
    public @interface Email {}
    public @interface Min {
        double value() default 0;
    }
    public @interface Max {
        double value() default Double.MAX_VALUE;
    }
    public @interface Pattern {
        String regexp() default "";
    }

    public static class ValidationUtils {
        public static List<String> validate(Object obj) {
            List<String> errors = new ArrayList<>();
            return errors;
        }
    }

    // HTTP Response
    public static class ResponseEntity<T> {
        private final T body;
        private final int status;
        private final Map<String, String> headers;

        private ResponseEntity(T body, int status, Map<String, String> headers) {
            this.body = body;
            this.status = status;
            this.headers = headers;
        }

        public static <T> ResponseEntity<T> ok(T body) {
            return new ResponseEntity<>(body, 200, new HashMap<>());
        }

        public static <T> ResponseEntity<T> created(T body) {
            return new ResponseEntity<>(body, 201, new HashMap<>());
        }

        public static <T> ResponseEntity<T> noContent() {
            return new ResponseEntity<>(null, 204, new HashMap<>());
        }

        public static <T> ResponseEntity<T> badRequest(T body) {
            return new ResponseEntity<>(body, 400, new HashMap<>());
        }

        public static <T> ResponseEntity<T> notFound() {
            return new ResponseEntity<>(null, 404, new HashMap<>());
        }

        public static <T> ResponseEntity<T> status(int status) {
            return new ResponseEntity<>(null, status, new HashMap<>());
        }

        public T getBody() { return body; }
        public int getStatus() { return status; }
        public Map<String, String> getHeaders() { return headers; }
    }

    // HATEOAS - Links
    public static class Link {
        private final String href;
        private final String rel;
        private final String type;

        public Link(String href, String rel, String type) {
            this.href = href;
            this.rel = rel;
            this.type = type;
        }

        public String getHref() { return href; }
        public String getRel() { return rel; }
        public String getType() { return type; }

        public static Link of(String href, String rel) {
            return new Link(href, rel, null);
        }
    }

    public static class ResourceSupport {
        private final List<Link> links = new ArrayList<>();

        public void add(Link link) {
            links.add(link);
        }

        public List<Link> getLinks() {
            return links;
        }
    }

    public static class UserResource extends ResourceSupport {
        private Long id;
        private String name;
        private String email;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    // Paging and Sorting
    public static class Pageable {
        private final int page;
        private final int size;
        private final String sort;

        public Pageable(int page, int size, String sort) {
            this.page = page;
            this.size = size;
            this.sort = sort;
        }

        public int getPage() { return page; }
        public int getSize() { return size; }
        public String getSort() { return sort; }

        public static Pageable of(int page, int size) {
            return new Pageable(page, size, null);
        }
    }

    public static class Page<T> {
        private final List<T> content;
        private final int totalElements;
        private final int totalPages;
        private final int number;
        private final int size;

        public Page(List<T> content, int totalElements, int totalPages, int number, int size) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.number = number;
            this.size = size;
        }

        public List<T> getContent() { return content; }
        public int getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getNumber() { return number; }
        public int getSize() { return size; }
        public boolean hasNext() { return number < totalPages - 1; }
        public boolean hasPrevious() { return number > 0; }
    }

    // Content Negotiation
    public static class MediaType {
        public static final String APPLICATION_JSON = "application/json";
        public static final String APPLICATION_XML = "application/xml";
        public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    }

    // Exception Handling
    public static class ApiError {
        private final int status;
        private final String error;
        private final String message;
        private final String path;
        private final LocalDateTime timestamp;
        private final List<String> details;

        public ApiError(int status, String error, String message, String path, List<String> details) {
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
            this.timestamp = LocalDateTime.now();
            this.details = details;
        }

        public int getStatus() { return status; }
        public String getError() { return error; }
        public String getMessage() { return message; }
        public String getPath() { return path; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public List<String> getDetails() { return details; }
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }

    public static class GlobalExceptionHandler {
        public ApiError handleNotFound(ResourceNotFoundException ex, String path) {
            return new ApiError(404, "Not Found", ex.getMessage(), path, null);
        }

        public ApiError handleValidation(ValidationException ex, String path) {
            return new ApiError(400, "Bad Request", ex.getMessage(), path, null);
        }
    }

    // CORS
    public @interface CrossOrigin {
        String[] value() default {};
        String[] origins() default {};
        String[] methods() default {};
    }

    public static class CorsConfiguration {
        private List<String> allowedOrigins = new ArrayList<>();
        private List<String> allowedMethods = new ArrayList<>();
        private boolean allowCredentials = true;

        public void addAllowedOrigin(String origin) {
            allowedOrigins.add(origin);
        }

        public void addAllowedMethod(String method) {
            allowedMethods.add(method);
        }

        public void setAllowCredentials(boolean allow) {
            this.allowCredentials = allow;
        }

        public List<String> getAllowedOrigins() { return allowedOrigins; }
        public List<String> getAllowedMethods() { return allowedMethods; }
        public boolean isAllowCredentials() { return allowCredentials; }
    }

    // Versioning
    public @interface ApiVersion {
        String value() default "v1";
    }

    public static class VersioningStrategy {
        public enum Type {
            URI_PATH,
            HEADER,
            QUERY_PARAM
        }

        private Type type;

        public VersioningStrategy(Type type) {
            this.type = type;
        }

        public Type getType() { return type; }
    }

    // OpenAPI/Swagger
    public @interface ApiOperation {
        String value() default "";
        String notes() default "";
    }

    public @interface ApiResponse {
        String response() default "";
        String message() default "";
    }

    // Filter/Security
    public @interface PreAuthorize {
        String value() default "";
    }

    // Async REST
    public static class DeferredResult<T> {
        private T result;
        private boolean set = false;

        public void setResult(T result) {
            this.result = result;
            this.set = true;
        }

        public T getResult() { return result; }
        public boolean isSet() { return set; }
    }

    public static class Callable<T> {
        private final java.util.concurrent.Callable<T> callable;

        public Callable(java.util.concurrent.Callable<T> callable) {
            this.callable = callable;
        }

        public java.util.concurrent.Callable<T> getCallable() {
            return callable;
        }
    }

    public static void demonstrateRestAPI() {
        System.out.println("=== REST Controllers ===");
        RestController userController = new RestController("/api/users");
        System.out.println("Base path: " + userController.getBasePath());

        System.out.println("\n=== DTOs ===");
        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@test.com");
        user.setAge(30);
        user.setCreatedAt(LocalDateTime.now());
        System.out.println("User: " + user.getName());

        System.out.println("\n=== Response Entity ===");
        ResponseEntity<UserDTO> response = ResponseEntity.ok(user);
        System.out.println("Status: " + response.getStatus());

        System.out.println("\n=== HATEOAS Links ===");
        UserResource resource = new UserResource();
        resource.setId(1L);
        resource.setName("John");
        resource.setEmail("john@test.com");
        resource.add(Link.of("/api/users/1", "self"));
        resource.add(Link.of("/api/users", "users"));
        System.out.println("Links: " + resource.getLinks().size());

        System.out.println("\n=== Pagination ===");
        Pageable pageable = Pageable.of(0, 10);
        System.out.println("Page: " + pageable.getPage() + ", Size: " + pageable.getSize());

        List<UserDTO> users = List.of(new UserDTO(), new UserDTO());
        Page<UserDTO> page = new Page<>(users, 100, 10, 0, 10);
        System.out.println("Total pages: " + page.getTotalPages());

        System.out.println("\n=== Exception Handling ===");
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ApiError error = handler.handleNotFound(new ResourceNotFoundException("User not found"), "/api/users/1");
        System.out.println("Error: " + error.getStatus() + " - " + error.getMessage());

        System.out.println("\n=== CORS ===");
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedOrigin("http://localhost:3000");
        cors.addAllowedMethod("GET");
        cors.addAllowedMethod("POST");
        System.out.println("Allowed origins: " + cors.getAllowedOrigins());
    }

    public static void main(String[] args) {
        demonstrateRestAPI();
    }
}