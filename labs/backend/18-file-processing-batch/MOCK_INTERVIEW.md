# Mock Interview: File Processing & Spring Batch (Lab 18)

**Role:** Backend Engineer (Senior)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is Spring Batch and when would you use it instead of regular Spring Boot processing?

**Candidate:** Spring Batch is a lightweight, comprehensive batch processing framework for Java. It provides:
- **Chunk-oriented processing:** Read → Process → Write in configurable chunks
- **Job orchestration:** Restart, skip, retry, rollback capabilities
- **Partitioning:** Split processing across multiple threads or workers
- **ItemReaders/ItemWriters:** Built-in for files, databases, messaging, and more

Use Spring Batch when you need: transaction management for bulk operations, restartability (resume from failure), step-level scaling, or complex job orchestration. For simple file upload with no failure recovery, a regular Spring Boot service suffices.

**Interviewer:** Explain the chunk-oriented processing model.

**Candidate:** Chunk processing divides work into configurable-sized chunks:
1. **ItemReader** reads items one at a time until chunk size is reached
2. **ItemProcessor** processes each item (transform, validate, filter)
3. **ItemWriter** writes the entire chunk in a single transaction
4. After successful write, the transaction commits
5. On failure, the chunk rolls back and can be retried

This model provides efficient database batching while maintaining transaction boundaries. Default chunk size is configurable via `commit-interval`.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How would you design a batch job that processes 10M records from a CSV file?

**Candidate:** 

```java
@Bean
public Job processLargeFileJob(JobRepository jobRepository, Step processFileStep) {
    return new JobBuilder("processLargeFileJob", jobRepository)
        .start(processFileStep)
        .build();
}

@Bean
public Step processFileStep(JobRepository jobRepository, PlatformTransactionManager txManager) {
    return new StepBuilder("processFileStep", jobRepository)
        .<TransactionCsv, Transaction>chunk(5000, txManager) // 5000 records per chunk
        .reader(flatFileItemReader())
        .processor(transactionProcessor())
        .writer(jdbcBatchItemWriter())
        .listener(stepExecutionListener())
        .build();
}

@Bean
public FlatFileItemReader<TransactionCsv> flatFileItemReader() {
    return new FlatFileItemReaderBuilder<TransactionCsv>()
        .name("transactionItemReader")
        .resource(new FileSystemResource("/data/transactions.csv"))
        .delimited()
        .names("id", "amount", "currency", "timestamp")
        .linesToSkip(1) // header
        .maxItemCount(10_000_000) // limit
        .build();
}
```

**Performance considerations:**
- Chunk size of 5000 for transactional integrity
- `JdbcBatchItemWriter` with `assertUpdates=false` for performance
- Multi-threaded step with `TaskExecutor` for parallel processing
- Partitioning for large files (split file into multiple partitions)
- Skip policy for malformed records (log and continue)

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a batch ETL job that processes 100M records across multiple data sources with restartability and error handling.

**Candidate:** 

**Architecture:**
```
Job: Daily Data Sync
├── Step 1: Extract from Oracle (JdbcCursorItemReader)
│   └── Partitioned by REGION (10 partitions)
├── Step 2: Transform (ItemProcessor)
│   ├── Validate data
│   ├── Enrich with API data (calls REST endpoint)
│   └── Filter invalid records
├── Step 3: Load to PostgreSQL (JdbcBatchItemWriter)
│   └── Chunk size: 10000
├── Step 4: Generate report
└── Step 5: Send notification
```

**Restartability:**
```java
@Bean
public Job scalableEtlJob(JobRepository jobRepository, Step extractStep, Step loadStep) {
    return new JobBuilder("scalableEtlJob", jobRepository)
        .start(extractStep)
        .next(loadStep)
        .listener(jobExecutionListener())
        .build();
}
```

Spring Batch automatically saves `ExecutionContext` to the job repository after each chunk. If the job fails, it restarts from the last saved execution context (not from the beginning).

**Error handling:**
```java
@Bean
public Step extractStep(JobRepository jobRepository, PlatformTransactionManager txManager) {
    return new StepBuilder("extractStep", jobRepository)
        .<SourceRecord, TargetRecord>chunk(10000, txManager)
        .reader(databaseReader())
        .processor(validatingProcessor())
        .writer(batchWriter())
        .faultTolerant()
        .skip(ValidationException.class)
        .skipLimit(1000) // skip up to 1000 invalid records
        .retry(TransientDataAccessException.class)
        .retryLimit(3)
        .backOff(new ExponentialBackOffPolicy())
        .noRollback(ValidationException.class) // don't rollback entire chunk
        .listener(chunkListener())
        .build();
}
```

**Scaling with partitioning:**
```java
@Bean
@StepScope
public JdbcCursorItemReader<SourceRecord> partitionedReader(
        @Value("#{stepExecutionContext['region']}") String region) {
    // Each partition reads only its assigned region
}
```

---

## Interviewer Feedback

**Strengths:** Strong batch processing knowledge, practical partitioning, good error handling  
**Areas to Improve:** Could discuss Spring Batch's remote partitioning/chunking for distributed processing  
**Verdict:** Strong Hire

---

*Lab 18 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
