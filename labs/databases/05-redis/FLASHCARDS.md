# Flashcards: Redis

## Card 1
**Q**: What port does Redis run on?
**A**: 6379

## Card 2
**Q**: What is the default persistence of Redis?
**A**: RDB snapshots

## Card 3
**Q**: Is Redis single-threaded?
**A**: Yes (command execution), I/O threaded since 6.0

## Card 4
**Q**: What data structure supports leaderboards?
**A**: Sorted Set

## Card 5
**Q**: What command sets a key only if it doesn't exist?
**A**: SETNX

## Card 6
**Q**: What does TTL stand for?
**A**: Time To Live

## Card 7
**Q**: How many databases exist by default in Redis?
**A**: 16 (indexed 0-15)

## Card 8
**Q**: What is the difference between Redis and Memcached?
**A**: Redis has richer data structures, persistence, replication

## Card 9
**Q**: What is the maximum string value size?
**A**: 512MB

## Card 10
**Q**: What does AOF stand for?
**A**: Append-Only File

## Card 11
**Q**: How do you create a distributed lock in Redis?
**A**: SETNX + EXPIRE (or Redlock algorithm)

## Card 12
**Q**: What does Redis accept for key-value size limit?
**A**: Keys up to 512MB, values up to 512MB

## Card 13
**Q**: What does the `MULTI` command start?
**A**: A transaction block

## Card 14
**Q**: What is sentinel used for?
**A**: High availability, automatic failover

## Card 15
**Q**: What does `BGSAVE` do?
**A**: Fork and save RDB in background

## Card 16
**Q**: What is the pub/sub pattern?
**A**: Publisher sends to channel, subscribers receive

## Card 17
**Q**: What is the main disadvantage of single-threaded?
**A**: One slow command blocks all others

## Card 18
**Q**: What is `SCAN` used for?
**A**: Iterate keys incrementally (non-blocking)

## Card 19
**Q**: What does the `--bigkeys` flag show?
**A**: Largest keys by data type

## Card 20
**Q**: How does Redis Cluster achieve high availability?
**A**: Data sharded across masters, each master has replicas
