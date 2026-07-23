package com.prod.solutions.slowquery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulates the N+1 query problem where a parent query is followed by
 * N child queries (one per parent row). This naive pattern causes
 * 1 + N round trips to the database instead of 1 batch query.
 *
 * BUG: For each author, we execute a separate query to load their books.
 * With 1000 authors, that's 1001 queries instead of 2.
 */
public class SlowQuerySimulator {

    static class Author {
        int id;
        String name;
        List<Book> books = new ArrayList<>();

        Author(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    static class Book {
        String title;

        Book(String title) { this.title = title; }
    }

    public static void main(String[] args) {
        System.out.println("=== N+1 Query Problem Demo ===");

        List<Author> authors = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            authors.add(new Author(i, "Author" + i));
        }

        long start = System.nanoTime();

        // BUG: N+1 queries — one query for authors, then one per author for books
        for (Author author : authors) {
            simulateFindBooksByAuthor(author); // 1 query per author
        }

        long nPlusOneTime = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("N+1 approach: %d queries, %d ms%n",
                authors.size() + 1, nPlusOneTime);

        // Fixed version: batch query
        start = System.nanoTime();
        simulateFindBooksForAllAuthors(authors); // 2 queries total
        long batchTime = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("Batch approach: 2 queries, %d ms%n", batchTime);
        System.out.printf("Speedup: %.1fx%n", (double) nPlusOneTime / batchTime);

        System.out.println("\nN+1 queries are the #1 cause of slow ORM-based applications.");

        // Print query counts
        System.out.printf("%nWith 1000 authors: N+1 = %d queries vs Batch = 2 queries%n",
                1000 + 1);
    }

    static void simulateFindBooksByAuthor(Author author) {
        // Simulate a database query (5ms per query)
        sleep(5);
        int bookCount = ThreadLocalRandom.current().nextInt(1, 5);
        for (int b = 0; b < bookCount; b++) {
            author.books.add(new Book(author.name + "-Book" + b));
        }
    }

    static void simulateFindBooksForAllAuthors(List<Author> authors) {
        // Simulate batch query — single round trip
        sleep(5); // First query: parent
        sleep(5); // Second query: all children via JOIN or IN clause
        for (Author author : authors) {
            for (int b = 0; b < 3; b++) {
                author.books.add(new Book(author.name + "-Book" + b));
            }
        }
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
