# Mathematical Foundation of CAP Theorem

## 📐 Formal Proof of CAP
In 2002, Seth Gilbert and Nancy Lynch of MIT published a formal proof of Brewer's CAP Conjecture.

### The Setup
Assume a distributed system consists of two nodes: $N_1$ and $N_2$.
- The network between $N_1$ and $N_2$ can drop messages arbitrarily (Partition Tolerance is required).
- A client $C$ can send read/write requests to either node.

### The Proof by Contradiction
Assume there exists a system that guarantees Consistency (C), Availability (A), and Partition Tolerance (P) simultaneously.

1. **The Partition**: A network partition occurs. All messages between $N_1$ and $N_2$ are dropped.
2. **The Write**: Client $C$ sends a write request $W(v_1)$ to $N_1$.
   - Because the system is Available (A), $N_1$ must process the write and return a success response, even though it cannot communicate with $N_2$.
   - Now, $N_1$ holds value $v_1$, but $N_2$ still holds the old value $v_0$.
3. **The Read**: Client $C$ sends a read request $R()$ to $N_2$.
   - Because the system is Available (A), $N_2$ must return a response.
   - Because $N_2$ never received the update from $N_1$ (due to the partition), it returns $v_0$.
4. **The Contradiction**: The client wrote $v_1$ but subsequently read $v_0$. This violates the definition of linearizable Consistency (C), which requires every read to receive the most recent write.

Therefore, a system cannot simultaneously guarantee C, A, and P.

## 🔄 PACELC Theorem
CAP is often criticized for being too simplistic because it only describes the system's behavior *during a network partition*. What happens when the network is running normally?

In 2010, Daniel Abadi proposed the **PACELC Theorem**, which expands CAP:
- If there is a **P**artition (**P**), how does the system trade off **A**vailability and **C**onsistency (**A** vs **C**)?
- **E**lse (**E**), when the system is running normally, how does it trade off **L**atency and **C**onsistency (**L** vs **C**)?

### PACELC Examples
- **DynamoDB / Cassandra (PA/EL)**: During a partition, they choose Availability. During normal operation, they choose lower Latency over strict Consistency (by not waiting for all replicas to acknowledge a write).
- **MongoDB (PC/EC)**: During a partition, they choose Consistency (by electing a new primary or rejecting writes). During normal operation, they choose Consistency (all reads go to the primary node, increasing latency).