package com.dataeng.twelve;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.*;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TypeDescriptors;

public class WordCountPipeline {

    public interface Options extends PipelineOptions {
        @Description("Input path") @Default.String("gs://data/input/*.txt")
        String getInputFile(); void setInputFile(String v);
        @Description("Output path") @Default.String("gs://data/output/counts")
        String getOutput(); void setOutput(String v);
    }

    public static void main(String[] args) {
        var opts = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);
        Pipeline p = Pipeline.create(opts);
        p.apply("Read", TextIO.read().from(opts.getInputFile()))
         .apply("Words", FlatMapElements.into(TypeDescriptors.strings())
             .via((String line) -> java.util.Arrays.asList(line.toLowerCase().split("[^a-z']+"))))
         .apply("Filter", Filter.by(w -> !w.isEmpty()))
         .apply("Count", Count.perElement())
         .apply("Format", MapElements.into(TypeDescriptors.strings())
             .via((KV<String, Long> kv) -> kv.getKey() + ": " + kv.getValue()))
         .apply("Write", TextIO.write().to(opts.getOutput()).withSuffix(".txt"));
        p.run().waitUntilFinish();
    }
}
