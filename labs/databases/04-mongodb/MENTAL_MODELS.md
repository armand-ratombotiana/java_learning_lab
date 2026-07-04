# Mental Models: MongoDB

## Document = JSON Object
Think of MongoDB as a file system where each file is a JSON document. Collections are folders grouping related documents. No schema means each file can have different fields.

## Embed vs Reference Decision Tree
```
Is the relationship 1:1?  → Embed
Is the sub-data always accessed with parent? → Embed
Is the sub-data small (< 16MB)? → Embed
Will the sub-data grow unbounded? → Reference
Do you need independent queries on sub-data? → Reference
Do you need N:M relationships? → Reference
```

## Aggregation Pipeline = Assembly Line
Each stage transforms documents and passes them to the next:
```
$match (filter) → $sort → $group → $project → $limit
```
Like Unix pipes: `grep | sort | uniq -c | head`

## Indexes = Library Card Catalog
Without an index, finding a document requires scanning every document (collection scan). With an index, MongoDB navigates a B-tree to locate documents directly. Compound indexes are like sorted-by-lastname-then-firstname directories.

## Replica Set = Ensemble Cast
One primary (lead actor) handles writes. Secondaries (supporting cast) maintain copies and can take over if the primary fails. The oplog is the script they all follow.

## Sharding = Multiple Checkout Lines
Instead of one line serving all customers, split customers across multiple registers. The shard key determines which register serves which customer. The mongos router directs each request to the correct register.
