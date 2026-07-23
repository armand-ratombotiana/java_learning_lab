package design_patterns;

/**
 * Builder Pattern — separates construction of a complex object from its representation.
 * 
 * Variants:
 * 1. Classic Builder (GoF)
 * 2. Lombok @Builder
 * 3. Modern Java: records with builder
 * 
 * Time: O(1) per field set
 * Space: O(n) for the builder
 */
public class BuilderPattern {

    // Product
    static class Computer {
        private final String cpu;
        private final String ram;
        private final String storage;
        private final String gpu;
        private final boolean bluetooth;

        private Computer(Builder b) {
            this.cpu = b.cpu;
            this.ram = b.ram;
            this.storage = b.storage;
            this.gpu = b.gpu;
            this.bluetooth = b.bluetooth;
        }

        @Override
        public String toString() {
            return "Computer{cpu='" + cpu + "', ram='" + ram + "', storage='" + storage +
                   "', gpu='" + gpu + "', bluetooth=" + bluetooth + "}";
        }

        static class Builder {
            private String cpu;
            private String ram;
            private String storage;
            private String gpu;
            private boolean bluetooth;

            Builder cpu(String val) { this.cpu = val; return this; }
            Builder ram(String val) { this.ram = val; return this; }
            Builder storage(String val) { this.storage = val; return this; }
            Builder gpu(String val) { this.gpu = val; return this; }
            Builder bluetooth(boolean val) { this.bluetooth = val; return this; }
            Computer build() { return new Computer(this); }
        }
    }

    // Modern Java: record with builder
    record Book(String title, String author, String isbn, int year) {
        static Builder builder() { return new Builder(); }

        static class Builder {
            private String title;
            private String author;
            private String isbn;
            private int year;

            Builder title(String v) { this.title = v; return this; }
            Builder author(String v) { this.author = v; return this; }
            Builder isbn(String v) { this.isbn = v; return this; }
            Builder year(int v) { this.year = v; return this; }
            Book build() { return new Book(title, author, isbn, year); }
        }
    }

    public static void main(String[] args) {
        Computer pc = new Computer.Builder()
            .cpu("Intel i9")
            .ram("32GB")
            .storage("1TB SSD")
            .gpu("RTX 4080")
            .bluetooth(true)
            .build();
        System.out.println(pc);

        Book book = Book.builder()
            .title("Effective Java")
            .author("Joshua Bloch")
            .isbn("978-0134685991")
            .year(2018)
            .build();
        System.out.println(book);

        assert pc.toString().contains("Intel i9");
        assert book.title().equals("Effective Java");
        System.out.println("All BuilderPattern tests passed.");
    }
}