package com.learning.backend11;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test for ProductService using Mockito.
 *
 * @ExtendWith(MockitoExtension.class) enables Mockito annotations.
 * @Mock creates a mock instance of ProductRepository.
 * We inject the mock into ProductService manually.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        // Arrange
        Product p1 = new Product("Laptop", 999.99, 10);
        Product p2 = new Product("Mouse", 29.99, 50);
        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getName).contains("Laptop", "Mouse");
        verify(productRepository).findAll();
    }

    @Test
    void getProductById_whenFound_shouldReturnProduct() {
        Product product = new Product("Keyboard", 89.99, 20);
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertThat(result.getName()).isEqualTo("Keyboard");
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_whenNotFound_shouldThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
            .isInstanceOf(ProductNotFoundException.class)
            .hasMessageContaining("Product not found");
    }

    @Test
    void createProduct_shouldSaveAndReturnProduct() {
        Product input = new Product("Monitor", 299.99, 15);
        Product saved = new Product("Monitor", 299.99, 15);
        saved.setId(1L);
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        Product result = productService.createProduct(input);

        assertThat(result.getId()).isEqualTo(1L);
        verify(productRepository).save(input);
    }
}
