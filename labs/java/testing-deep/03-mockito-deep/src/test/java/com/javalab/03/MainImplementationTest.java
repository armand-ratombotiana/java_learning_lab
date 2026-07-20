package com.javalab.03;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainImplementationTest {
    private MainImplementation store;
    @BeforeEach void setUp() { store = new MainImplementation(); }
    @Test @DisplayName("Put and get should work") void testPutGet() { assertNull(store.put("key", "value")); assertEquals("value", store.get("key")); }
    @Test @DisplayName("Remove should return old value") void testRemove() { store.put("k", "v"); assertEquals("v", store.remove("k")); assertNull(store.get("k")); }
    @Test @DisplayName("Size tracking") void testSize() { assertEquals(0, store.size()); store.put("a", "1"); assertEquals(1, store.size()); }
    @Test @DisplayName("Clear should remove all") void testClear() { store.put("a", "1"); store.put("b", "2"); store.clear(); assertEquals(0, store.size()); }
}
