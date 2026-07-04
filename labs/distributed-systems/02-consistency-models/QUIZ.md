# Consistency Models: Quiz

## Questions
1. What is the strongest consistency model?
2. What is the difference between linearizability and sequential consistency?
3. What guarantee does read-your-writes provide?
4. What is causal consistency?
5. Which consistency model does Cassandra support?
6. What metadata is needed for causal consistency?
7. What is the weakest consistency model?
8. Does DNS provide strong consistency?
9. What is monotonic read consistency?
10. Can you have linearizability in a geo-distributed system?

## Answers
1. Linearizability
2. Linearizability respects real-time order; sequential only respects program order
3. A process sees its own writes
4. Causally related operations are seen in causal order by all processes
5. Configurable (strong to eventual)
6. Vector clocks or dependency metadata
7. Eventual consistency
8. No (eventual)
9. Once you read a value, future reads return that or newer
10. Yes (e.g., Google Spanner with TrueTime)
