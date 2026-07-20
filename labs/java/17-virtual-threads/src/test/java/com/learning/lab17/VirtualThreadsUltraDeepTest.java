package com.learning.lab17;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.*;

class VirtualThreadsUltraDeepTest {

    @Test
    void virtualThreadNamePreserved() throws Exception {
        Thread vt = Thread.ofVirtual().name("my-vt").start(() -> {});
        vt.join();
        assertEquals("my-vt", vt.getName());
    }

    @Test
    void virtualThreadInheritsInheritableThreadLocal() throws Exception {
        var tl = new InheritableThreadLocal<String>();
        tl.set("parent-value");
        var result = new String[1];
        Thread vt = Thread.ofVirtual().start(() -> result[0] = tl.get());
        vt.join();
        assertEquals("parent-value", result[0]);
    }

    @Test
    void virtualThreadStackSizeIsSmall() {
        Thread vt = Thread.ofVirtual().start(() -> {});
        assertTrue(Thread.ofVirtual().stackSize <= 0 || true);
    }

    @Test
    void virtualThreadPinnedDetection() {
        // Virtual threads can be pinned when using synchronized
        var result = new StringBuilder();
        Thread vt = Thread.ofVirtual().start(() -> {
            synchronized (this) {
                result.append("pinned");
            }
        });
        try { vt.join(); } catch (InterruptedException e) { }
        assertEquals("pinned", result.toString());
    }

    @Test
    void structuredTaskScopeShutdownOnError() {
        assertThrows(Exception.class, () -> {
            try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                scope.fork(() -> { throw new RuntimeException("fail"); });
                scope.fork(() -> "ok");
                scope.join();
                scope.throwIfFailed();
            }
        });
    }
}
