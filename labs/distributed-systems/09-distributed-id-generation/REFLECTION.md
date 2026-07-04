# Distributed ID Generation: Reflection

## Key Insights
- 64-bit Snowflake IDs are more efficient than 128-bit UUIDs
- Time-ordered IDs improve B-tree index performance
- Clock handling is the hardest part of ID generation
- The right ID strategy improves database performance significantly

## Questions
1. Are your current IDs causing database index fragmentation?
2. Do you need globally unique or just unique per service?
3. Could readable IDs cause security issues?
4. What's your ID generation throughput requirement?

## Personal Notes
- Snowflake is almost always the right choice for new systems
- UUID v7 addresses UUID's main drawback (no ordering)
- Don't use MAC addresses in UUID v1 for privacy reasons
