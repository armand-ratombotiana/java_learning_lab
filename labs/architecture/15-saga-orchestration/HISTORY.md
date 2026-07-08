# History: Saga Pattern

## Origins
The Saga pattern was first described by Hector Garcia-Molina and Kenneth Salem in their 1987 paper Sagas. It was developed as an alternative to distributed transactions for long-lived transactions.

## Evolution

### 1987
The original Sagas paper proposed a mechanism for maintaining data consistency in long-lived transactions without blocking resources.

### 1990s
Sagas remained a theoretical concept as most systems were monolithic with local transactions.

### 2000s
Microservices architecture revived interest in sagas. Distributed transactions became necessary but two-phase commit proved unsuitable.

### 2010s
Chris Richardson popularized the Saga pattern for microservices. Axon Framework and eventually Eventuate Tram provided Java implementations.

### 2020s
Saga orchestration becomes standard for microservices transactions. Camunda BPM and Temporal provide workflow-based saga engines.

## Key Milestones
- 1987: Original Sagas paper by Garcia-Molina and Salem
- 2014: Chris Richardson describes saga for microservices
- 2015: Axon Framework saga support
- 2017: Eventuate Tram saga implementation
- 2019: Camunda BPM for saga orchestration
- 2021: Temporal workflow engine for sagas
