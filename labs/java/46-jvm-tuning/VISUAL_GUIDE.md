# Visual Guide to JVM Tuning

## Heap Layout
```
┌───────────────────────────────────────────┐
│ Heap (Xms = initial, Xmx = max)           │
│ ┌──────────────────┬────────────────────┐ │
│ │ Young (Xmn)      │ Old               │ │
│ │ ┌────┬────┬────┐ │                    │ │
│ │ │Eden│ S0 │ S1 │ │                    │ │
│ │ └────┴────┴────┘ │                    │ │
│ │ SurvivorRatio=8  │ NewRatio=2         │ │
│ └──────────────────┴────────────────────┘ │
└───────────────────────────────────────────┘
```

## Code Cache Division
```
┌─────────────────────────────────────────┐
│ Code Cache (ReservedCodeCacheSize=240M) │
│ ┌──────────┬──────────┬────────────────┐│
│ │Non-nmethod│Profiled  │ Non-profiled   ││
│ │ (3MB)    │ (12MB)   │ (120MB)        ││
│ │stubs,    │C1 code   │ C2 code        ││
│ │adapters  │          │                ││
│ └──────────┴──────────┴────────────────┘│
└─────────────────────────────────────────┘
```

## Metaspace Layout
```
┌─────────────────────────────────────────┐
│ Native Memory                             │
│ ┌─────────────────────────────────────┐  │
│ │ Metaspace (MaxMetaspaceSize=256m)   │  │
│ │ ┌─────┬─────┬─────┬─────┬─────┐    │  │
│ │ │ Chk1│ Chk2│ Chk3│ ... │ ChkN│    │  │
│ │ │ 2M  │ 2M  │ 4M  │     │ 2M  │    │  │
│ │ └─────┴─────┴─────┴─────┴─────┘    │  │
│ └─────────────────────────────────────┘  │
│ ┌─────────────────────────────────────┐  │
│ │ Compressed Class Space (1GB)        │  │
│ └─────────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

## String Dedup Flow
```
String object (char[] "hello")      String object (char[] "hello")
         ↓                                    ↓
    Hash("hello") = 0x1234          Hash("hello") = 0x1234
         ↓                                    ↓
    Dedup table lookup              Dedup table lookup
         ↓                                    ↓
    Not found → add entry           Found → share char[] reference
```

## NUMA Architecture
```
Socket 0                    Socket 1
┌─────────────┐            ┌─────────────┐
│ Core 0-15   │            │ Core 16-31  │
│ Memory (local)│ ← slow → │ Memory (local)│
└─────────────┘            └─────────────┘
```
