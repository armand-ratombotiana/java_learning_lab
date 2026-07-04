# Quiz: Database Migrations

## Question 1
What table does Flyway use to track applied migrations?
- A) `DATABASECHANGELOG`
- B) `flyway_schema_history`
- C) `migration_tracker`
- D) `schema_version`

<details><summary>Answer</summary>B</details>

## Question 2
Which Liquibase file format is NOT supported?
- A) XML
- B) YAML
- C) TOML
- D) JSON

<details><summary>Answer</summary>C</details>

## Question 3
What does `flyway baseline` do?
- A) Drops all tables and reapplies all migrations
- B) Marks an existing database as already at a given version
- C) Rolls back the last migration
- D) Repairs a corrupted history table

<details><summary>Answer</summary>B</details>

## Question 4
True or False: You can edit an already-applied Flyway migration and re-run it.

<details><summary>Answer</summary>False – checksum validation will fail. Create a new migration instead.</details>

## Question 5
What is the purpose of `flyway_schema_history` checksums?
- A) Performance optimization
- B) Detect tampering or modification of applied migrations
- C) Encrypt migration content
- D) Track who ran the migration

<details><summary>Answer</summary>B</details>
