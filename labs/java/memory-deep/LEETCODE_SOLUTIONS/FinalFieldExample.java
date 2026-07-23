package memory;

/**
 * Final field semantics — JMM guarantees for final fields.
 * 
 * JMM guarantee: A properly constructed object's final fields are visible
 * to all threads without synchronization.
 * 
 * How: The compiler ensures a StoreStore barrier after the constructor
 * completes (and before publishing the reference).
 * 
 * This is how String, Integer, and other immutable classes can be safely
 * published without synchronization.
 * 
 * Time: O(1)
 * Space: O(1)
 */
public class FinalFieldExample {

    static class Safe {
        final int x;
        final int y;
        static Safe instance;

        Safe() {
            x = 1;
            y = 2;
            // JMM ensures StoreStore barrier here
        }

        static void writer() {
            instance = new Safe();
        }

        static void reader() {
            Safe s = instance;
            if (s != null) {
                // Guaranteed to see x=1, y=2 because they are final
                assert s.x == 1 : "JMM guarantees final x";
                assert s.y == 2 : "JMM guarantees final y";
            }
        }
    }

    // Without final — no guarantee!
    static class Unsafe {
        int x = 1;
        int y = 2;
        static Unsafe instance;

        Unsafe() { }

        static void writer() {
            instance = new Unsafe();
        }

        static void reader() {
            Unsafe u = instance;
            if (u != null) {
                // Could see x=0, y=0 due to reordering!
                // No guarantee without synchronization
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // Demonstrate final field safety
        for (int i = 0; i < 10_000; i++) {
            Thread t1 = new Thread(Safe::writer);
            Thread t2 = new Thread(Safe::reader);
            t1.start(); t2.start();
            t1.join(); t2.join();
        }

        // Safe with record (all fields final)
        record Point(int x, int y) { }
        Point p = new Point(3, 4);
        assert p.x() == 3;
        assert p.y() == 4;

        System.out.println("All FinalFieldExample tests passed.");
    }
}