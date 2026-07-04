# History of Distributed Locking

## Timeline
- **2006**: ZooKeeper (Yahoo) - distributed coordination
- **2013**: etcd (CoreOS) - distributed key-value store with locks
- **2014**: Redis Redlock proposed by Salvatore Sanfilippo
- **2016**: Martin Kleppmann criticizes Redlock in "How to do distributed locking"
- **2018**: etcd v3 with improved concurrency API

## Key Figures
- **Salvatore Sanfilippo**: Redis creator, Redlock proposer
- **Martin Kleppmann**: Distributed locking critic, fencing tokens advocate
- **ZooKeeper team**: Benjamin Reed, Flavio Junqueira

## Controversy
Redlock has been debated extensively: Martin Kleppmann argues it's unsafe without fencing tokens; Redis community defends it for practical use. Consensus: fencing tokens are essential for safety.
