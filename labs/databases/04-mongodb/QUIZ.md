# Quiz: MongoDB

## Question 1
What format does MongoDB use to store documents?
- A) JSON
- B) BSON
- C) XML
- D) YAML

**Answer: B) BSON**

## Question 2
Which operator checks if an array contains all specified elements?
- A) $in
- B) $all
- C) $contains
- D) $elemMatch

**Answer: B) $all**

## Question 3
What is the maximum document size in MongoDB?
- A) 4MB
- B) 8MB
- C) 16MB
- D) 64MB

**Answer: C) 16MB**

## Question 4
Which stage must come first in an aggregation pipeline for optimal performance?
- A) $group
- B) $project
- C) $match
- D) $sort

**Answer: C) $match**

## Question 5
How many data-bearing members are required in a replica set?
- A) 1
- B) 2
- C) 3
- D) 5

**Answer: A) 1 (minimum), 3 recommended for high availability**

## Question 6
What is the purpose of the oplog?
- A) Store query logs
- B) Track write operations for replication
- C) Cache frequently accessed documents
- D) Store user authentication data

**Answer: B) Track write operations for replication**

## Question 7
Which index type supports text search?
- A) B-tree
- B) Hashed
- C) Text
- D) Geospatial

**Answer: C) Text**

## Question 8
What does the `w` write concern option control?
- A) Write timeout
- B) Number of nodes that must acknowledge the write
- C) Write buffer size
- D) Write concurrency level

**Answer: B) Number of nodes that must acknowledge the write**

## Question 9
Which Java driver is recommended for reactive applications?
- A) mongodb-driver-sync
- B) mongodb-driver-reactivestreams
- C) mongodb-driver-async
- D) mongodb-driver-rxjava

**Answer: B) mongodb-driver-reactivestreams**

## Question 10
What is the correct way to create a unique index in MongoDB?
- A) collection.createIndex(fields)
- B) collection.createUniqueIndex(fields)
- C) collection.createIndex(fields, new IndexOptions().unique(true))
- D) collection.createIndex(fields, UniqueIndex)

**Answer: C) collection.createIndex(fields, new IndexOptions().unique(true))**
