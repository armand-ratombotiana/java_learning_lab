# Module 69: Data Governance & Privacy - Quizzes

---

## Q1: GDPR "Right to be Forgotten"
Which technical pattern solves the problem of honoring a GDPR data deletion request in an immutable, append-only system like an Event Sourced database or Apache Kafka?

A) Compaction
B) Two-Phase Commit (2PC)
C) Crypto-Shredding
D) MapReduce

**Answer**: C
**Explanation**: Crypto-shredding involves encrypting the PII before writing it to the immutable log. To "delete" the data, you simply delete the unique decryption key from the central Key Management Service. The data in the log becomes mathematically unreadable forever, satisfying privacy regulations.

---

## Q2: PII in Logs
How do enterprise Java applications typically prevent sensitive customer information (like credit card numbers) from being exposed in centralized log management systems (like Datadog or Splunk)?

A) By turning off logging in production entirely.
B) By using a log masking or obfuscation filter in the logging framework (e.g., Logback or Log4j2) that uses Regular Expressions to detect and replace sensitive patterns (e.g., replacing credit card numbers with `XXXX-XXXX-XXXX-1234`) before the log is output.
C) By encrypting the entire log file with AES-256.
D) By manually checking the logs every morning.

**Answer**: B
**Explanation**: Mistakes happen, and developers might accidentally log full Java objects. A central masking filter acts as a safety net, intercepting log payloads and scrubbing known PII patterns before they leave the JVM.

---

## Q3: Re-identification Risk
What is the primary danger of "Pseudo-anonymization" (e.g., simply deleting the 'Name' column from a dataset)?

A) It takes up too much disk space.
B) It corrupts the database indexes.
C) The remaining data points (like ZIP code, gender, and birth date) can often be cross-referenced with public databases to perfectly re-identify the individuals, violating privacy laws.
D) It prevents machine learning models from compiling.

**Answer**: C
**Explanation**: Harvard studies have shown that 87% of the US population can be uniquely identified using only ZIP code, gender, and date of birth. True anonymization requires techniques like blurring (making data less specific) to ensure individuals hide within a larger crowd.