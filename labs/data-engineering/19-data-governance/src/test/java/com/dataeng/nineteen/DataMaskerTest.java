import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DataMaskerTest {
    private final DataMasker masker = new DataMasker();

    @Test
    void testMaskEmail() {
        String masked = masker.mask("john.doe@example.com", DataMasker.MaskingType.EMAIL);
        assertEquals("j***.doe@example.com", masked);
    }

    @Test
    void testMaskSSN() {
        String masked = masker.mask("123-45-6789", DataMasker.MaskingType.SSN);
        assertTrue(masked.contains("6789"));
        assertFalse(masked.contains("123"));
    }

    @Test
    void testMaskPhone() {
        String masked = masker.mask("555-123-4567", DataMasker.MaskingType.PHONE);
        assertEquals("***-***-4567", masked);
    }

    @Test
    void testMaskCreditCard() {
        String masked = masker.mask("1234-5678-9012-3456", DataMasker.MaskingType.CREDIT_CARD);
        assertEquals("****-****-****-3456", masked);
    }

    @Test
    void testMaskNull() {
        assertNull(masker.mask(null, DataMasker.MaskingType.GENERAL));
    }

    @Test
    void testMaskByRole() {
        String value = "user@example.com";
        assertEquals(value, masker.maskByRole(value, "admin", DataMasker.MaskingType.EMAIL));
        assertNotEquals(value, masker.maskByRole(value, "viewer", DataMasker.MaskingType.EMAIL));
    }
}
