# Why Database Migrations Exist

Database migrations exist to solve the database-as-code problem. Before migration tools:

- Schema changes were manual SQL scripts shared via email or wiki
- No version control for database state
- Different environments drifted apart
- Rollbacks required manual reverse engineering
- Team coordination was error-prone

Migrations bring database changes into the same software engineering lifecycle as application code: version-controlled, peer-reviewed, CI/CD-pipeline, and repeatably deployable across environments.
