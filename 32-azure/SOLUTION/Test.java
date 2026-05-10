package solution;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AzureSolutionTest {

    @Test
    void testBlobUpload() {
        AzureSolution solution = new AzureSolution("connection-string");
        assertNotNull(solution);
    }

    @Test
    void testBlobDownload() {
        AzureSolution solution = new AzureSolution("connection-string");
        assertNotNull(solution);
    }

    @Test
    void testCosmosDB() {
        AzureSolution solution = new AzureSolution("connection-string");
        assertNotNull(solution);
    }
}