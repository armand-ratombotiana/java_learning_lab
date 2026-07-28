# Lab 05: Batch Optimization — Interview Questions

1. **Explain Spark's unified memory model**. What happens when execution memory spills into storage memory?

2. **Your Spark job is slow with a sort-merge join on a 10TB dataset**. How do you optimize it?

3. **What is data skew** and how do you detect and handle it in Spark?

4. **Compare broadcast hash join vs sort-merge join**. When is each optimal?

5. **How does Adaptive Query Execution (AQE) help with performance**?

6. **What's the impact of setting spark.sql.shuffle.partitions too high or too low**?

7. **How does Kryo serialization improve performance over Java serialization**?

8. **Design a strategy to handle a join where one key has 99% of the data**.

9. **What is a "straggler" in Spark and how do you mitigate it**?

10. **How do you choose the number of executors, cores, and memory** for a 1TB job?

11. **Explain the difference between coalesce and repartition**. When to use each?

12. **What causes a "shuffle spill" and how do you minimize it**?

13. **How does Parquet file format optimization** (predicate pushdown, column pruning) work?

14. **Design a file sizing strategy** to avoid the "small files problem" in Spark.

15. **How do you debug a Spark job with an OOM error**?
