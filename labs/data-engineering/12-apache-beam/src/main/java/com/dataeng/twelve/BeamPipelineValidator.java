package com.dataeng.twelve;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

public class BeamPipelineValidator {

    public static PCollection<String> validateNonEmpty(PCollection<String> input, String name) {
        return input.apply(name, ParDo.of(new DoFn<String, String>() {
            @ProcessElement
            public void process(ProcessContext c) {
                if (c.element() != null && !c.element().isEmpty()) {
                    c.output(c.element());
                }
            }
        }));
    }

    public static PCollection<String> sample(PCollection<String> input, double fraction) {
        return input.apply("Sample", ParDo.of(new DoFn<String, String>() {
            @ProcessElement
            public void process(ProcessContext c) {
                if (Math.random() < fraction) c.output(c.element());
            }
        }));
    }

    public static PCollection<String> deduplicate(PCollection<String> input) {
        return input.apply("Dedup", ParDo.of(new DoFn<String, String>() {
            private final java.util.Set<String> seen = new java.util.HashSet<>();
            @ProcessElement
            public void process(ProcessContext c) {
                if (seen.add(c.element())) c.output(c.element());
            }
        }));
    }

    public static void main(String[] args) {
        var options = PipelineOptionsFactory.fromArgs(args).withValidation().create();
        Pipeline p = Pipeline.create(options);
        System.out.println("Pipeline created: " + p.getOptions().getAppName());
        p.run().waitUntilFinish();
    }
}
