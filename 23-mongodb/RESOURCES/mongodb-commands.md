# MongoDB Commands

## Basic Operations

```javascript
// Database
use mydb                    // Switch/create database
db                          // Show current database
show dbs                    // List all databases

// Collections
db.createCollection('users') // Create collection
show collections             // List collections
db.users.drop()             // Drop collection

// CRUD Operations
db.users.insertOne({ name: 'Alice', age: 30 })
db.users.insertMany([{}, {}])

db.users.find()                    // Find all
db.users.find({ age: { $gt: 20 } }) // Query
db.users.findOne({ name: 'Alice' }) // Find one

db.users.updateOne({ name: 'Alice' }, { $set: { age: 31 } })
db.users.updateMany({}, { $inc: { age: 1 } })

db.users.deleteOne({ name: 'Alice' })
db.users.deleteMany({ age: { $lt: 18 } })
```

## Query Operators

```
┌─────────────────────────────────────────────────────────────────┐
│  COMPARISON                                                      │
├─────────────────────────────────────────────────────────────────┤
│  $eq    Equal              { age: { $eq: 25 } }                │
│  $ne    Not equal          { age: { $ne: 25 } }                │
│  $gt    Greater than       { age: { $gt: 25 } }                │
│  $gte   Greater or equal   { age: { $gte: 25 } }                │
│  $lt    Less than          { age: { $lt: 25 } }                │
│  $lte   Less or equal      { age: { $lte: 25 } }                │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  LOGICAL                                                         │
├─────────────────────────────────────────────────────────────────┤
│  $and   AND               { $and: [{a:1}, {b:2}] }            │
│  $or    OR                { $or: [{a:1}, {b:2}] }              │
│  $not   NOT               { age: { $not: { $gt: 25 } } }       │
│  $nor   NOT OR            { $nor: [{a:1}, {b:2}] }            │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  ELEMENT                                                         │
├─────────────────────────────────────────────────────────────────┤
│  $exists  Field exists    { email: { $exists: true } }        │
│  $type    Field type      { age: { $type: 'number' } }         │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  ARRAY                                                           │
├─────────────────────────────────────────────────────────────────┤
│  $in     In array         { status: { $in: ['active', 'pending'] } }
│  $all    Contains all    { tags: { $all: ['java', 'spring'] } }
│  $size   Array size      { skills: { $size: 3 } }              │
│  $elemMatch  Element match { scores: { $elemMatch: { $gt: 80 } } }
└─────────────────────────────────────────────────────────────────┘
```

## Update Operators

```javascript
// Set/Unset
{ $set: { name: 'New Name' } }
{ $unset: { tempField: 1 } }

// Increment/Decrement
{ $inc: { age: 1 } }
{ $inc: { count: -1 } }

// Array operators
{ $push: { skills: 'Java' } }
{ $pull: { skills: 'Python' } }
{ $addToSet: { skills: 'Go' } }  // Only if not exists

// Rename
{ $rename: { oldName: 'newName' } }
```

## Aggregation Pipeline

```javascript
db.users.aggregate([
  { $match: { age: { $gte: 18 } } },
  { $group: { _id: '$city', count: { $sum: 1 } } },
  { $sort: { count: -1 } },
  { $limit: 10 },
  { $project: { _id: 0, city: '$_id', count: 1 } }
])
```

### Aggregation Stages

```
┌─────────────────────────────────────────────────────────────────┐
│  STAGE              PURPOSE                                     │
├─────────────────────────────────────────────────────────────────┤
│  $match           Filter documents                             │
│  $project         Select/transform fields                      │
│  $group           Group and aggregate                          │
│  $sort            Sort results                                  │
│  $limit           Limit results                                │
│  $skip            Skip documents                               │
│  $unwind          Deconstruct arrays                           │
│  $lookup           Join collections                             │
│  $addFields       Add new fields                               │
│  $count           Count documents                              │
└─────────────────────────────────────────────────────────────────┘
```

### Aggregation Operators

```javascript
// Accumulator operators (in $group)
{ $sum: 1 }           // Sum
{ $avg: '$price' }    // Average
{ $min: '$price' }    // Minimum
{ $max: '$price' }    // Maximum
{ $push: '$name' }    // Array of values

// Arithmetic
{ $add: ['$price', '$tax'] }
{ $multiply: ['$price', 2] }
{ $round: [{ $divide: ['$total', '$count'] }, 2] }

// String
{ $toUpper: '$name' }
{ $concat: ['$firstName', ' ', '$lastName'] }

// Date
{ $year: '$createdAt' }
{ $month: '$createdAt' }
{ $dayOfMonth: '$createdAt' }
```

## Indexing

```javascript
// Create indexes
db.users.createIndex({ email: 1 })           // Single field
db.users.createIndex({ name: 1, age: -1 })  // Compound
db.users.createIndex({ email: 1 }, { unique: true })

// Text index
db.articles.createIndex({ title: 'text', body: 'text' })
db.articles.find({ $text: { $search: 'mongodb' } })

// Hashed index
db.users.createIndex({ _id: 'hashed' })

// Show indexes
db.users.getIndexes()

// Explain query
db.users.find({ email: 'test@example.com' }).explain()
```

## Java/Spring Data MongoDB

```java
// Repository
public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByAgeGreaterThan(int age);
    List<User> findByCityIn(List<String> cities);
    @Query("{ 'name': { $regex: ?0 } }")
    List<User> findByNameRegex(String regex);
}

// Service
@Service
@RequiredArgsConstructor
public class UserService {
    private final MongoTemplate mongoTemplate;

    public User save(User user) {
        return mongoTemplate.save(user);
    }

    public List<User> findByAgeRange(int min, int max) {
        Query query = new Query(Criteria.where("age").gte(min).lte(max));
        return mongoTemplate.find(query, User.class);
    }
}
```

## Common Queries

```javascript
// Pagination
db.users.find().skip(20).limit(10)

// Projection
db.users.find({}, { name: 1, email: 1, _id: 0 })

// Sort
db.users.find().sort({ createdAt: -1 })

// Distinct
db.users.distinct('city')

// Count
db.users.countDocuments({ status: 'active' })

// Upsert
db.users.updateOne(
    { email: 'new@example.com' },
    { $set: { name: 'New' } },
    { upsert: true }
)
```