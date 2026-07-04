# Distributed ID Generation: Quiz

## Questions
1. How many bits are in a Snowflake ID?
2. What does the timestamp component in Snowflake represent?
3. What is the advantage of UUID v7 over v4?
4. How does Snowflake handle sequence exhaustion?
5. What is the Hi/Lo algorithm?
6. What causes clock drift in ID generation?
7. How many IDs per second can Snowflake generate per worker?
8. Why are sequential IDs a security concern?
9. What is worker ID allocation?
10. How long can Snowflake generate unique IDs before wrapping?

## Answers
1. 64 bits
2. Milliseconds since custom epoch
3. Time-ordered (sortable)
4. Waits for next millisecond
5. Allocate ID range (hi) from DB, assign locally (lo)
6. System clock synchronization (NTP adjustments)
7. 4096 (with 12 sequence bits)
8. They reveal application growth rate and enable enumeration
9. Assigning unique node identifiers to prevent collisions
10. ~69 years (with 41-bit timestamp)
