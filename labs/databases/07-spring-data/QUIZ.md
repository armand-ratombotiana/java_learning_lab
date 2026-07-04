# Quiz: Spring Data JPA

## Question 1
What does `findByPriceBetween(BigDecimal min, BigDecimal max)` translate to in JPQL?
- A) `WHERE price = min AND price = max`
- B) `WHERE price BETWEEN ?1 AND ?2`
- C) `WHERE price > min OR price < max`
- D) `WHERE price IN (min, max)`

<details><summary>Answer</summary>B</details>

## Question 2
Which annotation marks a repository method as a modifying query?
- A) `@Query`
- B) `@Modifying`
- C) `@Transactional`
- D) `@DataJpaTest`

<details><summary>Answer</summary>B</details>

## Question 3
What is the return type of a paginated repository method?
- A) `List<T>`
- B) `Iterable<T>`
- C) `Page<T>`
- D) `Slice<T>` or `Page<T>`

<details><summary>Answer</summary>D</details>

## Question 4
True or False: Spring Data JPA repositories must always have an implementation class.

<details><summary>Answer</summary>False – Spring generates the implementation at runtime.</details>

## Question 5
Which annotation enables JPA auditing in a Spring Boot application?
- A) `@EnableJpaRepositories`
- B) `@EnableJpaAuditing`
- C) `@EnableAuditing`
- D) `@EntityListeners`

<details><summary>Answer</summary>B</details>
