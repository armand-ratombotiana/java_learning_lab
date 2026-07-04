# Distributed ID Generation: Flashcards

## Front: What is Snowflake?
**Back**: Twitter's 64-bit time-sorted distributed ID generator.

## Front: Snowflake bit layout?
**Back**: 1 sign + 41 timestamp + 10 worker + 12 sequence = 64 bits.

## Front: How many Snowflake IDs per ms per worker?
**Back**: 4096 (2¹²).

## Front: What is UUID v7?
**Back**: Time-ordered UUID with timestamp prefix.

## Front: What is the Hi/Lo algorithm?
**Back**: Batch allocate ID ranges to avoid DB calls per ID.

## Front: What causes Snowflake ID collisions?
**Back**: Clock drift (backwards), duplicate worker IDs.

## Front: What is worker ID allocation?
**Back**: Process of assigning unique node IDs.

## Front: UUID v4 collision probability?
**Back**: ~2⁻¹²² per pair, practically zero.
