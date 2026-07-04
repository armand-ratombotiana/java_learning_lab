# History of Arrays

## Pre-History (1940s–1950s)

Arrays emerged with stored-program computers. **EDSAC** (1949) used sequential memory addresses — programmers manually calculated offsets. **FORTRAN** (1957) introduced the first array syntax: `DIMENSION A(100)`. John Backus's team gave programmers the ability to name a block of memory and index into it.

## The C Era (1970s)

C's array model (Dennis Ritchie, 1972) defined `a[i]` as syntactic sugar for `*(a + i)`. This exposed the raw pointer arithmetic view and made array/pointer interchangeability a language feature. The lack of bounds checking was a trade-off for performance.

## Java Arrays (1995–present)

James Gosling's Java team made several deliberate choices:
- Arrays are objects with a `length` field
- Bounds checking at runtime (throwing `ArrayIndexOutOfBoundsException`)
- Array covariance for flexibility
- No multi-dimensional arrays — only arrays of arrays (jagged)

## Dynamic Arrays

- **1979**: `Vector` class in Smalltalk-80
- **1995**: `java.util.Vector` — synchronized dynamic array
- **1998**: `java.util.ArrayList` — unsynchronized, faster alternative
- **2004**: Java 5 generics — `ArrayList<String>` instead of raw `ArrayList`

## Modern Era

- **Java 8+**: `Arrays.parallelPrefix`, `Arrays.parallelSetAll` for parallel operations
- **Project Valhalla** (in development): value types may allow `List<int>` without boxing
- **Foreign-Memory Access** (Java 14+): `MemorySegment` for off-heap array-like structures
