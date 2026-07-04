# Visual Guide: Migration Lifecycle

```
Development                    CI/CD                         Production
───────────                    ─────                         ──────────
                                                             
[Write V3__add_email.sql]  →  [Run tests]  →  [Deploy to    →  [migrate]
                              [validate]        staging]        [verify]
                                                
Schema Evolution Over Time:
                                                             
Version     Applied      Migration File
──────────────────────────────────────────────────
V1          ✅           V1__create_users.sql
V2          ✅           V2__add_roles.sql
V3          ❌           V3__add_email.sql    ← Pending
V4          ❌           V4__add_index.sql    ← Pending

Schema History Table (flyway_schema_history):
┌─────┬──────────┬──────────────────────┬──────────┐
│ ver │ checksum │ installed_by         │ success  │
├─────┼──────────┼──────────────────────┼──────────┤
│ 1   │ 12345678 │ jratombo-adm         │ true     │
│ 2   │ 23456789 │ jratombo-adm         │ true     │
└─────┴──────────┴──────────────────────┴──────────┘
```
