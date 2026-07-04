# Saga Pattern Quiz

## Question 1
What problem does the Saga pattern solve?
- a) Service discovery
- b) Distributed data consistency without 2PC
- c) Load balancing
- d) API gateway routing

**Answer: b)**

## Question 2
What is the difference between choreography and orchestration?
- a) Choreography has a coordinator; orchestration is decentralized
- b) Orchestration has a coordinator; choreography is decentralized
- c) They are the same thing
- d) Choreography is for sync, orchestration for async

**Answer: b)**

## Question 3
What is a compensating transaction?
- a) A retry of a failed transaction
- b) An action that undoes a previous transaction
- c) A backup transaction
- d) An audit log entry

**Answer: b)**

## Question 4
What order should compensating transactions execute in?
- a) Same order as original steps
- b) Reverse order of original steps (LIFO)
- c) Random order
- d) Alphabetical order

**Answer: b)**

## Question 5
Why should saga steps be idempotent?
- a) To improve performance
- b) Because events may be delivered multiple times
- c) To reduce database size
- d) To simplify error handling

**Answer: b)**

## Question 6
When should you NOT use a saga?
- a) Cross-service workflow
- b) Long-running business process
- c) Simple local transaction
- d) When compensating actions are impossible

**Answer: c) and d)**
