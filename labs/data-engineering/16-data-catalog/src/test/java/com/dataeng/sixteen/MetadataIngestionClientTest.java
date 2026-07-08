import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

class MetadataIngestionClientTest {
    @Test
    void testDatasetMetadataCreation() {
        var cols = List.of(
            new MetadataIngestionClient.ColumnMetadata("id", "BIGINT", "Primary key"),
            new MetadataIngestionClient.ColumnMetadata("name", "STRING", "Customer name"));
        var meta = new MetadataIngestionClient.DatasetMetadata("db", "public", "customers", cols, "Customer table", "ETL");
        assertEquals("db", meta.database());
        assertEquals("customers", meta.table());
        assertEquals(2, meta.columns().size());
    }

    @Test
    void testClientCreation() {
        var client = new MetadataIngestionClient("http://localhost:8585", "test-key");
        assertNotNull(client);
    }
}
