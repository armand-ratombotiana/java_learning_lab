# Visual Guide to Garbage Collection

## Generational Heap Layout
```
┌──────────────────────────────────────────────────────┐
│                      Heap                             │
│ ┌──────────────────────┬─────────────────────────┐   │
│ │   Young Generation   │   Old Generation         │   │
│ │ ┌────┬────┬────────┐ │                         │   │
│ │ │Eden│S0  │  S1    │ │                         │   │
│ │ └────┴────┴────────┘ │                         │   │
│ └──────────────────────┴─────────────────────────┘   │
└──────────────────────────────────────────────────────┘
```

## G1 Region Map
```
┌────┬────┬────┬────┬────┬────┬────┬────┐
│ E  │ E  │ S  │ O  │ O  │ H  │ O  │ E  │
├────┼────┼────┼────┼────┼────┼────┼────┤
│ O  │ E  │ E  │ O  │ A  │ O  │ O  │ S  │
├────┼────┼────┼────┼────┼────┼────┼────┤
│ E  │ O  │ O  │ O  │ A  │ A  │ E  │ E  │
└────┴────┴────┴────┴────┴────┴────┴────┘
Regions: E=Eden, S=Survivor, O=Old, H=Humongous, A=Available
```

## ZGC Colored Pointer
```
64-bit Pointer:
┌─────────┬───────┬────────────────────────────┐
│ Color   │ Resvd │  Pointer (42-bit address)   │
│ (4 bits)│(3 bits)│                             │
└─────────┴───────┴────────────────────────────┘
   Bits 47-44  43     Bits 0-42
```

## G1 Marking Cycle Phases
```
Concurrent Mark Cycle:
┌───────┬─────────────────┬────────┬─────────┐
│InitMak│ Concurrent Mark │Remark  │ Cleanup │
│ STW   │ (application)   │ STW    │ STW     │
│ 1ms   │ 10-100ms        │ 10ms   │ 1ms     │
└───────┴─────────────────┴────────┴─────────┘
```

## GC Pause Times by Collector
```
Pause (ms): 0.1    1      10     100    1000
Serial       │──────────────│────────────────
Parallel     │──────│
G1           │──│
ZGC          │
Shenandoah   │
```
