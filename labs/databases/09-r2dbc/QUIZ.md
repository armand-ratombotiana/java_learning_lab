# Quiz: R2DBC

## Question 1
What does R2DBC stand for?
- A) Reactive Relational Database Connectivity
- B) Reactive Relational DataBase Connector
- C) Reactive Relational Data Binding Component
- D) Reusable Relational Database Connectivity

<details><summary>Answer</summary>A</details>

## Question 2
Which return type is used for a single result in reactive repository?
- A) `Optional<T>`
- B) `T`
- C) `Mono<T>`
- D) `CompletableFuture<T>`

<details><summary>Answer</summary>C</details>

## Question 3
What does `.limitRate(100)` do in a reactive stream?
- A) Limits the total number of emitted elements to 100
- B) Requests elements from upstream in batches of 100
- C) Throws an error if more than 100 elements
- D) Sets timeout to 100ms

<details><summary>Answer</summary>B</details>

## Question 4
True or False: R2DBC supports JPA annotations like `@Entity` and `@Table`.

<details><summary>Answer</summary>False – R2DBC is not an ORM and does not support JPA annotations.</details>

## Question 5
Which scheduler should be used for wrapping blocking JDBC calls in a reactive application?
- A) `Schedulers.parallel()`
- B) `Schedulers.single()`
- C) `Schedulers.boundedElastic()`
- D) `Schedulers.immediate()`

<details><summary>Answer</summary>C</details>
