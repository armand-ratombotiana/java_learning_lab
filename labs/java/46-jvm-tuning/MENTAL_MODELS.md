# Mental Models for JVM Tuning

## Heap as a Water Tank
Think of the heap as a water tank with a drain (GC). `-Xmx` is the tank capacity. `-Xms` is the initial water level. `-Xmn` is the size of the top compartment (young generation). If the tank is too small, water overflows (OOM). If too large, water stagnates (long pauses). The drain rate (GC throughput) depends on drain design (collector).

## Code Cache as a Notebook
The code cache is a notebook where the JIT writes optimized recipes (compiled methods). If the notebook fills up, the JIT stops writing new recipes, and methods run using the cookbook (interpreter). A small notebook causes lost recipes; a large one wastes paper.

## Metaspace as a Library Archive
Metaspace is the library archive (class metadata). Every class you load is a book added to the archive. Without limits, the archive grows until it fills the building (native memory). Setting `MaxMetaspaceSize` is like setting a maximum number of shelves — once full, no more books can be added.

## Large Pages as Express Lanes
Regular 4 KB pages are like local roads with many intersections (TLB misses). Large 2 MB pages are like highways with few exits. For long journeys (large heap traversals), highways are much faster. For short trips (small heaps), local roads are fine.

## NUMA as a Warehouse Network
NUMA is like a company with warehouses in different cities. Each CPU socket is a warehouse with local inventory. Accessing local inventory (local memory) is fast; accessing remote warehouses (other sockets) requires shipping (slower). NUMA-aware allocation keeps inventory in the local warehouse.

## String Dedup as a Recycling Program
String dedup is like a city recycling program. When you put out cans (strings with identical content), the recycling center merges them into a single can (shared char[]). New residents who use the same can content get the recycled version instead of new raw materials.

## JVM Flags as an Airplane Cockpit
JVM flags are like the switches and dials in an airplane cockpit. Most have sensible defaults (autopilot mode). Some need adjustment for specific conditions (takeoff, landing). Changing random switches without understanding their effect can crash the plane. The JvmFlagReporter is the cockpit instrument panel showing current settings.
