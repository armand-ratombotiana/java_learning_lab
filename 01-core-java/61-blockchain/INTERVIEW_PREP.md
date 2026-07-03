# Module 61: Blockchain & Web3 in Java - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is a Smart Contract, and why is it executed deterministically?
**Answer**:
A Smart Contract is self-executing code deployed on a blockchain (like the Ethereum Virtual Machine). It contains the business rules of an agreement.
It must be **deterministic**, meaning given the same input, it must *always* produce the exact same output, no matter which node in the world executes it. If it were non-deterministic (e.g., if it relied on `Math.random()` or reading an external HTTP API like the current weather), Node A might execute it and get a different result than Node B. This would completely break the consensus protocol of the blockchain, as the network relies on all nodes independently verifying and agreeing on the exact resulting state.

### Q2: Why is modifying data on a blockchain (like Ethereum) fundamentally different from modifying data in a SQL database?
**Answer**:
In a SQL database, a client opens a connection, executes an `UPDATE` statement, and the database modifies the row synchronously.
In a blockchain, the database is an immutable, append-only log. You do not modify data directly. Instead, you cryptographically sign a **Transaction** with your private key and broadcast it to the network peer-to-peer pool (the Mempool). You then have to wait. Miners or Validators pick up your transaction, execute the smart contract logic, and bundle the resulting state changes into a new "Block," which is then appended to the chain. This process is inherently asynchronous, can take anywhere from seconds to minutes, and costs a transaction fee (Gas).

### Q3: What is "Gas" in Ethereum, and how does it prevent the Halting Problem?
**Answer**:
The Halting Problem states that it is impossible for a program to analyze another program and definitively determine if it will finish running or get stuck in an infinite loop. 
Because the EVM is Turing-complete (you can write `while(true)` loops), a malicious actor could deploy an infinite loop, freezing every node on the network.
To solve this, Ethereum uses **Gas**. Every single operation (addition, variable assignment, database write) costs a specific fraction of a token. When you submit a transaction, you provide a Gas limit and pay upfront. If your code hits an infinite loop, it rapidly consumes all the provided Gas. Once the Gas runs out, the EVM immediately terminates the execution, rolls back any state changes, and keeps the fee as a penalty.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: The Oracle Problem
**Problem**: An interviewer presents the following architecture: "We are writing a Java Smart Contract (using Hyperledger Fabric or interacting via Web3j) for a crop insurance company. The contract says: 'If it doesn't rain in Kansas for 30 days, automatically pay out $10,000 to the farmer.' The smart contract needs to check the weather. How do you design this?"

**Solution**:
This explores **The Oracle Problem**. As stated in Q1, smart contracts cannot make outbound HTTP calls to a weather API because that breaks determinism (the API might go down, or return different data to different nodes).
**The Fix**: You must use an **Oracle**. An Oracle is a trusted off-chain entity (often written in Java, Node, or Go) that fetches real-world data (the weather) and *pushes* it onto the blockchain by executing a transaction. 
1. The Java Oracle service runs on AWS, queries the Weather API daily, and submits a signed transaction to the Smart Contract: `updateWeather("Kansas", "Sunny")`.
2. The Smart Contract stores this state.
3. The Smart Contract logic evaluates the internal state, and if 30 days of "Sunny" are recorded, it executes the payout.