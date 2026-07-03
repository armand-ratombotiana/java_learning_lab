# History of the JVM

## Early Days (1991-1996)
- **1991**: James Gosling at Sun Microsystems starts the Green Project (Oak language)
- **1995**: Java 1.0a2 released with the first JVM
- **1996**: Java 1.0 officially released — interpreted only, no JIT, simple mark-sweep GC

## JIT Era (1997-2005)
- **1997**: Java 1.1 — JIT compilers introduced, improved memory model
- **1998**: Java 2 (1.2) — HotSpot JVM released by Sun (acquired from Longview Technologies)
- **2000**: Java 1.3 — HotSpot 2.0, faster JIT, improved GC
- **2002**: Java 1.4 — assert keyword, NIO, exception chain improvements
- **2004**: J2SE 5.0 (1.5) — generics, annotations, autoboxing, enhanced for loop, enum
- **2005**: HP and IBM release their own JVMs

## Modern JVM (2006-2014)
- **2006**: Java 6 — performance improvements, scripting support
- **2009**: Oracle acquires Sun Microsystems
- **2011**: Java 7 — G1 GC (experimental), invoke dynamic, NIO.2, fork/join
- **2014**: Java 8 — Lambda expressions, Streams API, Nashorn JavaScript engine, metaspace replaces permgen

## Recent Evolution (2017-present)
- **2017**: Java 9 — module system, G1 default GC, JShell
- **2018**: Java 11 — ZGC (experimental), Epsilon GC, flight recorder open sourced
- **2021**: Java 17 — sealed classes, pattern matching, ZGC production-ready (on most platforms)
- **2024**: Java 21 — virtual threads, record patterns, generational ZGC
- **2025**: Java 22-24 — continued improvements in GC, value types (Valhalla), Lilliput (smaller object headers)
