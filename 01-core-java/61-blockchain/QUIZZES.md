# Module 61: Blockchain & Web3 in Java - Quizzes

---

## Q1: Immutability
How does a blockchain achieve immutability?

A) By using a central administrator to lock the database.
B) By ensuring each block contains the cryptographic hash of the previous block, meaning any tampering changes the hash and invalidates the entire subsequent chain.
C) By storing data only in RAM.
D) By writing data in read-only Java files.

**Answer**: B
**Explanation**: The cryptographic linking of blocks is the core mechanism of blockchain immutability. A change to a single byte in Block 1 changes its Hash. Block 2 holds the old Hash, so the connection is broken. The network immediately rejects the altered chain.

---

## Q2: Public vs Enterprise Blockchain
What is the primary difference between Ethereum and Hyperledger Fabric?

A) Ethereum is for writing smart contracts; Hyperledger Fabric is only for storing images.
B) Ethereum is a public, permissionless blockchain where anyone can read/write data; Hyperledger Fabric is a private, permissioned blockchain designed for enterprise consortiums.
C) Ethereum uses Java; Hyperledger uses Python.
D) There is no difference; they are the exact same technology.

**Answer**: B
**Explanation**: Enterprises (like a consortium of banks or shipping companies) often cannot put sensitive business logic on a public chain like Ethereum. Hyperledger Fabric allows them to create closed, identity-verified blockchain networks while still achieving decentralized trust.

---

## Q3: Web3j
What is the primary purpose of the `Web3j` library in the Java ecosystem?

A) To create web browsers.
B) To mine Bitcoin on the JVM.
C) To act as an integration layer, providing type-safe Java wrappers to communicate with Ethereum smart contracts and nodes via JSON-RPC.
D) To replace Spring Boot.

**Answer**: C
**Explanation**: Web3j generates Java wrapper classes from Ethereum Smart Contracts (written in Solidity), allowing Java developers to interact with the blockchain using standard Java objects and methods instead of crafting manual HTTP JSON-RPC calls.