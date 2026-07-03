# Mental Models for JVM Internals

## The Factory Model
The JVM is like a factory:
- **Class Loader**: Receiving dock that brings in raw materials (class files)
- **Method Area**: Blueprint storage (class metadata, bytecode)
- **Heap**: Main warehouse for products (objects)
- **Stack**: Assembly line for each worker (thread)
- **PC Register**: Each worker's current step in the assembly manual
- **GC**: Janitorial team that clears out unused inventory
- **JIT**: Automation engineer that replaces manual steps with machines

## The City Model
The JVM is a city:
- **Metaspace**: City hall (class metadata, permanently needed)
- **Heap**: Residential and commercial zones (all objects)
- **Young Generation**: New development area (recently built objects survive here)
- **Old Generation**: Established neighborhoods (objects that survived multiple GCs)
- **GC**: Garbage trucks that collect trash
- **JIT**: City planners optimizing traffic flow (code paths)
- **Threads**: Workers commuting between zones

## The Library Model
The JVM is a library:
- **Library shelves**: Method area (all class information)
- **Books**: Classes (loaded on demand)
- **Reading rooms**: Thread stacks (each reader has their own space)
- **PC Register**: Bookmark showing current page
- **Checkout desk**: Class loader (processes book requests)
- **Return cart**: GC (returns unused books to shelves)
