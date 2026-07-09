# How It Works: EBS Architecture

## 1. Request Lifecycle

### 1.1 Submission
User submits through form or concurrent request. Inserted into FND_CONCURRENT_REQUESTS.

### 1.2 Validation

System validates input parameters, data types, and function security.

### 1.3 Processing

Business logic executes: database reads/writes, API calls, workflow triggers.

### 1.4 Completion

Request status updated. User notified if configured.

## 2. How MOAC Works

FND_GLOBAL.ORG_ID set. All MOAC-enabled queries filter by org via VPD.

## 3. How EBR Works

Edition creates private copy of objects. Patch in new edition. Users access old edition. Finalize/cutover switches users.

## 4. How Concurrent Processing Works

Concurrent manager polls FND_CONCURRENT_REQUESTS for phase=P. Spawns worker, sets phase=R, executes, sets phase=C with status.
