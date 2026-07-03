# Module 58: Data Engineering in Java - Mini Project

**Project Name**: Resilient Batch ETL Pipeline  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Use **Spring Batch** to build a robust, fault-tolerant ETL (Extract, Transform, Load) pipeline that reads a massive CSV file, applies business logic transformations, and writes the data to a database without exhausting JVM memory.

## 📝 Requirements

### Core Features

1. **Project Setup**:
   - Create a Spring Boot application with dependencies: `spring-boot-starter-batch`, `spring-boot-starter-data-jpa`, and `h2` database.

2. **The Data Model**:
   - Create a simple CSV file `raw_transactions.csv` containing 1,000 rows. Columns: `transactionId`, `amount`, `currency`, `timestamp`. Intentionally introduce 3 rows with bad data (e.g., negative amounts or empty currencies).
   - Create an `@Entity` class `ProcessedTransaction` with fields: `id`, `usdAmount`, `status`.

3. **The ETL Pipeline (Spring Batch Configuration)**:
   - Configure a `Job` and a `Step` using Spring Batch's fluent builder API.
   - Set a **Chunk Size** of 100. This means the framework will read 100 items, transform 100 items, and write 100 items in a single transaction.

4. **Extract (ItemReader)**:
   - Use `FlatFileItemReader` to read the CSV file line by line mapping it to a `TransactionDto`.

5. **Transform (ItemProcessor)**:
   - Implement an `ItemProcessor<TransactionDto, ProcessedTransaction>`.
   - Business Logic: If the currency is "EUR", multiply the amount by 1.10 to get `usdAmount`. If the amount is negative, throw a `DataValidationException`.

6. **Load (ItemWriter)**:
   - Use `RepositoryItemWriter` to save the `ProcessedTransaction` entities to the H2 database.

7. **Fault Tolerance (Skip Logic)**:
   - Configure the Step to be fault-tolerant.
   - Tell the framework to `skip(DataValidationException.class)` and set a `skipLimit(10)`. This ensures that if a bad row is encountered, the job doesn't crash; it just skips the row, logs it, and continues processing the other 997 valid rows.

---

## 💡 Solution Blueprint

1. **Processor Implementation**:
   ```java
   @Component
   public class TransactionProcessor implements ItemProcessor<TransactionDto, ProcessedTransaction> {
       @Override
       public ProcessedTransaction process(TransactionDto item) throws Exception {
           if (item.getAmount() < 0) {
               throw new DataValidationException("Negative amount detected for ID: " + item.getId());
           }
           
           double usdAmount = item.getCurrency().equalsIgnoreCase("EUR") ? 
                              item.getAmount() * 1.10 : item.getAmount();
                              
           return new ProcessedTransaction(item.getId(), usdAmount, "CLEARED");
       }
   }
   ```

2. **Job Configuration**:
   ```java
   @Configuration
   public class BatchConfig {
       
       @Bean
       public Step transactionStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                   FlatFileItemReader<TransactionDto> reader,
                                   TransactionProcessor processor,
                                   RepositoryItemWriter<ProcessedTransaction> writer) {
           return new StepBuilder("transactionStep", jobRepository)
               .<TransactionDto, ProcessedTransaction>chunk(100, transactionManager)
               .reader(reader)
               .processor(processor)
               .writer(writer)
               .faultTolerant()
               .skip(DataValidationException.class)
               .skipLimit(10)
               .build();
       }
       
       @Bean
       public Job transactionJob(JobRepository jobRepository, Step step) {
           return new JobBuilder("transactionJob", jobRepository)
               .start(step)
               .build();
       }
   }
   ```