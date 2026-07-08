# Flashcards: Apache Iceberg

## Card 1
**Front**: What is Apache Iceberg?
**Back**: Open table format for huge analytic datasets with ACID, schema/partition evolution, and engine-agnostic design

## Card 2
**Front**: What is partition evolution?
**Back**: Ability to change table partitioning scheme without rewriting existing data files

## Card 3
**Front**: What is hidden partitioning?
**Back**: Automatic partition derivation from column values; users query by value not partition name

## Card 4
**Front**: What catalogs does Iceberg support?
**Back**: Hive, Nessie, JDBC, REST, Glue, DynamoDB — providing engine-agnostic metadata management

## Card 5
**Front**: What is an Iceberg snapshot?
**Back**: Point-in-time view of table; enables time travel, rollback, and incremental reads
