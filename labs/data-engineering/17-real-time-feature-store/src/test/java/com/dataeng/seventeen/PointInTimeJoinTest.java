import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

class PointInTimeJoinTest {
    @Test
    void testSimpleJoin() {
        var labels = List.of(
            new PointInTimeJoin.Label("user1", 1000L, "click"));
        var features = List.of(
            new PointInTimeJoin.Feature("user1", 500L, "total_orders", 10));
        var result = PointInTimeJoin.execute(labels, features);
        assertEquals(1, result.size());
        assertEquals(10, result.get(0).get("total_orders"));
    }

    @Test
    void testFutureFeatureExcluded() {
        var labels = List.of(
            new PointInTimeJoin.Label("user1", 1000L, "click"));
        var features = List.of(
            new PointInTimeJoin.Feature("user1", 1500L, "total_orders", 20));
        var result = PointInTimeJoin.execute(labels, features);
        assertNull(result.get(0).get("total_orders"));
    }

    @Test
    void testLatestFeatureUsed() {
        var labels = List.of(
            new PointInTimeJoin.Label("user1", 1000L, "click"));
        var features = List.of(
            new PointInTimeJoin.Feature("user1", 300L, "total_orders", 5),
            new PointInTimeJoin.Feature("user1", 800L, "total_orders", 10));
        var result = PointInTimeJoin.execute(labels, features);
        assertEquals(10, result.get(0).get("total_orders"));
    }

    @Test
    void testLeakageDetection() {
        var labels = List.of(
            new PointInTimeJoin.Label("user1", 1000L, "click"));
        var features = List.of(
            new PointInTimeJoin.Feature("user1", 1500L, "future_feat", 99));
        assertThrows(IllegalStateException.class,
            () -> PointInTimeJoin.validateNoLeakage(labels, features));
    }
}
