package memory;

/**
 * Double-Checked Locking (DCL) Singleton.
 * 
 * Classic DCL anti-pattern is broken without volatile (pre-Java 5).
 * With the JSR-133 JMM (Java 5+), volatile fixes DCL.
 * 
 * The problem: Without volatile, a thread may see a partially constructed
 * object — the constructor may not have finished when the reference is
 * assigned to the static field (reordering allows this).
 * 
 * With volatile: happens-before guarantee prevents reordering of
 * constructor writes before the volatile write of the reference.
 * 
 * Alternative (preferred): Bill Pugh holder pattern or enum.
 * 
 * Time: O(1)
 * Space: O(1)
 */
public class DCLSingleton {

    private static volatile DCLSingleton instance;

    private final String name;

    private DCLSingleton() {
        this.name = "Singleton";
        // Simulate expensive init
        try { Thread.sleep(1); } catch (InterruptedException e) { }
    }

    public static DCLSingleton getInstance() {
        DCLSingleton result = instance;
        if (result == null) {
            synchronized (DCLSingleton.class) {
                result = instance;
                if (result == null) {
                    instance = result = new DCLSingleton();
                }
            }
        }
        return result;
    }

    public String getName() { return name; }

    // Broken DCL — without volatile (DO NOT USE)
    static class BrokenDCL {
        private static BrokenDCL instance;

        public static BrokenDCL getInstance() {
            if (instance == null) {
                synchronized (BrokenDCL.class) {
                    if (instance == null) {
                        instance = new BrokenDCL(); // May publish partially constructed object!
                    }
                }
            }
            return instance;
        }
    }

    public static void main(String[] args) throws Exception {
        // Thread-safe verification
        var ref = new java.util.concurrent.atomic.AtomicReference<DCLSingleton>();
        var threads = new java.util.ArrayList<Thread>();
        for (int i = 0; i < 100; i++) {
            threads.add(new Thread(() -> ref.set(DCLSingleton.getInstance())));
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();

        DCLSingleton s = ref.get();
        assert s != null;
        assert "Singleton".equals(s.getName());

        // Verify all threads return the same instance
        DCLSingleton first = DCLSingleton.getInstance();
        for (Thread t : threads) {
            assert DCLSingleton.getInstance() == first : "All must be same instance";
        }

        System.out.println("All DCLSingleton tests passed.");
    }
}