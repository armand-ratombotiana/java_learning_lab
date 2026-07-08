# Quiz: Video Streaming Design

## Instructions
Answer 25 questions to test your knowledge. Each has one correct answer.

## Section 1: Fundamentals (Q1-8)

**1.** What is the primary characteristic of Video Streaming Design?
- A) Eventual consistency  B) Scalability  C) Fault tolerance  D) All of the above

**2.** Which design principle is most important?
- A) Premature optimization  B) Separation of concerns  C) Duplication  D) Tight coupling

**3.** What does CAP theorem stand for?
- A) Consistency, Availability, Partition Tolerance
- B) Cost, Accessibility, Performance
- C) Cache, API, Protocol
- D) Concurrency, Atomicity, Persistence

**4.** Which Java 21+ feature is most useful?
- A) Virtual threads  B) Records  C) Pattern matching  D) All of the above

**5.** What is the main trade-off?
- A) Speed vs accuracy  B) Consistency vs availability  C) Cost vs features  D) Security vs usability

**6.** Which consistency model is strongest?
- A) Eventual  B) Strong  C) Causal  D) Read-your-writes

**7.** What is a load balancer's role?
- A) Encrypt data  B) Distribute traffic  C) Store data  D) Authenticate users

**8.** Which is NOT a scaling strategy?
- A) Vertical  B) Horizontal  C) Diagonal  D) Auto

## Section 2: Implementation (Q9-16)

**9.** Time complexity of consistent hashing lookup?
- A) O(1)  B) O(log n)  C) O(n)  D) O(n²)

**10.** Best concurrent structure for high-read workloads?
- A) ConcurrentHashMap  B) CopyOnWriteArrayList  C) Hashtable  D) Synchronized List

**11.** What pattern prevents cascading failures?
- A) Circuit breaker  B) Singleton  C) Factory  D) Observer

**12.** Purpose of dead letter queue?
- A) Store succeeded messages  B) Hold failed messages  C) Cache data  D) Route messages

**13.** Spring DI annotation?
- A) @Inject  B) @Autowired  C) @Resource  D) All of the above

**14.** How to handle concurrent modifications?
- A) Synchronized  B) Optimistic locking  C) Pessimistic locking  D) Version vectors

**15.** Unit testing framework?
- A) JUnit 5  B) TestNG  C) Spock  D) All of the above

**16.** Tool for distributed tracing?
- A) OpenTelemetry  B) Log4j  C) JMeter  D) Gatling

## Section 3: Advanced (Q17-25)

**17.** Cache hit ratio formula?
- A) hits / total requests  B) misses / total  C) cache / working set  D) working / total

**18.** Algorithm for leader election?
- A) Paxos  B) Raft  C) Zab  D) All of the above

**19.** Purpose of health check?
- A) Return metrics  B) Verify operational  C) Rotate logs  D) Flush caches

**20.** How rate limiting protects?
- A) Prevents corruption  B) Limits request rate  C) Encrypts data  D) Balances load

**21.** REST vs gRPC difference?
- A) REST=HTTP, gRPC=HTTP/2  B) REST=sync, gRPC=async  C) REST=JSON, gRPC=Protobuf  D) All of the above

**22.** Pattern for read/write separation?
- A) CQRS  B) Saga  C) Event sourcing  D) BFF

**23.** Purpose of retry?
- A) Handle transient failures  B) Increase throughput  C) Reduce latency  D) Improve security

**24.** System throughput measurement?
- A) P95 latency  B) Requests/second  C) Error rate  D) CPU utilization

**25.** Final optimization step?
- A) Implement  B) Measure & verify  C) Deploy  D) Document

## Answer Key
1-D, 2-B, 3-A, 4-D, 5-B, 6-B, 7-B, 8-C, 9-B, 10-B,
11-A, 12-B, 13-D, 14-D, 15-D, 16-A, 17-A, 18-D, 19-B,
20-B, 21-D, 22-A, 23-A, 24-B, 25-B
