import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class WordCountPipelineTest {
    @Test
    void testOptionsCreation() {
        var opts = org.apache.beam.sdk.options.PipelineOptionsFactory
            .fromArgs(new String[]{"--inputFile=in.txt", "--output=out.txt"})
            .as(WordCountPipeline.Options.class);
        assertEquals("in.txt", opts.getInputFile());
        assertEquals("out.txt", opts.getOutput());
    }

    @Test
    void testPipelineRuns() {
        assertDoesNotThrow(() -> {
            var opts = org.apache.beam.sdk.options.PipelineOptionsFactory.create();
            var p = org.apache.beam.sdk.Pipeline.create(opts);
            assertNotNull(p);
            p.run().waitUntilFinish();
        });
    }
}
