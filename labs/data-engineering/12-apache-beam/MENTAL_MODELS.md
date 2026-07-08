# Mental Models for Apache Beam

## 1. The Recipe
Pipeline = Recipe. PCollection = Ingredients. PTransform = Cooking steps. Runner = Kitchen equipment (oven, stove, microwave). Same recipe works on different equipment.

## 2. The Assembly Line
PCollection is conveyor belt. ParDo is worker station. Each worker processes one item. Multiple workers at same station = parallelism. Windows are bins for time-based grouping.

## 3. The Universal Remote
Beam is a universal remote. Write one pipeline, run on any runner (Dataflow TV, Flink Sound System, Spark Player). The remote API is consistent regardless of the device.
