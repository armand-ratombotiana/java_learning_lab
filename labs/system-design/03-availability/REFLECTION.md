# Availability - REFLECTION

## Key Takeaways

1. **Failures are inevitable**: Accept that components will fail. Design for failure, not to prevent it.

2. **Redundancy is not enough without testing**: A standby that hasn't been tested will fail when you need it.

3. **MTTR matters more than MTBF**: Improving how fast you recover has more impact than preventing failures.

4. **Complexity is the enemy of availability**: Each additional component adds failure modes. Keep it simple.

## Self-Assessment Questions

- Can I calculate availability for a system architecture?
- Do I understand when to use circuit breakers vs retries?
- Can I design a disaster recovery plan?
- Do I know the trade-offs between active-active and active-passive?

## Common Misconceptions

- "99.999% availability means 5 minutes per year" — It's 5.26 minutes, and it's an average, not a guarantee
- "Add more servers = higher availability" — Only if redundancy is designed in; adding a single server to a chain reduces availability
- "We don't need failover, we'll fix it fast" — Every minute of downtime costs significant money

## Next Steps

- Set up a Kubernetes cluster and test pod eviction
- Implement chaos engineering experiments
- Practice disaster recovery drills
- Read "Site Reliability Engineering" by Google SRE team
