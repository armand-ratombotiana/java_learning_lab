# Mental Models: Database Migrations

## Git for Databases
Think of migrations as Git commits for your schema. Each migration is a commit with a diff. `flyway migrate` is like `git pull` – it applies pending changes. `flyway undo` is `git revert`. The `flyway_schema_history` table is the reflog.

## Staircase
Each migration is one step up a staircase. You can only go forward one step at a time. Skipping steps is not allowed. You always know exactly which step you're on.

## Recipe Book
Migrations are recipes. The current database state is the meal. You can see the full recipe list (migration files) and which recipes have been cooked (history table). Anyone can recreate the meal from scratch by following all recipes in order.

## Snapshot Album
Each migration creates a snapshot. You can view the album to see how the schema evolved. `flyway info` shows the table of contents. `flyway baseline` marks which snapshot you started from.
