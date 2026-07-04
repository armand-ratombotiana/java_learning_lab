# History: Bloom Filters

## Invention (1970)

- **1970**: **Burton H. Bloom** published "Space/Time Trade-offs in Hash Coding with Allowable Errors" in the Communications of the ACM. He was a researcher at **Computer Usage Company** in Waltham, Massachusetts.
- Bloom's original paper described the filter as a method for reducing the cost of an external store by using a small in-memory filter.

## Early Applications (1980s)

- **1980s**: Used in dictionary systems for spelling correction
- Limited adoption due to the specialized nature of the idea

## Rediscovery (1990s)

- **1990s**: The internet boom created systems dealing with billions of elements
- Bloom filters were rediscovered as a solution to scaling problems
- **1997**: **Pei Cao** proposed using Bloom filters for Web cache sharing (Summary Cache)

## Wide Adoption (2000s)

- **2000**: **Fan et al.** proposed **Counting Bloom filters** for web cache sharing
- **2002**: **Broder & Mitzenmacher** published "Network Applications of Bloom Filters: A Survey" — the definitive reference
- **2004**: **Google Bigtable** paper (Chang et al.) described using Bloom filters to reduce disk lookups
- **2006**: **Cassandra** (Facebook) and **Dynamo** (Amazon) papers described Bloom filter use
- **2008**: **Satoshi Nakamoto** proposed Bloom filters for Bitcoin SPV (Simplified Payment Verification)
- **2009**: Guava library added `BloomFilter` implementation

## Modern Era (2010s–present)

- **2014**: **Cuckoo filter** (Fan et al.) — higher space efficiency, supports deletion
- **2015**: **Bloomier filters** — extend Bloom filters to map keys to values
- **2020s**: **Xor filters** — more space-efficient than Bloom filters (static sets)
- Continued research into compressed, scalable, and distributed Bloom filters

Java Bloom filter implementations exist in:
- **Guava**: `BloomFilter` (production-ready)
- **Apache Commons**: `BloomFilter` (added in Commons Collections 4.5)
- **Orestes Bloom Filter** library: concurrent, scalable variants
