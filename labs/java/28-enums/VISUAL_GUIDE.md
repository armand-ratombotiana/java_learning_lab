# Visual Guide: Enums

## Enum Memory Layout
```
Enum Constant (Color.RED)
┌──────────────────────────┐
│ Object Header (12-16B)   │
├──────────────────────────┤
│ Enum.name = "RED"        │ ← inherited from java.lang.Enum
│ Enum.ordinal = 0         │
│ Custom fields (if any)   │
└──────────────────────────┘
```

## EnumSet Internal Bitmask
```
Enum: Day { MON, TUE, WED, THU, FRI, SAT, SUN }
Ordinal:  0    1    2    3    4    5    6

EnumSet.of(MON, WED, FRI):
Bitmask: 0b0010101
            │││││││
            ││││││└─ MON (bit 0)
            │││││└── TUE (bit 1)
            ││││└─── WED (bit 2)
            │││└──── THU (bit 3)
            ││└───── FRI (bit 4)
            │└────── SAT (bit 5)
            └─────── SUN (bit 6)
```

## EnumMap Internal Array
```
EnumMap<Day, String>:
Index (ordinal): 0    1     2     3     4     5     6
                ┌─────┬─────┬─────┬─────┬─────┬─────┬─────┐
                │"Mon"│ null│"Wed"│ null│"Fri"│ null│ null│
                └─────┴─────┴─────┴─────┴─────┴─────┴─────┘
                  MON   TUE   WED   THU   FRI   SAT   SUN
```

## Enum State Machine
```
Status enum as finite state machine:
    ┌─────────┐
    │  PENDING │
    └────┬─────┘
         │ submit()
         ▼
    ┌─────────┐
    │ ACTIVE  │
    └────┬─────┘
         │ complete()        │ fail()
         ▼                    ▼
    ┌─────────┐        ┌─────────┐
    │COMPLETED│        │ FAILED  │
    └─────────┘        └─────────┘
```
