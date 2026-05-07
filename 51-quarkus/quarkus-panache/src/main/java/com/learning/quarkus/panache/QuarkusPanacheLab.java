package com.learning.quarkus.panache;

public class QuarkusPanacheLab {

    public static void main(String[] args) {
        System.out.println("=== Quarkus Panache Lab ===\n");

        System.out.println("1. Panache Entity Example:");
        System.out.println("   @Entity");
        System.out.println("   class Book extends PanacheEntity {");
        System.out.println("       public String title;");
        System.out.println("       public String author;");
        System.out.println("       public int year;");
        System.out.println("   }");

        System.out.println("\n2. Panache Repository Methods:");
        System.out.println("   Book.listAll() - Get all books");
        System.out.println("   Book.findById(id) - Find by ID");
        System.out.println("   Book.find(\"title\", title).list() - Query by field");
        System.out.println("   book.persist() - Save entity");
        System.out.println("   book.delete() - Delete entity");

        System.out.println("\n3. REST Resource:");
        System.out.println("   @Path(\"/books\")");
        System.out.println("   public class BookResource {");
        System.out.println("       @GET public List<Book> getAll() { return Book.listAll(); }");
        System.out.println("       @POST public Response create(Book book) { book.persist(); return created; }");
        System.out.println("   }");

        System.out.println("\n=== Quarkus Panache Lab Complete ===");
    }
}