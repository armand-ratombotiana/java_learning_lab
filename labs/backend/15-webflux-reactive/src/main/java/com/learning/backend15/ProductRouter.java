package com.learning.backend15;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * Functional routing configuration for Spring WebFlux.
 *
 * RouterFunction replaces @RequestMapping in the functional programming model.
 * Routes are composed using RouterFunctions.route() with predicates and handlers.
 *
 * This approach gives fine-grained control without relying on annotation magic.
 */
@Configuration
public class ProductRouter {

    private static final Logger log = LoggerFactory.getLogger(ProductRouter.class);

    @Bean
    public RouterFunction<ServerResponse> productRoutes(ProductHandler handler) {
        log.info("Registering product routes");

        return RouterFunctions.route()
            .GET("/api/products", handler::getAllProducts)
            .GET("/api/products/{id}", handler::getProductById)
            .POST("/api/products", handler::createProduct)
            .GET("/api/products/stream", handler::streamProducts)
            .build();
    }
}
