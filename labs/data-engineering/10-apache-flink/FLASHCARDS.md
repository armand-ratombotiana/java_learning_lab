# Flashcards: Apache Flink

## Card 1
**Front**: What is a watermark in Flink?
**Back**: A metric tracking event time progress, used for window trigger decisions and late-event handling

## Card 2
**Front**: What is the difference between aligned and unaligned checkpoints?
**Back**: Aligned: wait for barriers from all inputs; Unaligned: proceed immediately, better under backpressure

## Card 3
**Front**: What is a savepoint?
**Back**: User-initiated checkpoint created manually for job version upgrades, parallelism changes, and maintenance

## Card 4
**Front**: What state backends does Flink support?
**Back**: HashMapStateBackend (heap, fast, <1GB) and RocksDBStateBackend (disk, >1GB, memory-efficient)

## Card 5
**Front**: What is Flink SQL?
**Back**: Relational API for stream processing; same SQL semantics over bounded and unbounded data
