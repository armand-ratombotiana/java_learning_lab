import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

class DeltaOptimizerTest {
    private final DeltaOptimizer opt = new DeltaOptimizer(256 * 1024 * 1024);

    @Test
    void testRequiredFiles() {
        assertEquals(4, opt.calculateRequiredFiles(1024L * 1024 * 1024));
    }

    @Test
    void testCompactionRatio() {
        double ratio = opt.estimateCompactionRatio(100, 1024L * 1024 * 1024);
        assertTrue(ratio > 20);
    }

    @Test
    void testZOrderRecommendation() {
        var cardinality = Map.of("id", 1_000_000, "status", 5, "date", 365);
        var filters = Set.of("id", "date", "status");
        String[] cols = opt.recommendZOrderColumns(cardinality, filters);
        assertTrue(cols.length > 0);
        assertEquals("id", cols[0]);
    }

    @Test
    void testOptimizePlan() {
        String plan = opt.generateOptimizePlan("/data/table", 1024L * 1024 * 1024, 1000);
        assertTrue(plan.contains("Optimize"));
        assertTrue(plan.contains("ratio"));
    }
}
