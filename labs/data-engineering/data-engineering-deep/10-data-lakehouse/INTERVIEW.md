# Lab 10: Data Lakehouse — Interview Questions

1. **What is a lakehouse and how does it differ from a data lake and a data warehouse**?

2. **Compare Delta Lake, Apache Iceberg, and Apache Hudi**. What are the key trade-offs?

3. **How does Delta Lake's transaction log provide ACID guarantees** on object storage?

4. **How does Iceberg's manifest-based architecture enable partition evolution**?

5. **Design a time-travel query system** — how do you access data as of a specific version?

6. **What is "copy-on-write" vs "merge-on-read"** in Hudi? When is each better?

7. **How does Z-order clustering improve query performance** in a lakehouse?

8. **How do you handle concurrent writes** to a Delta Lake table?

9. **What is hidden partitioning in Iceberg** and how does it benefit query performance?

10. **Design a system to migrate a Hive-style partitioned table to Iceberg format**.

11. **How do you compact small files** in a lakehouse? Why is it necessary?

12. **What is the role of the metastore** (Hive Metastore, AWS Glue) in a lakehouse?

13. **How would you implement a MERGE (UPSERT) operation** on a lakehouse table?

14. **What are the performance implications of many partitions vs many files**?

15. **How does a lakehouse handle schema evolution** compared to a traditional data warehouse?
