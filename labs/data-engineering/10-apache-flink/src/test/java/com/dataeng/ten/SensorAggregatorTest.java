import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SensorAggregatorTest {
    private final SensorAggregator agg = new SensorAggregator();

    @Test
    void testAccumulatorCreation() {
        SensorAggregator.Accumulator acc = agg.createAccumulator();
        assertEquals(0, acc.count);
        assertEquals(0.0, acc.sum);
    }

    @Test
    void testAdd() {
        SensorAggregator.Accumulator acc = agg.createAccumulator();
        agg.add(new SensorReading("s1", 10.0, 1L, "celsius"), acc);
        agg.add(new SensorReading("s1", 20.0, 2L, "celsius"), acc);
        assertEquals(2, acc.count);
        assertEquals(30.0, acc.sum);
        assertEquals(20.0, acc.max);
    }

    @Test
    void testGetResult() {
        SensorAggregator.Accumulator acc = agg.createAccumulator();
        agg.add(new SensorReading("s1", 10.0, 1L, "celsius"), acc);
        agg.add(new SensorReading("s1", 20.0, 2L, "celsius"), acc);
        AggregateResult result = agg.getResult(acc);
        assertEquals(15.0, result.getAvgValue());
        assertEquals(20.0, result.getMaxValue());
        assertEquals(2, result.getCount());
    }
}
