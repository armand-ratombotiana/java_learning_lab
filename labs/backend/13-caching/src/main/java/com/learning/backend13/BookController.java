package com.learning.backend13;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing book endpoints that leverage caching.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBook(@PathVariable String isbn) {
        log.info("GET /api/books/{}", isbn);
        try {
            return ResponseEntity.ok(bookService.getBookByIsbn(isbn));
        } catch (BookNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{isbn}")
    public Book updateBook(@PathVariable String isbn, @RequestBody Book book) {
        log.info("PUT /api/books/{}", isbn);
        return bookService.updateBook(isbn, book);
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        log.info("DELETE /api/books/{}", isbn);
        bookService.deleteBook(isbn);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cache")
    public ResponseEntity<Void> clearCache() {
        log.info("DELETE /api/books/cache");
        bookService.clearBookCache();
        return ResponseEntity.noContent().build();
    }
}
