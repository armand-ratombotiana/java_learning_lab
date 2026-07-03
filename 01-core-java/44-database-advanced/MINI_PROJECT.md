# Module 44: Advanced Database Concepts - Mini Project

**Project Name**: Database Performance & Concurrency Tester  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Understand database isolation levels, indexing, and the N+1 problem by implementing a Spring Boot application that deliberately triggers concurrency anomalies and performance issues, and then fixes them.

## 📝 Requirements

### Core Features

1. **The N+1 Problem Simulator**:
   - Create two JPA Entities: `Author` and `Book` (One-to-Many).
   - Insert 10 Authors, each with 10 Books, into an H2 database.
   - Write a REST endpoint `/api/authors/slow` that fetches all authors using `repository.findAll()` and then loops through them, calling `author.getBooks().size()`. Turn on `spring.jpa.show-sql=true` to observe the massive number of queries logged.
   - Write a second endpoint `/api/authors/fast` that uses a JPQL `JOIN FETCH` query to fetch the exact same data. Observe that only 1 SQL query is logged.

2. **The Dirty Read Simulator**:
   - Create a service method `updateBalanceSlowly(Long id, double amount)` that updates a user's balance but sleeps for 5 seconds before committing the transaction (`@Transactional`).
   - Create a second service method `readBalance(Long id)` that reads the user's balance.
   - *Experiment*: In your controller, annotate the read method's transaction with `@Transactional(isolation = Isolation.READ_UNCOMMITTED)`. 
   - Fire a request to update the balance, and immediately fire a request to read it. Observe that the read method sees the uncommitted balance.
   - Change the isolation level to `READ_COMMITTED` and run the experiment again. The read method should now block or see the old balance until the update commits.

3. **Indexing Benchmark**:
   - Create an entity `LogEntry` with a `String message` field.
   - Insert 100,000 random log entries into the database.
   - Write a method that searches for a specific message using `findByMessage(String message)`. Time the execution of this query in milliseconds.
   - Now, add an index to the database (`CREATE INDEX idx_message ON log_entry(message)`). You can execute this via a native query or Flyway/Liquibase.
   - Run the search again and log the time difference to prove the efficiency of B-Tree indexing.

---

## 💡 Solution Blueprint

1. **N+1 Problem Fix (Repository)**:
   ```java
   public interface AuthorRepository extends JpaRepository<Author, Long> {
       // Slow: Relies on default Lazy loading in a loop
       
       // Fast: Fetches all books in a single query
       @Query("SELECT a FROM Author a JOIN FETCH a.books")
       List<Author> findAllWithBooks();
   }
   ```

2. **Isolation Level Testing**:
   ```java
   @Service
   public class BankService {
       @Transactional
       public void updateBalanceSlowly(Long id, double amount) {
           Account acc = repo.findById(id).orElseThrow();
           acc.setBalance(acc.getBalance() + amount);
           repo.save(acc);
           try { Thread.sleep(5000); } catch (Exception e) {}
           // If an exception was thrown here, the transaction would roll back.
           // A Dirty Read would have seen the temporary, invalid balance.
       }

       // Allows reading data that hasn't been committed yet!
       @Transactional(isolation = Isolation.READ_UNCOMMITTED)
       public double dirtyReadBalance(Long id) {
           return repo.findById(id).orElseThrow().getBalance();
       }
   }
   ```