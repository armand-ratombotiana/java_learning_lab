# Code Deep Dive: Apache Beam

See Java source files in src/main/java/com/dataeng/twelve/ for complete implementations:

- WordCountPipeline.java: Complete batch pipeline with Beam options
- StreamingPipeline.java: Kafka streaming with windowing and triggers
- BeamPipelineValidator.java: Utility transforms for validation

Key patterns:
```java
Pipeline p = Pipeline.create(options);
p.apply("Read", TextIO.read().from(input))
 .apply("Words", FlatMapElements.via((String line) -> ...))
 .apply("Count", Count.perElement())
 .apply("Write", TextIO.write().to(output));
p.run().waitUntilFinish();

// Streaming
p.apply(KafkaIO.read().withTopic("events"))
 .apply(Window.into(FixedWindows.of(5, MINUTES))
     .triggering(AfterWatermark.pastEndOfWindow()
         .withEarlyFirings(AfterProcessingTime.pastFirstElementInPane())))
 .apply(Count.perKey());
```
