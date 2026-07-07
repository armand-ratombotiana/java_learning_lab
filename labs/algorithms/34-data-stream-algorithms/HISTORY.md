# History of Data Stream Algorithms

1962: Donald Knuth described reservoir sampling in his book "The Art of Computer Programming" (the original algorithm for selecting a random sample from an unknown population).

1978: Alan Waterman independently rediscovered reservoir sampling. Jeffrey Vitter published the rigorous analysis in 1985.

1987: Philippe Flajolet and Nigel Martin published the first algorithm for counting distinct elements in a stream using log-space (the FM sketch). This was the foundation of what became HyperLogLog.

1996: Alon, Matias, and Szegedy published their seminal paper "The Space Complexity of Approximating the Frequency Moments," introducing the AMS algorithm and establishing the theoretical foundations of streaming algorithms.

2002: Jayadev Misra and David Gries published the Misra-Gries algorithm for frequent items, later rediscovered as the "frequent" algorithm.

2004: Graham Cormode and S. Muthukrishnan published the Count-Min Sketch, a simple and powerful data structure for frequency estimation.

2005: The Space-Saving algorithm for frequent items was introduced by Metwally, Agrawal, and El Abbadi, providing better accuracy than Misra-Gries for the same space.

2007: HyperLogLog by Flajolet and colleagues improved distinct counting using harmonic means, achieving 2% error with 1.5 KB of memory.

2010s: Streaming algorithms became standard in big data systems (Apache Spark Streaming, Apache Flink, Kafka Streams). Count-Min Sketch and HyperLogLog were added as built-in data structures in Redis and PostgreSQL.

2020s: Sliding window streaming and machine learning on streams are active research areas. Data stream algorithms are essential for IoT, real-time fraud detection, and network monitoring.