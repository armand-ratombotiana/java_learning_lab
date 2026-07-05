package com.learning.lab06;

/**
 * Demonstrates various uses of the 'this' keyword.
 */
public class ThisKeywordExample {

    public static void showThisUsage() {
        System.out.println("=== 'this' Keyword ===");

        Book book = new Book("Effective Java", "Joshua Bloch");
        book.printInfo();
        book.setPages(416);
        System.out.println("Updated book: " + book);
    }
}

class Book {
    private String title;
    private String author;
    private int pages;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public Book setPages(int pages) {
        this.pages = pages;
        return this;
    }

    public void printInfo() {
        Book self = this;
        System.out.println("Book: " + self.title + " by " + self.author);
    }

    @Override
    public String toString() {
        return "Book{title='" + title + "', author='" + author + "', pages=" + pages + "}";
    }
}
