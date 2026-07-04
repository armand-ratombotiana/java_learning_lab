# Why Distributed ID Generation Matters

## Business Impact
- **Order numbers**: Sequential IDs aid customer service
- **Traceability**: Correlate requests across services
- **Performance**: ID generation should never be a bottleneck
- **Storage**: 64-bit IDs use 50% less storage than 128-bit UUIDs

## Technical Impact
- UUID v4 is simple but not sortable
- Snowflake requires clock synchronization
- Database sequences create coupling
- Bad ID design causes database index fragmentation

## Key Insight
The choice of ID generation strategy affects database performance, system scalability, and operational debuggability.
