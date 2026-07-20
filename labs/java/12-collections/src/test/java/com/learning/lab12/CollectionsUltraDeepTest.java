package com.learning.lab12;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class CollectionsUltraDeepTest {

    @Test
    void hashMapNullKeyAllowed() {
        Map<String, String> map = new HashMap<>();
        map.put(null, "nullValue");
        assertEquals("nullValue", map.get(null));
    }

    @Test
    void treeMapNullKeyThrows() {
        Map<String, String> map = new TreeMap<>();
        assertThrows(NullPointerException.class, () -> map.put(null, "val"));
    }

    @Test
    void arrayListGrowthDynamics() {
        ArrayList<Integer> list = new ArrayList<>();
        int initialCapacity = 10;
        for (int i = 0; i < 11; i++) list.add(i);
        assertEquals(11, list.size());
    }

    @Test
    void setOperationsUnionIntersection() {
        Set<Integer> a = new HashSet<>(Set.of(1, 2, 3));
        Set<Integer> b = new HashSet<>(Set.of(3, 4, 5));
        Set<Integer> union = new HashSet<>(a);
        union.addAll(b);
        assertEquals(Set.of(1, 2, 3, 4, 5), union);
        Set<Integer> intersection = new HashSet<>(a);
        intersection.retainAll(b);
        assertEquals(Set.of(3), intersection);
    }

    @Test
    void concurrentModificationDetected() {
        List<String> list = new ArrayList<>(List.of("a", "b", "c"));
        assertThrows(ConcurrentModificationException.class, () -> {
            for (String s : list) {
                if (s.equals("b")) list.remove(s);
            }
        });
    }
}
