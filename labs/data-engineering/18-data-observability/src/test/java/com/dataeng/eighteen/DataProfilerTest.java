import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

class DataProfilerTest {
    private final DataProfiler profiler = new DataProfiler();

    @Test
    void testNumericProfile() {
        var values = List.of(1, 2, 3, 4, 5);
        var result = profiler.profileNumericColumn(values, "age");
        assertEquals(5, result.rowCount());
        assertEquals(3.0, result.mean());
        assertEquals(1.0, result.min());
        assertEquals(5.0, result.max());
    }

    @Test
    void testNullHandling() {
        var values = Arrays.asList(1, null, 3, null, 5);
        var result = profiler.profileNumericColumn(values, "age");
        assertEquals(5, result.rowCount());
        assertEquals(2, result.nullCount());
        assertEquals(0.4, result.nullRate());
    }

    @Test
    void testStringProfile() {
        var values = List.of("a", "b", "a", "c", "b", "a");
        var result = profiler.profileStringColumn(values, "category");
        assertEquals(3, result.distinctCount());
        assertTrue(result.topValues().containsKey("a"));
        assertTrue(result.topValues().get("a") >= 3);
    }
}
