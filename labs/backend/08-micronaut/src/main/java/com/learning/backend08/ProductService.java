package com.learning.backend08;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Product service — a Micronaut @Singleton bean.
 *
 * @Singleton creates a single instance per application context.
 * @Bean (Micronaut) marks the class as a bean candidate for DI.
 * @Inject (Jakarta) can be used for field/setter injection.
 *
 * Micronaut compiles DI at build time, requiring no runtime reflection.
 */
@Singleton
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @PostConstruct
    public void init() {
        log.info("Initializing ProductService with sample products");
        save(new Product(null, "Laptop", 999.99));
        save(new Product(null, "Mouse", 29.99));
        save(new Product(null, "Keyboard", 89.99));
    }

    public List<Product> findAll() {
        return List.copyOf(products.values());
    }

    public Product findById(Long id) {
        Product p = products.get(id);
        if (p == null) throw new ProductNotFoundException("Product not found: " + id);
        return p;
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idGen.getAndIncrement());
        }
        products.put(product.getId(), product);
        log.info("Saved product: {}", product);
        return product;
    }
}
