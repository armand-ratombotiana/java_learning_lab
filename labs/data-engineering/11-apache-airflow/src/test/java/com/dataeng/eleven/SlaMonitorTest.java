import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.*;

class SlaMonitorTest {
    @Test
    void testNoBreach() {
        SlaMonitor sm = new SlaMonitor();
        sm.registerSla(new SlaMonitor.SlaDefinition("dag1", "task1", Duration.ofMinutes(5), Duration.ZERO, "HIGH"));
        sm.checkDuration("dag1", "task1", Duration.ofMinutes(3));
        assertTrue(sm.getBreaches("dag1").isEmpty());
    }

    @Test
    void testBreachDetected() {
        SlaMonitor sm = new SlaMonitor();
        sm.registerSla(new SlaMonitor.SlaDefinition("dag1", "task1", Duration.ofMinutes(5), Duration.ZERO, "HIGH"));
        sm.checkDuration("dag1", "task1", Duration.ofMinutes(10));
        assertFalse(sm.getBreaches("dag1").isEmpty());
        assertEquals("duration", sm.getBreaches("dag1").get(0).metric());
    }

    @Test
    void testMultipleBreaches() {
        SlaMonitor sm = new SlaMonitor();
        sm.registerSla(new SlaMonitor.SlaDefinition("dag1", "t1", Duration.ofMinutes(5), Duration.ZERO, "HIGH"));
        sm.registerSla(new SlaMonitor.SlaDefinition("dag1", "t2", Duration.ofMinutes(3), Duration.ZERO, "HIGH"));
        sm.checkDuration("dag1", "t1", Duration.ofMinutes(10));
        sm.checkDuration("dag1", "t2", Duration.ofMinutes(10));
        assertEquals(2, sm.getBreaches("dag1").size());
    }
}
