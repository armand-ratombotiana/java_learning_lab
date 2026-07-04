# Visual Guide to Layered Architecture

## Layer Diagram
```
+------------------------------------------+
|         Presentation Layer                |
|  Controllers, DTOs, Validators            |
|  (Handles HTTP, JSON, views)              |
+------------------------------------------+
          |  depends on
          v
+------------------------------------------+
|          Business Layer                   |
|  Services, Business Logic, Validation     |
|  (Transactions, orchestration, rules)     |
+------------------------------------------+
          |  depends on
          v
+------------------------------------------+
|         Persistence Layer                 |
|  Repositories, DAOs, Entities             |
|  (Data access, mapping, queries)          |
+------------------------------------------+
          |  depends on
          v
+------------------------------------------+
|           Database                        |
|  (SQL, NoSQL, file system)               |
+------------------------------------------+

Cross-Cutting Layer:
+------------------------------------------+
|  Logging, Security, Caching, Monitoring   |
|  (Applies to all layers via AOP)          |
+------------------------------------------+
```

## N-Tier Deployment
```
+-------------------+
|   Web Server       |  <-- Presentation (Spring MVC)
|   (Tomcat)         |
+--------+-----------+
         |
+--------v-----------+
|   Application       |  <-- Business (Spring Services)
|   Server            |
+--------+-----------+
         |
+--------v-----------+
|   Database          |  <-- Data (PostgreSQL)
|   Server            |
+--------------------+
```

## Illegal Dependencies
```
    Controller
       |     \
       |      \  (WRONG! Skip service layer)
       |       \
       v        v
    Service    Repository (not allowed directly from controller!)
       |
       v
    Repository

Correct: Controller -> Service -> Repository
```
