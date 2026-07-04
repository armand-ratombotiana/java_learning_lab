# Visual Guide: Spring Data JPA Architecture

```
┌─────────────────────────────────────────────────┐
│                  Application Layer               │
│  UserService ──→ UserRepository (Interface)      │
└──────────────────────┬──────────────────────────┘
                       │ proxy invocation
                       ▼
┌─────────────────────────────────────────────────┐
│            Spring Data JPA (Runtime)             │
│  ┌───────────────────────────────────────────┐   │
│  │  JpaRepositoryFactory                     │   │
│  │  └→ JDK Dynamic Proxy                     │   │
│  │     └→ SimpleJpaRepository<T, ID>         │   │
│  │        ├─ save() ───── EntityManager.persist│  │
│  │        ├─ findById() ─ EntityManager.find │  │
│  │        ├─ findAll() ── CriteriaQuery      │  │
│  │        └─ custom ───── PartTreeJpaQuery   │  │
│  └───────────────────────────────────────────┘   │
└──────────────────────┬──────────────────────────┘
                       │ JPA API
                       ▼
┌─────────────────────────────────────────────────┐
│              EntityManager / Hibernate           │
│  ┌──────────────┐  ┌───────────────────────┐    │
│  │ 1st Level    │  │   SQL Generation      │    │
│  │ Cache (Persistence│  JPQL → SQL AST      │    │
│  │ Context)     │  │   → parameter bind    │    │
│  └──────────────┘  └───────────────────────┘    │
└──────────────────────┬──────────────────────────┘
                       │ JDBC
                       ▼
┌─────────────────────────────────────────────────┐
│              Database (PostgreSQL, MySQL, etc.)  │
└─────────────────────────────────────────────────┘
```
