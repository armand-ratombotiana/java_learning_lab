# Flashcards: MongoDB

## Card 1
**Q**: What is the default port for MongoDB?
**A**: 27017

## Card 2
**Q**: What storage engine does MongoDB use?
**A**: WiredTiger (since 3.2)

## Card 3
**Q**: What is the maximum document size?
**A**: 16MB

## Card 4
**Q**: What does BSON stand for?
**A**: Binary JSON

## Card 5
**Q**: Which aggregation stage filters documents?
**A**: $match

## Card 6
**Q**: What is the replica set minimum recommended size?
**A**: 3 data-bearing members

## Card 7
**Q**: How does MongoDB store relationships (two ways)?
**A**: Embedding and referencing

## Card 8
**Q**: What is the oplog?
**A**: Capped collection tracking all writes for replication

## Card 9
**Q**: What does ESR stand for in indexing?
**A**: Equality, Sort, Range

## Card 10
**Q**: What read preference routes reads to primary only?
**A**: primary (default)

## Card 11
**Q**: How do you prevent SQL-like injection in MongoDB?
**A**: Use Filters/builders, not string interpolation

## Card 12
**Q**: What does `$lookup` do?
**A**: Left-outer join (similar to SQL JOIN)

## Card 13
**Q**: What is a covered query?
**A**: Query satisfied entirely by index, no document fetch

## Card 14
**Q**: What write concern value waits for majority acknowledgment?
**A**: `w: "majority"`

## Card 15
**Q**: What is a change stream?
**A**: Real-time notification of database changes

## Card 16
**Q**: How long can a transaction be open?
**A**: Default 60 seconds (`transactionLifetimeLimitSeconds`)

## Card 17
**Q**: What is the difference between `$set` and `$push`?
**A**: `$set` updates a field value, `$push` appends to an array

## Card 18
**Q**: What size are default chunks in sharding?
**A**: 64MB

## Card 19
**Q**: What does `mongos` do?
**A**: Routes queries to the correct shard(s)

## Card 20
**Q**: What is the `_id` field type?
**A**: ObjectId by default (12-byte, unique, sortable)
