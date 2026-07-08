import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class FeatureClientTest {
    @Test
    void testClientCreation() {
        var client = new FeatureClient("http://localhost:6566");
        assertNotNull(client);
    }

    @Test
    void testCacheEmptyInitially() {
        var client = new FeatureClient("http://localhost:6566");
        assertEquals(0, client.cacheSize());
    }

    @Test
    void testClearCache() {
        var client = new FeatureClient("http://localhost:6566");
        client.clearCache();
        assertEquals(0, client.cacheSize());
    }
}
