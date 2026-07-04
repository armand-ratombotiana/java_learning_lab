# Flashcards: Database Migrations

**Q:** What is the naming convention for Flyway versioned migrations?
**A:** `V<version>__<description>.sql` (e.g., `V2__add_email.sql`)

**Q:** What happens if you modify an already-applied migration?
**A:** Flyway detects checksum mismatch and fails validation.

**Q:** How does Liquibase identify a changeset?
**A:** By `id` + `author` combination (must be unique across changelog)

**Q:** What command repairs a failed Flyway migration?
**A:** `flyway repair` – removes failed entry from history

**Q:** What is `flyway baseline` used for?
**A:** Starting Flyway on an existing database without running prior migrations.

**Q:** What table does Liquibase use for tracking?
**A:** `DATABASECHANGELOG` (and `DATABASECHANGELOGLOCK` for locking)

**Q:** Difference between versioned and repeatable migrations in Flyway?
**A:** Versioned run once (ordered by version). Repeatable run every time they change (e.g., views, functions).

**Q:** What is a pre-condition in Liquibase?
**A:** Validation check before running a changeset (e.g., check column exists, table doesn't exist).
