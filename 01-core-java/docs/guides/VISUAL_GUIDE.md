# Visual Guide to Core Java

## Class Structure

```
┌─────────────────────────────────────┐
│           Class Name                 │
├─────────────────────────────────────┤
│  Fields/Properties                  │
│  - instance variables               │
│  - static variables                 │
├─────────────────────────────────────┤
│  Methods                            │
│  - instance methods                 │
│  - static methods                   │
│  - constructors                     │
├─────────────────────────────────────┤
│  Nested Classes                     │
└─────────────────────────────────────┘
```

## Memory Layout

```
┌─────────────────────────────────────────────┐
│  HEAP                                        │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐        │
│  │ Object  │ │ Object  │ │ Object  │  ...   │
│  └─────────┘ └─────────┘ └─────────┘        │
├─────────────────────────────────────────────┤
│  STACK                                       │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐     │
│  │ Frame 1 │ │ Frame 2 │ │ Frame 3 │       │
│  └──────────┘ └──────────┘ └──────────┘     │
└─────────────────────────────────────────────┘
```

## Execution Flow

Source → Compiler → Bytecode → JVM → Native Code