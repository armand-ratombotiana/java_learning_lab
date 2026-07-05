# Exercises: Event Streaming

## Beginner
1. Implement a simple producer that sends records to a topic
2. Implement a consumer that reads records and prints them
3. Add topic auto-creation on first produce request

## Intermediate
4. Implement log segment rolling (close active segment after size threshold)
5. Add partition replication with leader/follower model
6. Implement consumer group coordination with rebalancing
7. Add offset index (sparse mapping of offset -> file position)

## Advanced
8. Implement log compaction (retain latest value per key)
9. Add exactly-once semantics with transactional producer
10. Implement incremental cooperative rebalancing (sticky partition assignor)
11. Add tiered storage (S3 for cold data, local SSD for hot)
12. Implement KRaft metadata mode (Raft consensus without ZooKeeper)
