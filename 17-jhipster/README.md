# 17 - JHipster

JHipster application generation and scaffolding. Covers JHipster entity modeling (entity generation from JDL), code generation patterns (entities, repositories, services, DTOs, REST controllers), Liquibase changelog generation, and Angular/React frontend component scaffolding.

## Prerequisites

- Java 11+
- Maven 3.x
- JHipster (optional for code generation)

## Key Concepts

- Entity modeling: fields, types, constraints, relationships
- JHipster code generation: entities, repositories (JpaRepository), services (ServiceImpl), controllers (RestController), DTOs (MapStruct)
- Liquibase database changelog generation
- Angular component and route scaffolding
- JDL (JHipster Domain Language) for entity definition

## Module Structure

- `01-jhipster-basics/` - JHipster entity generation and scaffolding simulation

## Learning Objectives

- Model entities and generate CRUD code with JHipster
- Understand generated project structure (entities, repositories, services, controllers, DTOs)
- Generate Liquibase migrations for entity schemas

## Estimated Time

- 1-2 hours

## How to Build

```bash
cd 17-jhipster
mvn clean package
```

Run the lab:

```bash
cd 01-jhipster-basics
mvn compile exec:java -Dexec.mainClass="com.learning.jhipster.Lab"
```
