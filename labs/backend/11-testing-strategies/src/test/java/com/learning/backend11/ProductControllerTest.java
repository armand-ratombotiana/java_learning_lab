package com.learning.backend11;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web MVC slice test for ProductController.
 *
 * @WebMvcTest loads only the web layer (controllers, filters, etc.),
 * not the full application context. This makes tests faster.
 * @MockBean replaces ProductService with a mock in the context.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void getAllProducts_shouldReturnList() throws Exception {
        Product p1 = new Product("Laptop", 999.99, 10);
        Product p2 = new Product("Mouse", 29.99, 50);
        when(productService.getAllProducts()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void getProductById_shouldReturnProduct() throws Exception {
        Product product = new Product("Keyboard", 89.99, 20);
        product.setId(1L);
        when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Keyboard"));
    }

    @Test
    void createProduct_shouldReturn201() throws Exception {
        Product input = new Product("Monitor", 299.99, 15);
        Product saved = new Product("Monitor", 299.99, 15);
        saved.setId(1L);
        when(productService.createProduct(any(Product.class))).thenReturn(saved);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));
    }
}
