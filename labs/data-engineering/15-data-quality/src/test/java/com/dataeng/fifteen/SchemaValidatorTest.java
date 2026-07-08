import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

class SchemaValidatorTest {
    private final SchemaValidator validator = new SchemaValidator();

    @Test
    void testIdenticalSchemas() {
        var expected = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false));
        var actual = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false));
        var diff = validator.compare(expected, actual);
        assertFalse(diff.hasBreakingChanges());
        assertTrue(diff.newColumns().isEmpty());
        assertTrue(diff.missingColumns().isEmpty());
    }

    @Test
    void testNewColumn() {
        var expected = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false));
        var actual = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false),
            new SchemaValidator.ColumnDef("name", "STRING", true));
        var diff = validator.compare(expected, actual);
        assertFalse(diff.hasBreakingChanges());
        assertEquals(1, diff.newColumns().size());
    }

    @Test
    void testMissingColumn() {
        var expected = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false),
            new SchemaValidator.ColumnDef("name", "STRING", true));
        var actual = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false));
        var diff = validator.compare(expected, actual);
        assertTrue(diff.hasBreakingChanges());
        assertEquals(1, diff.missingColumns().size());
    }

    @Test
    void testTypeChange() {
        var expected = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false));
        var actual = List.of(new SchemaValidator.ColumnDef("id", "STRING", false));
        var diff = validator.compare(expected, actual);
        assertTrue(diff.hasBreakingChanges());
        assertEquals(1, diff.typeChanges().size());
    }

    @Test
    void testClassification() {
        var expected = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false));
        var actual = List.of(new SchemaValidator.ColumnDef("id", "BIGINT", false),
            new SchemaValidator.ColumnDef("name", "STRING", true));
        var diff = validator.compare(expected, actual);
        assertEquals("EVOLUTION", validator.classifyChange(diff));
    }
}
