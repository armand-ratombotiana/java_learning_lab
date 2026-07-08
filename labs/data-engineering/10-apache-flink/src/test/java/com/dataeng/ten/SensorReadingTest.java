import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SensorReadingTest {
    @Test
    void testConstructor() {
        SensorReading r = new SensorReading("s1", 25.5, 1000L, "celsius");
        assertEquals("s1", r.getSensorId());
        assertEquals(25.5, r.getValue());
        assertEquals(1000L, r.getTimestamp());
        assertEquals("celsius", r.getUnit());
    }

    @Test
    void testEquals() {
        SensorReading a = new SensorReading("s1", 25.5, 1000L, "celsius");
        SensorReading b = new SensorReading("s1", 25.5, 1000L, "celsius");
        assertEquals(a, b);
    }

    @Test
    void testNotEquals() {
        SensorReading a = new SensorReading("s1", 25.5, 1000L, "celsius");
        SensorReading b = new SensorReading("s2", 30.0, 2000L, "celsius");
        assertNotEquals(a, b);
    }

    @Test
    void testToString() {
        SensorReading r = new SensorReading("s1", 25.5, 1000L, "celsius");
        assertTrue(r.toString().contains("sensorId='s1'"));
        assertTrue(r.toString().contains("value=25.5"));
    }
}
