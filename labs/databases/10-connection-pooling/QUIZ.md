# Quiz: Connection Pooling

## Question 1
What is the default connection pool in Spring Boot 2+?
- A) Tomcat DBCP
- B) HikariCP
- C) Commons DBCP 2
- D) c3p0

<details><summary>Answer</summary>B</details>

## Question 2
What happens when `connection-timeout` is exceeded?
- A) Connection is created anyway
- B) `SQLTransactionTimeoutException` is thrown
- C) Pool size is automatically increased
- D) Request is queued indefinitely

<details><summary>Answer</summary>B</details>

## Question 3
What does `leak-detection-threshold` do?
- A) Prevents connections from being leaked
- B) Logs a stack trace if a connection is held longer than the threshold
- C) Closes long-running connections
- D) Sends an alert when pool utilization > threshold

<details><summary>Answer</summary>B</details>

## Question 4
True or False: Increasing `maximum-pool-size` always improves application performance.

<details><summary>Answer</summary>False – too many connections cause database context switching and degrade performance.</details>

## Question 5
Why is `maxLifetime` important?
- A) It forces periodic password rotation
- B) It prevents the pool from using connections closed by the database
- C) It limits total connection usage per day
- D) It's required for SSL connections

<details><summary>Answer</summary>B</details>
