# Module 69: Data Governance & Privacy - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is Data Lineage, and why is it legally necessary?
**Answer**:
Data Lineage tracks the origin of data, the systems it passes through, the transformations applied to it, and where it ultimately resides.
Legally, under regulations like GDPR or CCPA, if a user requests a copy of all their data, or asks to be forgotten, the company must know exactly where that data is located. If the company lacks data lineage, they might delete the user from the primary PostgreSQL database, but unknowingly leave the user's data sitting in an unmonitored Elasticsearch cache or an S3 data lake. Data Lineage (tracked by tools like Apache Atlas) guarantees that data architects have a complete, auditable map of data movement.

### Q2: How does K-Anonymity protect data privacy?
**Answer**:
K-Anonymity is a mathematical property of a dataset. A dataset is *k*-anonymous if the information for any single person in it cannot be distinguished from at least *k* - 1 other individuals.
For example, if you blur a dataset so that you only record "Gender: Male, ZIP: 90210, Age: 30-40", and there are at least 5 men in that zip code within that age range, the dataset has k-anonymity of 5. Even if an attacker cross-references this dataset with public voter records, they can only narrow down a medical record to a group of 5 people, preserving the individual's anonymity.

### Q3: What is "Crypto-Shredding"?
**Answer**:
Crypto-shredding is a technique used to permanently "delete" data in immutable systems (like Blockchains, Kafka event logs, or write-once HDFS clusters) where physically erasing a disk block is impossible.
Instead of storing PII in plaintext, it is encrypted using a unique, user-specific encryption key before being written to the immutable log. The encryption keys are stored in a centralized, highly secure, mutable database (like AWS KMS or HashiCorp Vault). 
When a user requests the Right to be Forgotten, the company simply deletes that user's unique key from the Key Management System. The encrypted data remains in the immutable log forever, but without the key, it is mathematically impossible to read, satisfying legal deletion requirements.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: The Tokenization Service
**Problem**: An interviewer presents the following architecture: "We are building an E-Commerce platform. The Order Service, Shipping Service, and Billing Service all need to handle Credit Card numbers. How do you design this so that we minimize our PCI-DSS compliance scope?"

**Solution**:
You absolutely **do not** allow the Order Service or Shipping Service to touch raw Credit Card numbers. If they do, they fall into PCI-DSS scope, meaning they must undergo extremely rigorous and expensive security audits.
**The Fix: Centralized Tokenization**.
1. Create a highly isolated, heavily audited **Tokenization Service** (Vault).
2. When the user checks out, the Frontend sends the raw Credit Card directly to the Tokenization Service via an encrypted channel.
3. The Tokenization Service stores the raw card in an encrypted database and generates a random, meaningless string (a Token, e.g., `TKN-998877`).
4. It returns this Token to the Frontend.
5. The Frontend passes the Token to the Order, Shipping, and Billing services.
6. These downstream services only ever store and process the Token. They are now completely out of PCI-DSS scope because the Token is mathematically meaningless to a hacker.
7. Only the external Payment Gateway (like Stripe) is allowed to receive the raw card. The Billing service passes the Token to the Payment Gateway, which resolves it internally.