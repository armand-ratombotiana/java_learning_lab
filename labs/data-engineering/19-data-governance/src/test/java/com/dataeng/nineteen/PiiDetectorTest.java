import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

class PiiDetectorTest {
    private final PiiDetector detector = new PiiDetector();

    @Test
    void testDetectEmail() {
        var result = detector.analyzeColumn("email", List.of("user@example.com", "test@test.org"));
        assertNotEquals("UNKNOWN", result.piiType());
        assertTrue(result.confidence() > 0.5);
    }

    @Test
    void testDetectSSN() {
        var result = detector.analyzeColumn("ssn", List.of("123-45-6789", "987-65-4321"));
        assertEquals("SSN", result.piiType());
    }

    @Test
    void testDetectPhone() {
        var result = detector.analyzeColumn("phone", List.of("+1-555-123-4567", "555-123-4567"));
        assertEquals("PHONE", result.piiType());
    }

    @Test
    void testColumnNameDetection() {
        var result = detector.analyzeColumn("user_email_address", List.of());
        assertEquals("EMAIL", result.piiType());
    }

    @Test
    void testNoPii() {
        var result = detector.analyzeColumn("product_name", List.of("Widget", "Gadget"));
        assertEquals("UNKNOWN", result.piiType());
    }
}
