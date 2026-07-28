# Lab 05: Mock Interview — Senior Big Data Engineer

**Interviewer**: "Your nightly Spark ETL job takes 6 hours and is getting slower. Walk me through your optimization process."

**Candidate**: "First, I'd look at the Spark UI — specifically the SQL tab for stage durations, shuffle read/write sizes, and spill metrics. I'd check if AQE is enabled — it's usually the first win. Then I'd look for data skew in the exchange stages."

**Interviewer**: "You find that one key (null) accounts for 40% of the data. How do you handle it?"

**Candidate**: "Null keys are a common skew pattern. I'd filter them out before the join, process them separately (if they produce no matches), or use a null-safe equality join. For non-null skew, I'd use the salted join pattern: detect skewed keys, add random salt to the large side, replicate the small side across salt buckets, and join on the composite key."

**Interviewer**: "The shuffle write size is 500GB and spilling to disk. What tuning would you do?"

**Candidate**: "500GB per stage suggests we need more partitions. I'd increase spark.sql.shuffle.partitions to 2000-4000. I'd also check spark.shuffle.spill.compress (should be true) and increase spark.reducer.maxSizeInFlight. If using sort-merge join, I'd verify the data is co-partitioned to avoid extra shuffles. I'd also look at enabling Kryo serialization for smaller shuffle payloads."

**Interviewer**: "How do you right-size the cluster for a 10TB batch job?"

**Candidate**: "I'd use the formula: total memory = 2x input size for processing overhead + shuffle buffers. For 10TB, I'd aim for ~20TB total executor memory. With 64GB per executor and 4 cores, that's about 320 executors. I'd leave 1-2 executors per node for overhead. I'd also ensure each executor has 5-7 cores max for optimal HDFS throughput."
