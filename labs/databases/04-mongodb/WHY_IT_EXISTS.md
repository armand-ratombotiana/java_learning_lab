# Why MongoDB Exists

MongoDB was created in 2007 by 10gen (now MongoDB Inc.) to address limitations of traditional relational databases in modern web application development.

## Problems MongoDB Solves

### Object-Relational Impedance Mismatch
Relational databases require mapping between OOP objects and normalized tables. MongoDB's document model stores data in the same structure as application objects, eliminating the ORM mapping layer.

### Schema Rigidity
RDBMS require predefined schemas with ALTER TABLE migrations. MongoDB's schema-less design allows fields to vary between documents, enabling rapid iteration.

### Horizontal Scaling
RDBMS scale vertically (bigger servers) which hits hardware limits. MongoDB scales horizontally via sharding — distributing data across commodity servers.

### Developer Velocity
- No JOINs needed for related data (embedded documents)
- Familiar JSON-like format for JavaScript/Node.js developers
- Fast prototyping without migration scripts

## Inspiration
MongoDB drew from Google's Bigtable (distributed storage) and Amazon's Dynamo (key-value store), combining document flexibility with horizontal scalability.
