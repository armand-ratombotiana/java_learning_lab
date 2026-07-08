import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DagValidatorTest {
    @Test
    void testNoCycle() {
        DagValidator dv = new DagValidator();
        dv.addEdge("A", "B");
        dv.addEdge("B", "C");
        assertFalse(dv.hasCycle());
    }

    @Test
    void testHasCycle() {
        DagValidator dv = new DagValidator();
        dv.addEdge("A", "B");
        dv.addEdge("B", "C");
        dv.addEdge("C", "A");
        assertTrue(dv.hasCycle());
    }

    @Test
    void testTopologicalSort() {
        DagValidator dv = new DagValidator();
        dv.addEdge("A", "B");
        dv.addEdge("A", "C");
        dv.addEdge("B", "D");
        dv.addEdge("C", "D");
        var order = dv.topologicalSort();
        assertEquals(4, order.size());
        assertEquals("A", order.get(0));
        assertEquals("D", order.get(3));
    }

    @Test
    void testEstimateDuration() {
        DagValidator dv = new DagValidator();
        dv.addEdge("A", "B");
        dv.addEdge("B", "C");
        var durations = Map.of("A", 5, "B", 10, "C", 3);
        assertEquals(18, dv.estimateDuration(durations));
    }
}
