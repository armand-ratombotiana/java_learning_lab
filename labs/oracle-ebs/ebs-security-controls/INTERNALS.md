# Internals: EBS Security Controls

## 1. Internal Data Structures

### 1.1 Request Queue

EBS uses FND_CONCURRENT_REQUESTS. Each request has: ID, Phase (P/R/C), Status (N/W/E), Hold, Priority.

### 1.2 Buffer Management

Internal buffers use PL/SQL associative arrays or Java ArrayList.

## 2. Internal Algorithms

### 2.1 Allocation

Step 1: Gather demand
Step 2: Gather supply
Step 3: Apply allocation rules
Step 4: Generate allocations

### 2.2 Pricing

Step 1: Determine price list
Step 2: Apply discounts
Step 3: Apply surcharges
Step 4: Calculate total

## 3. Internal Communication

- Database pipes (DBMS_PIPE)
- Advanced Queuing (AQ)
- Business Event System (BES)

## 4. Internal Logging

- FND_LOG_MESSAGES
- FND_LOG_EXCEPTIONS
- OAF logging (oracle.apps.jtf.log)
