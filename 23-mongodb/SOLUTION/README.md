# MongoDB Solution

Reference implementation for MongoDB CRUD, aggregation, and transactions.

## CRUD Operations
- `MongoCollection` interface
- `insertOne`, `insertMany`
- `find`, `findAll`, `findFirst`
- `updateOne`, `updateMany`
- `deleteOne`, `deleteMany`

## Filters
- `Filters.eq`, `ne`, `gt`, `gte`, `lt`, `lte`
- `Filters.in`, `nin`
- `Filters.and`, `or`
- `Filters.regex`, `exists`

## Updates
- `Updates.set`, `unset`
- `Updates.inc`, `mul`
- `Updates.push`, `pull`, `addToSet`
- `Updates.rename`, `currentDate`

## Aggregation Pipeline
- `match`, `group`, `project`
- `sort`, `limit`, `skip`
- Accumulator functions: `sum`, `avg`, `min`, `max`, `push`

## Transactions
- `ClientSession` for transaction management
- `startTransaction`, `commitTransaction`, `abortTransaction`

## Indexes
- `Index.ascending`, `Index.descending`
- `Index.unique` for unique indexes

## Test Coverage: 45+ tests