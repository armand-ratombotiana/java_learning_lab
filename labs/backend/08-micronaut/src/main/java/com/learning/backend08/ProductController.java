package com.learning.backend08;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.exceptions.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Micronaut @Controller — similar to Spring's @RestController.
 *
 * Key Micronaut annotations:
 * - @Controller: maps a class as a controller
 * - @Get: maps HTTP GET
 * - @Post: maps HTTP POST
 * - @Body: binds request body (like Spring's @RequestBody)
 * - @PathVariable: binds path variable
 */
@Controller("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    public List<Product> getAll() {
        log.info("GET /api/products");
        return productService.findAll();
    }

    @Get("/{id}")
    public Product getById(Long id) {
        log.info("GET /api/products/{}", id);
        return productService.findById(id);
    }

    @Post(consumes = MediaType.APPLICATION_JSON)
    public Product create(@Body Product product) {
        log.info("POST /api/products with body: {}", product);
        return productService.save(product);
    }
}
