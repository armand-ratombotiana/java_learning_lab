# Quizzes: AI Observability & Productionizing LLMs

Test your knowledge on deploying, monitoring, and securing AI applications in production.

## Quiz 1: Observability and Metrics

**Q1: Why is tracking token usage critical in a production LLM application?**
- A) To ensure the application compiles correctly.
- B) To monitor and manage the financial cost of API calls, as providers bill per token.
- C) To speed up the database queries.
- D) Because Spring Boot requires it to start.
*Answer: B*

**Q2: Which Spring AI feature allows you to intercept requests and responses to log the exact prompt and completion?**
- A) `VectorStore`
- B) `DocumentReader`
- C) `ChatClient` Advisors (e.g., `SimpleLoggerAdvisor`)
- D) `TextSplitter`
*Answer: C*

## Quiz 2: Resilience and Reliability

**Q1: If the OpenAI API goes down and starts returning 500 Server Errors, what is the best architectural pattern to prevent your application from crashing?**
- A) A `while(true)` loop that retries until it works.
- B) A Circuit Breaker (e.g., via Resilience4j) to fail fast and provide a fallback response.
- C) Restarting the Spring Boot application.
- D) Switching from JSON to XML.
*Answer: B*

**Q2: How can you mitigate the risk of an LLM provider experiencing sudden, massive latency spikes (e.g., taking 30 seconds to respond)?**
- A) Use synchronous blocking calls to ensure the thread waits for the answer.
- B) Use reactive programming (`Flux`) to stream the response and implement aggressive timeouts.
- C) Increase the `max_tokens` limit.
- D) Add more RAM to the server.
*Answer: B*

## Quiz 3: Security and Edge Cases

**Q1: What is a critical security risk when logging full LLM prompts and responses in a centralized logging system?**
- A) The logs might consume too much disk space.
- B) The LLM might learn from the logs.
- C) Inadvertently logging Personally Identifiable Information (PII) or sensitive data, violating compliance (GDPR/HIPAA).
- D) The logging system might crash the LLM provider.
*Answer: C*