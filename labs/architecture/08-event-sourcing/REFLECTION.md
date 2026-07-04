# Reflection: Event Sourcing

## Key Learnings
- Events are immutable facts, never delete or modify them
- Current state is always derivable from events
- Snapshots are essential for performance at scale
- Schema evolution must be planned from day one

## Challenges
- Event store size grows unboundedly over time
- Schema changes require upcasting strategy
- Temporal queries can be slow without snapshots
- Learning curve for developers used to CRUD

## Trade-offs
- **Storage vs History**: More storage for complete history
- **Complexity vs Auditability**: More complex but complete traceability
- **Write Performance vs Read Performance**: Simple writes, complex reads

## Questions to Consider
1. How long should events be retained?
2. What's the snapshot strategy?
3. How will events evolve over time?
4. What projections are needed?
5. How to handle GDPR/right-to-forget?

## Personal Application
- Use for domains where audit trail is critical
- Implement snapshots from day one
- Version events explicitly
- Use projections for queries, not event store
- Plan for event schema evolution
- Monitor event store size and projection lag
