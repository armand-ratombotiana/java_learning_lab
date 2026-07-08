import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class BeamPipelineValidatorTest {
    @Test
    void testValidateNonEmpty() {
        assertDoesNotThrow(() -> {
            var opts = org.apache.beam.sdk.options.PipelineOptionsFactory.create();
            org.apache.beam.sdk.Pipeline p = org.apache.beam.sdk.Pipeline.create(opts);
            var input = p.apply("Create", org.apache.beam.sdk.values.PCollectionList.of(p)
                .apply(org.apache.beam.sdk.io.Create.of("hello", "", "world")));
            var result = BeamPipelineValidator.validateNonEmpty(input, "Test");
            assertNotNull(result);
        });
    }

    @Test
    void testPipelineCreation() {
        assertDoesNotThrow(() -> {
            var opts = org.apache.beam.sdk.options.PipelineOptionsFactory.create();
            assertNotNull(opts);
        });
    }
}
