# Flashcards: Apache Beam

## Card 1
**Front**: What is Apache Beam?
**Back**: Unified programming model for batch and streaming pipelines, portable across runners

## Card 2
**Front**: What is a PTransform?
**Back**: An operation that transforms one or more PCollections into new PCollections

## Card 3
**Front**: What is Beam's portability architecture?
**Back**: SDK Harness manages user code execution; Fn API communicates with runner via protocol buffers

## Card 4
**Front**: What is windowing?
**Back**: Grouping unbounded data into finite time-based collections for aggregation

## Card 5
**Front**: What is the purpose of a trigger?
**Back**: Defines when window results are emitted: early (before watermark), on-time (at watermark), late (after watermark)
