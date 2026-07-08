import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

class QualityCheckEngineTest {
    private final QualityCheckEngine engine = new QualityCheckEngine((ds, r) -> {});

    @Test
    void testNotNullRule() {
        var rule = QualityCheckEngine.notNullRule("id", 5);
        var data = List.of(Map.of("id", 1), Map.of("id", 2), Map.of("id", null));
        var result = rule.evaluate(data);
        assertFalse(result.passed());
    }

    @Test
    void testUniqueRule() {
        var rule = QualityCheckEngine.uniqueRule("id", 5);
        var data = List.of(Map.of("id", 1), Map.of("id", 2), Map.of("id", 2));
        var result = rule.evaluate(data);
        assertFalse(result.passed());
    }

    @Test
    void testRangeRule() {
        var rule = QualityCheckEngine.rangeRule("age", 0, 120, 5);
        var data = List.of(Map.of("age", 25), Map.of("age", 30), Map.of("age", 200));
        var result = rule.evaluate(data);
        assertFalse(result.passed());
    }

    @Test
    void testAllPass() {
        var rule = QualityCheckEngine.notNullRule("name", 5);
        var data = List.of(Map.of("name", "Alice"), Map.of("name", "Bob"));
        var result = rule.evaluate(data);
        assertTrue(result.passed());
    }

    @Test
    void testReportScore() {
        engine.addRule(QualityCheckEngine.notNullRule("name", 5));
        var data = List.of(Map.of("name", "Alice"), Map.of("name", "Bob"));
        var report = engine.execute("test", data);
        assertTrue(report.allPassed());
        assertEquals(100.0, report.overallScore());
    }
}
