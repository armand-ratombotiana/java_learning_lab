package com.db.multimodel;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Optional;

/**
 * Demonstrates using Redis as a cache layer alongside a primary database.
 *
 * This is the "C" in polyglot persistence — Redis provides
 * sub-millisecond reads to reduce load on the primary database.
 *
 * Patterns: cache-aside (lazy loading), write-through, TTL-based expiry.
 */
public class RedisCacheLayer {

    static final String REDIS_HOST = "localhost";
    static final int REDIS_PORT = 6379;
    static final long CACHE_TTL_SECONDS = 300; // 5 minutes

    static class Product {
        final long id;
        final String name;
        final double price;
        final String category;

        Product(long id, String name, double price, String category) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.category = category;
        }

        /** Simulates fetching from primary database (PostgreSQL/MongoDB). */
        static Optional<Product> findFromDatabase(long id) {
            // In real app: SELECT * FROM products WHERE id = ?
            if (id > 0 && id <= 100) {
                return Optional.of(new Product(id,
                    "Product " + id, Math.random() * 200 + 10, "Category " + (id % 5)));
            }
            return Optional.empty();
        }

        String serialize() {
            return "%d|%s|%.2f|%s".formatted(id, name, price, category);
        }

        static Product deserialize(String data) {
            String[] parts = data.split("\\|");
            return new Product(
                Long.parseLong(parts[0]), parts[1],
                Double.parseDouble(parts[2]), parts[3]);
        }
    }

    /**
     * Cache-Aside pattern:
     * 1. Check cache → return if found
     * 2. If not found, load from database
     * 3. Store in cache with TTL
     * 4. Return result
     */
    static Optional<Product> getProductWithCache(Jedis jedis, long productId) {
        String cacheKey = "product:" + productId;

        // Step 1: Check cache
        String cached = jedis.get(cacheKey);
        if (cached != null) {
            System.out.println("  CACHE HIT for product " + productId);
            return Optional.of(Product.deserialize(cached));
        }

        // Step 2: Load from database
        System.out.println("  CACHE MISS for product " + productId + " — loading from DB");
        Optional<Product> product = Product.findFromDatabase(productId);

        // Step 3: Store in cache
        product.ifPresent(p -> {
            jedis.setex(cacheKey, CACHE_TTL_SECONDS, p.serialize());
            System.out.println("  Cached product " + productId + " for " + CACHE_TTL_SECONDS + "s");
        });

        return product;
    }

    /**
     * Invalidates cache when product is updated (write-through / write-invalidate).
     */
    static void updateProduct(Jedis jedis, Product updatedProduct) {
        // In real app: UPDATE products SET ... WHERE id = ...
        System.out.println("  Updated product " + updatedProduct.id + " in database");

        // Invalidate cache
        jedis.del("product:" + updatedProduct.id);
        System.out.println("  Invalidated cache for product " + updatedProduct.id);
    }

    public static void main(String[] args) {
        try (JedisPool pool = new JedisPool(REDIS_HOST, REDIS_PORT);
             Jedis jedis = pool.getResource()) {

            System.out.println("=== Redis Cache Layer Demo ===\n");

            // First access — cache miss
            System.out.println("First access:");
            getProductWithCache(jedis, 42);

            // Second access — cache hit
            System.out.println("\nSecond access:");
            getProductWithCache(jedis, 42);

            // Different product — cache miss
            System.out.println("\nAnother product:");
            getProductWithCache(jedis, 17);

            // Update product → invalidate cache
            System.out.println("\nUpdate product 42:");
            updateProduct(jedis, new Product(42, "Updated Product", 99.99, "Updated"));

            // Access after update — cache miss, fresh data from DB
            System.out.println("\nAccess after update:");
            getProductWithCache(jedis, 42);

            System.out.println("\n=== Cache Statistics ===");
            System.out.println("  Total keys: " + jedis.dbSize());
            var info = jedis.info("stats");
            System.out.println("  Redis stats available via INFO command");

        } catch (Exception e) {
            System.out.println("Redis required: " + e.getMessage());
        }
    }
}
