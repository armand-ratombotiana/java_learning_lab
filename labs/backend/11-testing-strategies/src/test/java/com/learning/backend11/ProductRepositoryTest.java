package com.learning.backend11;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Data JPA slice test for ProductRepository.
 *
 * @DataJpaTest configures an in-memory database, scans JPA entities,
 * and enables JPA repositories. Transactional by default (rollback after each test).
 * TestEntityManager provides a subset of EntityManager methods for tests.
 */
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByNameContainingIgnoreCase_shouldReturnMatchingProducts() {
        entityManager.persist(new Product("Laptop", 999.99, 10));
        entityManager.persist(new Product("Mouse Pad", 14.99, 100));
        entityManager.persist(new Product("Monitor", 299.99, 5));
        entityManager.flush();

        List<Product> result = productRepository.findByNameContainingIgnoreCase("mouse");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualToIgnoringCase("Mouse Pad");
    }

    @Test
    void findByPriceLessThanEqual_shouldReturnFilteredProducts() {
        entityManager.persist(new Product("Cheap Item", 5.99, 10));
        entityManager.persist(new Product("Mid Item", 50.00, 20));
        entityManager.persist(new Product("Expensive Item", 500.00, 2));
        entityManager.flush();

        List<Product> result = productRepository.findByPriceLessThanEqual(50.00);

        assertThat(result).hasSize(2);
    }
}
