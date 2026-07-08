package com.learning.backend18.processor;

import com.learning.backend18.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class ProductProcessor implements ItemProcessor<Product, Product> {

    private static final Logger log = LoggerFactory.getLogger(ProductProcessor.class);

    @Override
    public Product process(Product item) {
        if (item.getName() == null || item.getName().isBlank()) {
            log.warn("Skipping product with empty name: {}", item.getId());
            return null;
        }
        item.setName(item.getName().toUpperCase());
        log.debug("Processed product: {}", item);
        return item;
    }
}
