# AQS (AbstractQueuedSynchronizer) Framework

Welcome to the atomic mastery lab for the **AQS Framework**. This lab is part of the Java Academy's Concurrency module.

## 🧠 What You Will Master
- The internal state machine of AQS (the `volatile int state`).
- Exclusive vs. Shared mode acquisition.
- The CLH (Craig, Landin, and Hagersten) lock queue.
- How `ReentrantLock`, `Semaphore`, and `CountDownLatch` are implemented using AQS.
- Writing a custom synchronizer using AQS.

## 📂 Lab Structure
1. [THEORY.md](./THEORY.md) - The design philosophy of AQS.
2. [INTERNALS.md](./INTERNALS.md) - Deep dive into node queueing and unparking.
3. [CODE_DEEP_DIVE.md](./CODE_DEEP_DIVE.md) - Building a custom "One-Shot" Latch using AQS.