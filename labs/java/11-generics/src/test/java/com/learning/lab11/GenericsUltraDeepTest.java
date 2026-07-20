package com.learning.lab11;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class GenericsUltraDeepTest {

    @Test
    void rawTypeAssignmentWarning() {
        Box raw = new Box("raw");
        Box<String> typed = raw;
        assertEquals("raw", typed.get());
    }

    @Test
    void genericMethodTypeInference() {
        Integer result = GenericMethodExample.getFirst(new Integer[]{1, 2, 3});
        assertEquals(1, result);
    }

    @Test
    void wildcardUnboundedAcceptsAnyType() {
        List<?> list1 = List.of(1, 2, 3);
        List<?> list2 = List.of("a", "b");
        assertDoesNotThrow(() -> WildcardExample.printAll(list1));
        assertDoesNotThrow(() -> WildcardExample.printAll(list2));
    }

    @Test
    void boundedTypeParameterRestrictsToNumber() {
        List<Integer> ints = List.of(1, 2, 3);
        double sum = GenericMethodExample.average(ints);
        assertTrue(sum > 0);
    }

    @Test
    void comparableMaxWorksWithDifferentTypes() {
        assertEquals(3.14, GenericMethodExample.max(1.0, 3.14, 2.5));
        assertEquals("z", GenericMethodExample.max("a", "z", "m"));
    }
}
