package com.capstone.ecommerce;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ProductCatalogTest {
    private ProductCatalog catalog;
    private ProductCatalog.Product laptop;

    @BeforeEach
    void setUp() {
        catalog = new ProductCatalog();
        laptop = new ProductCatalog.Product("p1", "Laptop", "A powerful laptop",
            new BigDecimal("999.99"), "Electronics", List.of("tech", "portable"), 50, true);
    }

    @Test void testAddAndGetProduct() {
        catalog.addProduct(laptop);
        var found = catalog.getProduct("p1");
        assertTrue(found.isPresent());
        assertEquals("Laptop", found.get().name());
    }

    @Test void testSearch() {
        catalog.addProduct(laptop);
        catalog.addProduct(new ProductCatalog.Product("p2", "Mouse", "Wireless mouse",
            new BigDecimal("29.99"), "Electronics", List.of("tech"), 100, true));
        var results = catalog.search("laptop");
        assertEquals(1, results.size());
        results = catalog.search("tech");
        assertEquals(0, results.size());
    }

    @Test void testFilterByCategory() {
        catalog.addProduct(laptop);
        catalog.addProduct(new ProductCatalog.Product("p2", "Desk", "Wooden desk",
            new BigDecimal("299.99"), "Furniture", List.of(), 10, true));
        assertEquals(1, catalog.filterByCategory("Electronics").size());
        assertEquals(1, catalog.filterByCategory("Furniture").size());
    }

    @Test void testPriceRange() {
        catalog.addProduct(laptop);
        catalog.addProduct(new ProductCatalog.Product("p2", "Mouse", "Cheap mouse",
            new BigDecimal("9.99"), "Electronics", List.of(), 100, true));
        var results = catalog.filterByPriceRange(new BigDecimal("500"), new BigDecimal("1500"));
        assertEquals(1, results.size());
        assertEquals("Laptop", results.get(0).name());
    }

    @Test void testRemoveProduct() {
        catalog.addProduct(laptop);
        assertTrue(catalog.removeProduct("p1"));
        assertFalse(catalog.removeProduct("nonexistent"));
        assertFalse(catalog.getProduct("p1").isPresent());
    }

    @Test void testUpdateProduct() {
        catalog.addProduct(laptop);
        var updated = new ProductCatalog.Product("p1", "Gaming Laptop", "High-end gaming laptop",
            new BigDecimal("1499.99"), "Electronics", List.of("tech", "gaming"), 20, true);
        catalog.updateProduct("p1", updated);
        var found = catalog.getProduct("p1");
        assertTrue(found.isPresent());
        assertEquals("Gaming Laptop", found.get().name());
        assertThrows(NoSuchElementException.class, () -> catalog.updateProduct("nonexistent", updated));
    }

    @Test void testInvalidProduct() {
        assertThrows(IllegalArgumentException.class, () ->
            new ProductCatalog.Product("", "Invalid", "desc", BigDecimal.ONE, "Cat", List.of(), 1, true));
        assertThrows(IllegalArgumentException.class, () ->
            new ProductCatalog.Product("p", "Neg", "desc", new BigDecimal("-1"), "Cat", List.of(), 1, true));
    }
}
