package com.capstone.ecommerce;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ProductCatalog {
    private final Map<String, Product> products = new ConcurrentHashMap<>();
    private final Map<String, List<String>> categoryIndex = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> tagIndex = new ConcurrentHashMap<>();

    public record Product(String id, String name, String description, BigDecimal price,
                          String category, List<String> tags, int stock, boolean active) {
        public Product {
            if (id == null || id.isBlank()) throw new IllegalArgumentException("Product ID required");
            if (price.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Price cannot be negative");
            tags = tags == null ? List.of() : List.copyOf(tags);
        }
    }

    public Product addProduct(Product p) {
        products.put(p.id(), p);
        categoryIndex.computeIfAbsent(p.category().toLowerCase(), k -> new CopyOnWriteArrayList<>()).add(p.id());
        for (String tag : p.tags()) {
            tagIndex.computeIfAbsent(tag.toLowerCase(), k -> ConcurrentHashMap.newKeySet()).add(p.id());
        }
        return p;
    }

    public Optional<Product> getProduct(String id) {
        return Optional.ofNullable(products.get(id));
    }

    public Product updateProduct(String id, Product p) {
        if (!products.containsKey(id)) throw new NoSuchElementException("Product not found: " + id);
        removeFromIndex(products.get(id));
        products.put(id, p);
        addToIndex(p);
        return p;
    }

    public boolean removeProduct(String id) {
        Product p = products.remove(id);
        if (p != null) {
            removeFromIndex(p);
            return true;
        }
        return false;
    }

    public List<Product> search(String query) {
        String q = query.toLowerCase();
        return products.values().stream()
            .filter(p -> p.name().toLowerCase().contains(q) || p.description().toLowerCase().contains(q))
            .sorted(Comparator.comparing(Product::name))
            .collect(Collectors.toList());
    }

    public List<Product> filterByCategory(String category) {
        return categoryIndex.getOrDefault(category.toLowerCase(), List.of()).stream()
            .map(products::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public List<Product> filterByTag(String tag) {
        return tagIndex.getOrDefault(tag.toLowerCase(), Set.of()).stream()
            .map(products::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public List<Product> filterByPriceRange(BigDecimal min, BigDecimal max) {
        return products.values().stream()
            .filter(p -> p.price().compareTo(min) >= 0 && p.price().compareTo(max) <= 0)
            .sorted(Comparator.comparing(Product::price))
            .collect(Collectors.toList());
    }

    public List<Product> listActive() {
        return products.values().stream().filter(Product::active).collect(Collectors.toList());
    }

    public int size() { return products.size(); }

    private void removeFromIndex(Product p) {
        List<String> catList = categoryIndex.get(p.category().toLowerCase());
        if (catList != null) catList.remove(p.id());
        for (String tag : p.tags()) {
            Set<String> tagSet = tagIndex.get(tag.toLowerCase());
            if (tagSet != null) tagSet.remove(p.id());
        }
    }

    private void addToIndex(Product p) {
        categoryIndex.computeIfAbsent(p.category().toLowerCase(), k -> new CopyOnWriteArrayList<>()).add(p.id());
        for (String tag : p.tags()) {
            tagIndex.computeIfAbsent(tag.toLowerCase(), k -> ConcurrentHashMap.newKeySet()).add(p.id());
        }
    }
}
