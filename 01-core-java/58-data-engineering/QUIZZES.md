# Module 58: Data Engineering in Java - Quizzes

---

## Q1: ETL vs ELT
What is the primary architectural shift that caused the industry to move from ETL (Extract, Transform, Load) to ELT (Extract, Load, Transform)?

A) The invention of Java 8 Streams.
B) The rise of highly scalable cloud Data Warehouses (like Snowflake and BigQuery), which made it cheaper and faster to push raw data into the warehouse first and execute massive transformations natively using the database's own elastic compute power.
C) The realization that transforming data is no longer necessary in modern business.
D) The deprecation of SQL.

**Answer**: B
**Explanation**: Traditionally, databases were expensive and had limited CPU. You had to transform data on separate servers before loading it. Cloud data warehouses have virtually infinite, on-demand compute, making in-database transformations (ELT) the standard practice.

---

## Q2: Data Storage Architectures
Which of the following best describes a Data Lake?

A) A rigid, highly structured relational database optimized for fast transaction processing (OLTP).
B) A repository that only stores data that has been cleaned, filtered, and aggregated for specific business reports.
C) A vast, cheap storage repository (like AWS S3) that holds massive amounts of raw, unstructured, semi-structured, and structured data in its native format until it is needed.
D) A temporary in-memory cache like Redis.

**Answer**: C
**Explanation**: Data Lakes use "Schema-on-read", meaning you just dump the raw data in. The schema is applied only when the data is queried. Data Warehouses use "Schema-on-write", meaning the data must be transformed to fit rigid tables before insertion.

---

## Q3: Processing Paradigms
If a bank needs to detect fraudulent credit card transactions the millisecond the card is swiped, which processing paradigm must they use?

A) Batch Processing
B) Stream Processing
C) Offline Processing
D) MapReduce

**Answer**: B
**Explanation**: Batch processing runs on a schedule (e.g., analyzing all transactions at midnight). Stream processing analyzes data continuously in real-time as the events flow through the system (e.g., via Kafka), which is required for instant fraud detection.