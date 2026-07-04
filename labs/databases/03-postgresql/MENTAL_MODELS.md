# Mental Models for PostgreSQL

## 1. PostgreSQL as an Extensible Operating System
Think of PostgreSQL as a "data operating system." Extensions = device drivers. Custom types = new file formats. Custom operators = new syscalls.

## 2. MVCC as Time Machine
Each transaction sees a snapshot of data at a point in time. Dead tuples are "past versions" that VACUUM eventually archives.

## 3. JSONB as Flexible Filing Cabinet
JSONB columns are like a filing cabinet drawer where each folder can have different labels – flexible schema within a structured table.

## 4. Full-Text Search as Index Cards
`tsvector` = index cards for each row (what words are in the document).
`tsquery` = query on the card catalog (find documents matching these words).

## 5. Indexes as Map Layers
Different index types are like different map layers: B-tree = road map, GIN = point-of-interest lookup, GiST = terrain map, BRIN = highway atlas.

## 6. WAL as Flight Recorder
The Write-Ahead Log records every change before it happens. Like a flight's black box data recorder. Crash? Replay the WAL.

## 7. Vacuum as Garbage Collection
Like Java's GC, VACUUM cleans up dead tuples. But it runs manually/autonomously, not in response to memory pressure.

## JPA + PostgreSQL Mental Model
- `@GeneratedValue(strategy = IDENTITY)` → `BIGSERIAL`
- `@Column(columnDefinition = "JSONB")` → JSONB columns
- `@Type(JsonBinaryType.class)` → Hibernate JSON mapping
