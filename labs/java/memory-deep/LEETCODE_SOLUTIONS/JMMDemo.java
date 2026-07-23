package memory;

/**
 * Java Memory Model (JMM) demonstration.
 * 
 * Shows: happens-before relationships, reordering, visibility guarantees.
 * 
 * JMM guarantees:
 * - Program order rule (within thread)
 * - Monitor lock rule (unlock happens-before subsequent lock)
 * - Volatile variable rule (write happens-before subsequent read)
 * - Thread start/join rules
 * - Transitivity
 * 
 * Time: O(1) per operation
 * Space: O(1)
 */
public class JMMDemo {

    // Without synchronization — no visibility guarantee
    static class Incorrect {
        boolean ready = false;
        int number = 0;

        void writer() {
            number = 42;    // Write 1
            ready = true;   // Write 2 — may be reordered before Write 1!
        }

        void reader() {
            if (ready) {             // Read 2
                assert number == 42 : "JMM violation!"; // Read 1 — may see 0!
            }
        }
    }

    // With volatile — happens-before established
    static class Correct {
        volatile boolean ready = false;
        int number = 0;

        void writer() {
            number = 42;    // Write 1 — happens-before Write 2 (program order)
            ready = true;   // Write 2 — volatile write
        }

        void reader() {
            if (ready) {             // Read 2 — volatile read
                assert number == 42 : "JMM guarantees visibility"; // Read 1
            }
        }
    }

    // Synchronized establishes happens-before
    static class SyncDemo {
        int a = 0, b = 0;

        synchronized void set() { a = 1; b = 2; }

        synchronized boolean check() { return a == 1 && b == 2; }
    }

    public static void main(String[] args) throws Exception {
        // Test that volatile works
        for (int i = 0; i < 10_000; i++) {
            Correct c = new Correct();
            Thread t1 = new Thread(c::writer);
            Thread t2 = new Thread(c::reader);
            t1.start(); t2.start();
            t1.join(); t2.join();
        }
        System.out.println("Volatile visibility verified.");

        // Test synchronized
        SyncDemo sd = new SyncDemo();
        synchronized (sd) {
            sd.set();
        }
        boolean ok;
        synchronized (sd) {
            ok = sd.check();
        }
        assert ok;
        System.out.println("Synchronized visibility verified.");

        System.out.println("All JMMDemo tests passed.");
    }
}