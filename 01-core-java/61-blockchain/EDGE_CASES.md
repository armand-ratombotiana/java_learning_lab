# Module 61: Blockchain & Web3 in Java - Edge Cases & Pitfalls

---

## Pitfall 1: Storing PII on a Public Blockchain

### ❌ Wrong
Writing a Java application that creates a smart contract containing Personally Identifiable Information (PII) like names, email addresses, or medical records, and publishing it to Ethereum.

### ✅ Correct
Blockchains are immutable and public by design. Data cannot be deleted. Storing PII on a public blockchain is a massive violation of privacy laws like GDPR (the "Right to be Forgotten"). 
Only store hashes or cryptographic proofs on the blockchain. Store the actual sensitive PII in a traditional, secure, off-chain database.

---

## Pitfall 2: Treating the Blockchain like a Relational Database

### ❌ Wrong
Using Web3j to constantly query historical states, run complex aggregations, or loop through thousands of blocks to calculate a sum, assuming it behaves like a SQL database.

### ✅ Correct
Blockchains are extremely slow to query across historical blocks. If you need to perform complex queries on blockchain data, implement an indexer (like The Graph) or write a Java service that listens to Blockchain Events as they occur and projects them into an off-chain relational database (CQRS pattern) where they can be queried instantly.

---

## Pitfall 3: Ignoring Gas Costs in Smart Contract Interaction

### ❌ Wrong
Designing a Java backend that issues a massive number of state-changing transactions to Ethereum on behalf of users, treating them like free database inserts.

### ✅ Correct
Every state-changing transaction on Ethereum costs "Gas" (transaction fees paid in cryptocurrency). Inefficient loops or excessive writes can cost the company thousands of real-world dollars very quickly. Optimize smart contract code to minimize writes, batch transactions where possible, or use Layer 2 scaling solutions to reduce costs.