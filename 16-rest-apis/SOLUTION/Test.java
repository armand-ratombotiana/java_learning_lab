package com.learning.lab.module16.solution;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

public class Test {

    @Test void testRestControllerBasePath() {
        Solution.RestController controller = new Solution.RestController("/api/users");
        assertEquals("/api/users", controller.getBasePath());
    }

    @Test void testUserDTOGettersSetters() {
        Solution.UserDTO user = new Solution.UserDTO();
        user.setId(1L); user.setName("John"); user.setEmail("john@test.com");
        user.setAge(30);
        assertEquals(1L, user.getId());
        assertEquals("John", user.getName());
    }

    @Test void testProductDTO() {
        Solution.ProductDTO product = new Solution.ProductDTO();
        product.setId(1L); product.setName("Widget");
        product.setDescription("A widget"); product.setPrice(9.99); product.setStock(100);
        assertEquals("Widget", product.getName());
        assertEquals(9.99, product.getPrice());
    }

    @Test void testResponseEntityOk() {
        Solution.ResponseEntity<String> response = Solution.ResponseEntity.ok("success");
        assertEquals(200, response.getStatus());
        assertEquals("success", response.getBody());
    }

    @Test void testResponseEntityCreated() {
        Solution.ResponseEntity<String> response = Solution.ResponseEntity.created("created");
        assertEquals(201, response.getStatus());
    }

    @Test void testResponseEntityNoContent() {
        Solution.ResponseEntity<Void> response = Solution.ResponseEntity.noContent();
        assertEquals(204, response.getStatus());
        assertNull(response.getBody());
    }

    @Test void testResponseEntityBadRequest() {
        Solution.ResponseEntity<String> response = Solution.ResponseEntity.badRequest("error");
        assertEquals(400, response.getStatus());
    }

    @Test void testResponseEntityNotFound() {
        Solution.ResponseEntity<Void> response = Solution.ResponseEntity.notFound();
        assertEquals(404, response.getStatus());
    }

    @Test void testHATEOASLinkCreation() {
        Solution.Link link = Solution.Link.of("/api/users/1", "self");
        assertEquals("/api/users/1", link.getHref());
        assertEquals("self", link.getRel());
    }

    @Test void testUserResourceLinks() {
        Solution.UserResource resource = new Solution.UserResource();
        resource.setId(1L); resource.setName("John");
        resource.add(Solution.Link.of("/api/users/1", "self"));
        resource.add(Solution.Link.of("/api/users", "users"));
        assertEquals(2, resource.getLinks().size());
    }

    @Test void testPageableCreation() {
        Solution.Pageable pageable = Solution.Pageable.of(0, 10);
        assertEquals(0, pageable.getPage());
        assertEquals(10, pageable.getSize());
    }

    @Test void testPageHasNext() {
        Solution.Page<String> page = new Solution.Page<>(List.of("a"), 20, 5, 0, 10);
        assertTrue(page.hasNext());
    }

    @Test void testPageHasPrevious() {
        Solution.Page<String> page = new Solution.Page<>(List.of("a"), 20, 5, 1, 10);
        assertTrue(page.hasPrevious());
    }

    @Test void testPageNoNextOnLastPage() {
        Solution.Page<String> page = new Solution.Page<>(List.of("a"), 20, 5, 4, 10);
        assertFalse(page.hasNext());
    }

    @Test void testApiErrorCreation() {
        Solution.ApiError error = new Solution.ApiError(404, "Not Found", "User not found", "/api/users/1", null);
        assertEquals(404, error.getStatus());
        assertEquals("Not Found", error.getError());
    }

    @Test void testResourceNotFoundException() {
        assertThrows(Solution.ResourceNotFoundException.class, () -> {
            throw new Solution.ResourceNotFoundException("User not found");
        });
    }

    @Test void testValidationException() {
        assertThrows(Solution.ValidationException.class, () -> {
            throw new Solution.ValidationException("Invalid data");
        });
    }

    @Test void testGlobalExceptionHandler() {
        Solution.GlobalExceptionHandler handler = new Solution.GlobalExceptionHandler();
        Solution.ApiError error = handler.handleNotFound(new Solution.ResourceNotFoundException("test"), "/api");
        assertEquals(404, error.getStatus());
    }

    @Test void testCorsConfiguration() {
        Solution.CorsConfiguration cors = new Solution.CorsConfiguration();
        cors.addAllowedOrigin("http://localhost:3000");
        cors.addAllowedMethod("GET");
        cors.addAllowedMethod("POST");
        cors.setAllowCredentials(true);
        assertTrue(cors.getAllowedOrigins().contains("http://localhost:3000"));
        assertTrue(cors.isAllowCredentials());
    }

    @Test void testDeferredResult() {
        Solution.DeferredResult<String> result = new Solution.DeferredResult<>();
        result.setResult("completed");
        assertTrue(result.isSet());
        assertEquals("completed", result.getResult());
    }

    @Test void testValidationUtils() {
        List<String> errors = Solution.ValidationUtils.validate(new Object());
        assertNotNull(errors);
    }

    @Test void testVersioningStrategyURI() {
        Solution.VersioningStrategy strategy = new Solution.VersioningStrategy(Solution.VersioningStrategy.Type.URI_PATH);
        assertEquals(Solution.VersioningStrategy.Type.URI_PATH, strategy.getType());
    }

    @Test void testVersioningStrategyHeader() {
        Solution.VersioningStrategy strategy = new Solution.VersioningStrategy(Solution.VersioningStrategy.Type.HEADER);
        assertEquals(Solution.VersioningStrategy.Type.HEADER, strategy.getType());
    }

    @Test void testResponseEntityStatus() {
        Solution.ResponseEntity<Void> response = Solution.ResponseEntity.status(500);
        assertEquals(500, response.getStatus());
    }

    @Test void testApiErrorTimestamp() {
        Solution.ApiError error = new Solution.ApiError(400, "Error", "msg", "/api", null);
        assertNotNull(error.getTimestamp());
    }

    @Test void testPageTotalElements() {
        Solution.Page<String> page = new Solution.Page<>(List.of("a"), 100, 10, 0, 10);
        assertEquals(100, page.getTotalElements());
    }

    @Test void testPageNumberAndSize() {
        Solution.Page<String> page = new Solution.Page<>(List.of("a"), 50, 5, 2, 10);
        assertEquals(2, page.getNumber());
        assertEquals(10, page.getSize());
    }

    @Test void testPageContent() {
        List<String> content = List.of("a", "b", "c");
        Solution.Page<String> page = new Solution.Page<>(content, 3, 1, 0, 10);
        assertEquals(3, page.getContent().size());
    }

    @Test void testProductDTOSetters() {
        Solution.ProductDTO product = new Solution.ProductDTO();
        product.setId(5L); product.setName("Test"); product.setPrice(10.0); product.setStock(50);
        assertNotNull(product);
    }

    @Test void testCorsMultipleOrigins() {
        Solution.CorsConfiguration cors = new Solution.CorsConfiguration();
        cors.addAllowedOrigin("http://localhost:3000");
        cors.addAllowedOrigin("http://localhost:8080");
        assertEquals(2, cors.getAllowedOrigins().size());
    }

    @Test void testCorsMultipleMethods() {
        Solution.CorsConfiguration cors = new Solution.CorsConfiguration();
        cors.addAllowedMethod("GET"); cors.addAllowedMethod("POST"); cors.addAllowedMethod("PUT"); cors.addAllowedMethod("DELETE");
        assertEquals(4, cors.getAllowedMethods().size());
    }

    @Test void testValidationExceptionMessage() {
        try {
            throw new Solution.ValidationException("Validation failed");
        } catch (Solution.ValidationException e) {
            assertEquals("Validation failed", e.getMessage());
        }
    }

    @Test void testResourceNotFoundExceptionMessage() {
        try {
            throw new Solution.ResourceNotFoundException("Entity not found");
        } catch (Solution.ResourceNotFoundException e) {
            assertEquals("Entity not found", e.getMessage());
        }
    }

    @Test void testPageTotalPages() {
        Solution.Page<String> page = new Solution.Page<>(List.of("a"), 95, 10, 0, 10);
        assertEquals(10, page.getTotalPages());
    }

    @Test void testDeferredResultInitiallyNotSet() {
        Solution.DeferredResult<String> result = new Solution.DeferredResult<>();
        assertFalse(result.isSet());
    }

    @Test void testPageEmptyContent() {
        Solution.Page<String> page = new Solution.Page<>(List.of(), 0, 0, 0, 10);
        assertTrue(page.getContent().isEmpty());
    }

    @Test void testResponseEntityHeaders() {
        Solution.ResponseEntity<String> response = Solution.ResponseEntity.ok("body");
        assertNotNull(response.getHeaders());
    }

    @Test void testLinkWithType() {
        Solution.Link link = new Solution.Link("/api/users/1", "self", "application/json");
        assertEquals("application/json", link.getType());
    }

    @Test void testGlobalExceptionHandlerValidation() {
        Solution.GlobalExceptionHandler handler = new Solution.GlobalExceptionHandler();
        Solution.ApiError error = handler.handleValidation(new Solution.ValidationException("invalid"), "/api/test");
        assertEquals(400, error.getStatus());
    }

    @Test void testMediaTypeConstants() {
        assertEquals("application/json", Solution.MediaType.APPLICATION_JSON);
        assertEquals("application/xml", Solution.MediaType.APPLICATION_XML);
    }

    @Test void testPageHasPreviousOnFirstPage() {
        Solution.Page<String> page = new Solution.Page<>(List.of("a"), 10, 1, 0, 10);
        assertFalse(page.hasPrevious());
    }

    @Test void testApiErrorDetails() {
        Solution.ApiError error = new Solution.ApiError(400, "Validation", "Invalid", "/api", List.of("field1", "field2"));
        assertEquals(2, error.getDetails().size());
    }

    @Test void testPageWithZeroTotalElements() {
        Solution.Page<String> page = new Solution.Page<>(List.of(), 0, 0, 0, 10);
        assertEquals(0, page.getTotalPages());
    }
}