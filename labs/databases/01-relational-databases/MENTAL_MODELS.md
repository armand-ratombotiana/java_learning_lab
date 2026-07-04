# Mental Models for Relational Databases

## 1. Spreadsheet Collections
Each table is a **spreadsheet**, but with strict row-level rules. Columns have types, rows have uniqueness constraints.

## 2. Lego Bricks
Tables are brick types. Foreign keys are the studs that connect them. A query assembles a structure from multiple brick types.

## 3. Filing Cabinet
Tables are drawers, rows are folders, columns are labels. The index is the tab system for fast lookup.

## 4. Set Theory Venn Diagram
SELECT combines sets. JOIN is intersection. UNION is union. WHERE is filtering elements.

## 5. Graph of Nodes
Each row is a node, each foreign key is an edge. Relational databases are specialized graphs optimized for set operations.

## Normalization Mental Model: "One Fact, One Place"
Every piece of data lives in exactly one place (plus FK references). This avoids update anomalies.

## ACID Mental Model: "Bank Transfer"
- Atomicity: Money leaves account A AND arrives at account B, or neither
- Consistency: Total money in the system is unchanged
- Isolation: Two simultaneous transfers don't interfere
- Durability: Once confirmed, the money doesn't disappear on crash

## JPA Mental Model: "Object Graph Persistence"
Your Java object graph is a view of the relational database. Each `@Entity` is a row, each `@OneToMany` is a FK relationship traversed like object references.
