package com.learning.backend13;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service demonstrating Spring Cache annotations.
 *
 * @Cacheable: caches method results — subsequent calls with the same
 *   arguments skip method execution and return cached value.
 * @CacheEvict: removes entries from the cache (before or after invocation).
 * @CachePut: always executes the method and updates the cache with the result.
 *   Useful for cache updates without interfering with method execution.
 *
 * Cache names are logical groupings. Spring's CacheManager manages the stores.
 */
@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    // Simulated database
    private final Map<String, Book> bookStore = new ConcurrentHashMap<>();

    public BookService() {
        bookStore.put("978-3-16-148410-0", new Book("978-3-16-148410-0", "The Great Gatsby", "F. Scott Fitzgerald", 12.99));
        bookStore.put("978-0-14-044913-6", new Book("978-0-14-044913-6", "1984", "George Orwell", 9.99));
        bookStore.put("978-0-06-112008-4", new Book("978-0-06-112008-4", "To Kill a Mockingbird", "Harper Lee", 14.99));
    }

    /**
     * Retrieves a book by ISBN. Results are cached in "books" cache.
     * The first call executes the method; subsequent calls with the same ISBN
     * return the cached result without executing.
     */
    @Cacheable(value = "books", key = "#isbn")
    public Book getBookByIsbn(String isbn) {
        log.info("Fetching book with ISBN {} from database (NOT from cache)", isbn);
        simulateSlowService();
        Book book = bookStore.get(isbn);
        if (book == null) {
            throw new BookNotFoundException("Book not found with ISBN: " + isbn);
        }
        return book;
    }

    /**
     * Updates a book. @CachePut always executes and updates the cache,
     * ensuring fresh data is cached after the update.
     */
    @CachePut(value = "books", key = "#isbn")
    public Book updateBook(String isbn, Book updated) {
        log.info("Updating book with ISBN {} in database and cache", isbn);
        if (!bookStore.containsKey(isbn)) {
            throw new BookNotFoundException("Cannot update — book not found with ISBN: " + isbn);
        }
        updated.setIsbn(isbn);
        bookStore.put(isbn, updated);
        return updated;
    }

    /**
     * Deletes a book. @CacheEvict removes the entry from "books" cache
     * so subsequent lookups fetch fresh data.
     */
    @CacheEvict(value = "books", key = "#isbn")
    public void deleteBook(String isbn) {
        log.info("Deleting book with ISBN {} from database and evicting from cache", isbn);
        bookStore.remove(isbn);
    }

    /**
     * Evicts all entries from the "books" cache.
     * allEntries = true clears the entire cache for the given value.
     */
    @CacheEvict(value = "books", allEntries = true)
    public void clearBookCache() {
        log.info("Clearing all entries from books cache");
    }

    private void simulateSlowService() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
