# Interview Questions: Phaser

## Company-Specific Focus

### Google
- Phaser: flexible barrier synchronization, reusable, supports dynamic party registration
- Phase advancement: parties arrive and advance to next phase
- Tiered phasing: phaser tree for large-scale coordination

### Microsoft
- Phaser vs CountDownLatch/CyclicBarrier: Phaser combines both features
- Dynamic registration: parties can register/deregister on the fly

### Amazon
- Phaser for multi-stage parallel computation: each phase is a stage
- Bulk registration: bulkRegister for batch party addition
- Repeated use: unlike CountDownLatch, Phaser can be reused

### Meta
- arrive() vs arriveAndAwaitAdvance(): difference in synchronization behavior
- onAdvance(): called when phase advances, can terminate phaser
- getPhase(): current phase number

### Apple
- Phaser tree: hierarchical phasers for large-scale coordination
- Registration: register() adds a new unarrived party

### Oracle
- java.util.concurrent.Phaser specification
- Registration: parties registered for synchronization
- Termination: phaser can be terminated, getPhase returns negative

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1114 Print in Order | Easy | Google, Amazon, Apple | Phaser barrier for ordering |
| 1115 Print FooBar Alternately | Medium | Amazon, Google | Phaser two-party coordination |
| 1116 Print Zero Even Odd | Medium | Google, Microsoft | Multi-party phaser |

## Real Production Scenarios
- **LinkedIn**: Phaser for multi-stage data processing pipeline with dynamic worker pool

## Interview Patterns & Tips
- **Phases**: multiple phases of barrier synchronization
- **Registration**: dynamic party management
- **Reusable**: unlike CyclicBarrier, parties can re-register
- **Tree**: hierarchical phaser for large-scale coordination

## Deep Dive Questions
- **Barrier vs Phaser**: How does Phaser differ from CyclicBarrier?
- **Dynamic registration**: What is the internal mechanism for dynamic party addition?
- **Tiering**: How does Phaser tree coordination work?
- **onAdvance**: How is the onAdvance method called and what can it do?
- **Termination**: When does a Phaser terminate?