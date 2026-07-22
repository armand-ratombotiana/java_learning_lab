# Production Scenarios: Cassandra (Oracle Comparison)

## Scenario 1: Read Repair High CPU
**Context**: A 12-node Cassandra cluster supporting a real-time recommendation engine.
**Problem**: During peak traffic, CPU on all nodes was 95-100%. Read latency increased from 5ms to 200ms. `nodetool tpstats` showed read repair tasks consuming most CPU.
**Root Cause`: `read_repair_chance` was set to 1.0 (100% read repair). Every read was performing digest queries to all replicas and comparing results. With RF=3 and CL=QUORUM, each read triggered 2 additional read requests for repair comparison.
**Solution**: 1) Reduced `read_repair_chance` to 0.0. 2) Rely on hinted handoff and repair for consistency instead of on-read repair. 3) Scheduled `nodetool repair` to run weekly during low traffic. 4) Set `dclocal_read_repair_chance` to 0.1 for eventual cross-DC repair. 5) Monitored repair metrics: `nodetool cfstats` read repair counts.
**Lessons Learned**: On-read repair is expensive — use low probability or disable it. Use periodic `nodetool repair` for consistency. Monitor read repair metrics. Tune read repair based on consistency requirements.

## Scenario 2: Hinted Handoff Queue Growing
**Context**: A Cassandra node in a 6-node cluster went down for 3 hours for maintenance.
**Problem**: When the node came back, the hinted handoff queue had 5M hints to replay. The node took 2 hours to catch up, during which reads returned stale data.
**Root Cause**: The `max_hint_window_in_ms` was at default (3 hours). The maintenance window exceeded this. All writes during the 3 hours were stored as hints on the coordinator nodes. The hinted handoff replay was single-threaded by default.
**Solution**: 1) Increased `max_hint_window_in_ms` to 12 hours for longer maintenance windows. 2) Increased `max_hints_delivery_threads` from 1 to 4. 3) Monitored `nodetool stats` for pending hints. 4) Considered using `nodetool rebuild` for faster node recovery. 5) Planned maintenance windows shorter than `max_hint_window`.
**Lessons Learned**: Keep node downtime within `max_hint_window_in_ms`. Tune hinted handoff delivery threads. Monitor pending hints after node recovery. Use `nodetool rebuild` for nodes down > window.

## Scenario 3: Compaction Strategy Disk Exhaustion
**Context**: A Cassandra cluster with time-series data using `SizeTieredCompactionStrategy`.
**Problem`: Disk usage suddenly increased to 95%. Compaction was running constantly. Writes started failing with "No space left on device".
**Root Cause**: STCS compaction creates temporary copies of SSTables during compaction. A compaction of a 500GB SSTable tier required 500GB of temporary space. The disk only had 100GB free. The compaction could not complete, leaving partially compacted SSTables consuming more space.
**Solution**: 1) Temporarily added 500GB disk space to each node. 2) Changed compaction strategy to `TimeWindowCompactionStrategy` (TWCS) for time-series data. 3) Configured TWCS: `compaction_window_unit = DAYS`, `compaction_window_size = 1`. 4) Reduced SSTable count with `nodetool compact`. 5) Monitored `nodetool cfstats` for SSTable count and estimated disk usage.
**Lessons Learned**: Use TWCS for time-series data. STCS requires 50% free disk for compaction. Monitor compaction backlog. Choose compaction strategy based on data access pattern.

## Scenario 4: Tombstone Overload
**Context**: A Cassandra table used for shopping cart items had frequent deletions.
**Problem**: Queries for active carts started timing out. `nodetool cfstats` showed millions of tombstones. Read latency increased to 10 seconds.
**Root Cause**: The application deleted cart items and then queried all items for a cart. Deleted items left tombstones. The query `SELECT * FROM cart_items WHERE cart_id = X` had to scan through thousands of tombstones per query. `gc_grace_seconds` was at default 864000 (10 days), so tombstones were not compacted for 10 days.
**Solution**: 1) Changed the query to use `ALLOW FILTERING` with a status column instead of deletes: mark item as 'REMOVED' instead of deleting. 2) Lowered `gc_grace_seconds` to 86400 (1 day) for the cart table. 3) Added TTL to cart items: expired after 24 hours. 4) Manually compacted the table: `nodetool compact cart_items`. 5) Monitored tombstone counts: `nodetool cfstats`.
**Lessons Learned**: Avoid frequent deletes in Cassandra — use soft deletes. Set appropriate TTLs for temporary data. Monitor tombstone ratios. Compact tables with high tombstone counts. Choose appropriate `gc_grace_seconds`.
