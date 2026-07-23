package design_patterns;

/**
 * Thread-safe Singleton patterns in Java.
 * 
 * Variants:
 * 1. Eager initialization (simple, always creates instance)
 * 2. Synchronized method (thread-safe, poor performance)
 * 3. Double-checked locking (DCL) with volatile
 * 4. Bill Pugh (Initialization-on-demand holder)
 * 5. Enum singleton (best — serialization-safe, reflection-safe)
 * 
 * Time: O(1) for getInstance()
 * Space: O(1)
 */
public class Singleton {

    // 1. Eager initialization
    static class SingletonEager {
        private static final SingletonEager INSTANCE = new SingletonEager();
        private SingletonEager() {}
        public static SingletonEager getInstance() { return INSTANCE; }
    }

    // 2. Synchronized method
    static class SingletonSync {
        private static SingletonSync instance;
        private SingletonSync() {}
        public static synchronized SingletonSync getInstance() {
            if (instance == null) instance = new SingletonSync();
            return instance;
        }
    }

    // 3. Double-checked locking (DCL) — requires volatile in Java 5+
    static class SingletonDCL {
        private static volatile SingletonDCL instance;
        private SingletonDCL() {}
        public static SingletonDCL getInstance() {
            if (instance == null) {
                synchronized (SingletonDCL.class) {
                    if (instance == null) instance = new SingletonDCL();
                }
            }
            return instance;
        }
    }

    // 4. Bill Pugh — Initialization-on-demand holder idiom
    static class SingletonHolder {
        private SingletonHolder() {}
        private static class Holder {
            private static final SingletonHolder INSTANCE = new SingletonHolder();
        }
        public static SingletonHolder getInstance() { return Holder.INSTANCE; }
    }

    // 5. Enum singleton (best practice)
    enum SingletonEnum {
        INSTANCE;
        public void doSomething() { System.out.println("Enum singleton"); }
    }

    public static void main(String[] args) {
        // Verify all return same instance
        SingletonEager e1 = SingletonEager.getInstance();
        SingletonEager e2 = SingletonEager.getInstance();
        assert e1 == e2 : "Eager singleton failed";

        SingletonSync s1 = SingletonSync.getInstance();
        SingletonSync s2 = SingletonSync.getInstance();
        assert s1 == s2 : "Sync singleton failed";

        SingletonDCL d1 = SingletonDCL.getInstance();
        SingletonDCL d2 = SingletonDCL.getInstance();
        assert d1 == d2 : "DCL singleton failed";

        SingletonHolder h1 = SingletonHolder.getInstance();
        SingletonHolder h2 = SingletonHolder.getInstance();
        assert h1 == h2 : "Holder singleton failed";

        SingletonEnum en1 = SingletonEnum.INSTANCE;
        SingletonEnum en2 = SingletonEnum.INSTANCE;
        assert en1 == en2 : "Enum singleton failed";

        // Multi-threaded verification
        java.util.concurrent.ExecutorService es = java.util.concurrent.Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            es.submit(() -> {
                SingletonDCL d = SingletonDCL.getInstance();
                assert d != null;
            });
        }
        es.shutdown();
        System.out.println("All Singleton tests passed.");
    }
}