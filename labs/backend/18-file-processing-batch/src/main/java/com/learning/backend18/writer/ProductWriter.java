package com.learning.backend18.writer;

import com.learning.backend18.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class ProductWriter implements ItemWriter<Product> {

    private static final Logger log = LoggerFactory.getLogger(ProductWriter.class);

    @Override
    public void write(Chunk<? extends Product> chunk) {
        log.info("Writing chunk of {} products", chunk.size());
        chunk.getItems().forEach(product ->
            System.out.println("Writing: " + product));
    }
}
