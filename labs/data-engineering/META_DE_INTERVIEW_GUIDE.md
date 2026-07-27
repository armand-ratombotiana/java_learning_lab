# Meta Data Engineer Interview Guide

## Interview Structure
- Recruiter Screen (30 min): Large-scale data experience, SQL + Python
- Technical Screen (45 min): Complex SQL + Python data processing
- System Design (45 min): Social graph, real-time pipelines, ads
- Behavioral (45 min): Meta behaviors, impact, speed
- Coding + Design Deep Dive (60 min): Graph algorithms, scale constraints

## Key Topics

### SQL at Scale
- **Window functions:** RANGE vs ROWS vs GROUPS, FIRST/LAST_VALUE
- **Self-joins:** Graph analytics (connections, mutual friends)
- **Array/JSON:** UNNEST, LATERAL VIEW EXPLODE
- **UDAFs:** Custom aggregation functions (Presto/Hive)
- **Presto-specific:** approx_distinct, approx_percentile, map_agg
- **Optimization:** predicate pushdown, partition pruning, join ordering

### Graph Analytics
- Friend-of-friend queries
- Mutual friends count
- Connection strength scoring
- Friend suggestion algorithms
- Community detection (Triangle counting, LPA)

### Data Infrastructure
- **Presto/Trino:** Query federation, connector architecture
- **Spark at Meta:** Shuffle improvements, external shuffle service
- **Hive:** ACID, LLAP, ORC format, bucketization
- **Scribe:** Real-time log aggregation (streaming event bus)
- **TAO:** Distributed graph store (associations, objects)
- **Scuba/Linter:** Real-time analytics, in-memory columnar

### Real-time Pipelines
- Scribe → real-time processing → HDFS/Database
- Spark Streaming for ML feature computation
- Sessionization from clickstream (30-min inactivity timeout)
- Real-time A/B experiment results (metric computation)
- Data quality checks at stream ingestion

### System Design at Meta Scale
- Social graph analytics: billions of edges
- Notifications pipeline: personalization + delivery
- News feed ranking: feature extraction, model inference, scoring
- Ad performance measurement: impression → click → conversion pipeline
- Data quality framework: row-level diffs, schema validation

### Meta Culture (Behaviors)
- **Move Fast:** Ship pipeline iterations quickly
- **Focus on Impact:** Prioritize data projects with highest ROI
- **Be Direct and Respect:** Technical disagreements, code reviews
- **Build Social Value:** Using data for meaningful social impact
- **Be Open:** Collaboration across teams, sharing learnings

## Sample Questions
1. "Design a friend recommendation system pipeline"
2. "Write a query to compute mutual friends between users"
3. "Design a real-time ads performance measurement pipeline"
4. "How would you sessionize 1B daily click events?"
5. "Design a data quality monitoring system at petabyte scale"

## Resources
- Meta Engineering Blog: data infrastructure posts
- Presto/Trino: documentation and internals
- Deploying Machine Learning at Scale (Meta papers)
