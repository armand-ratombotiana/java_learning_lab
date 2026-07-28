# Lab 09: Mock Interview — Real-Time Analytics Engineer

**Interviewer**: "Design a real-time analytics system for an e-commerce platform that tracks revenue, orders, and user sessions with sub-second latency."

**Candidate**: "I'd use Apache Flink as the stream processor with Kafka as the event bus. Events are produced from the website/mobile apps to Kafka topics. Flink ingests these events and performs windowed aggregations. The results are written to a key-value store (Redis or Memcached) for dashboard serving, and also to a time-series DB (VictoriaMetrics) for historical trends."

**Interviewer**: "How do you handle currency conversion in real-time?"

**Candidate**: "I'd maintain a currency conversion rate as a broadcast state in Flink. The rates come from a separate Kafka topic that's updated by the finance team. Each order event is enriched with the latest conversion rate before aggregation. This is a classic stream-table join pattern."

**Interviewer**: "Your dashboard shows revenue dropping by 10%. The stakeholder asks: is it real or a data problem? How do you investigate?"

**Candidate**: "First, I'd check the pipeline latency — if events are being processed with higher than normal lag, the drop might be due to unprocessed events. Second, I'd compare the current window to the same window from yesterday (YoY) and last week (WoW). If both show a similar drop, it's likely a real trend. Third, I'd check for data quality issues: null rates, schema validation errors, and late data percentages."

**Interviewer**: "How do you handle the trade-off between latency and accuracy for windowed revenue counts?"

**Candidate**: "I'd use a two-phase approach: early results and final results. The dashboard shows early results (with watermark-based triggers) that update every second, but also shows a 'final' marker when the watermark passes the window end. This gives the user immediate visibility while acknowledging that late data may adjust the final number. We display a confidence indicator next to each metric."
