# Visual Guide to Java 21 Features

## Virtual Thread Architecture

```
┌─────────────────────────────────────────────────────┐
│            VIRTUAL THREAD ARCHITECTURE              │
├─────────────────────────────────────────────────────┤
│                                                     │
│   ┌──────────┐   ┌──────────┐   ┌──────────┐      │
│   │ Virtual  │   │ Virtual  │   │ Virtual  │      │
│   │ Thread 1 │   │ Thread 2 │   │ Thread N │      │
│   └────┬─────┘   └────┬─────┘   └────┬─────┘      │
│        │              │              │             │
│        └──────────────┼──────────────┘             │
│                       ▼                             │
│              ┌──────────────┐                       │
│              │  Carrier     │  (Platform Thread)   │
│              │  Thread Pool │                       │
│              └──────┬───────┘                       │
│                     │                               │
│                     ▼                               │
│              ┌──────────────┐                       │
│              │  OS Thread   │                       │
│              └──────────────┘                       │
│                                                     │
│   Virtual threads are cheap to create              │
│   Thousands can share few carrier threads         │
└─────────────────────────────────────────────────────┘
```

## Record Pattern Matching

```
Object: Box(Point(5, 10), 20)

┌─────────────────────────────────────────────────────┐
│  obj instanceof Box(Point(int x, int y), int w)    │
├─────────────────────────────────────────────────────┤
│                                                     │
│  Box ───► Match!                                   │
│    │                                               │
│    └──► Point(int x, int y) ──► x=5, y=10         │
│         └──► int w = 20                            │
│                                                     │
│  Now use: x=5, y=10, w=20 in scope                 │
└─────────────────────────────────────────────────────┘
```

## String Template Syntax

```
┌─────────────────────────────────────────────────────┐
│  STR."text \{expression} more text"                │
│         │           │                               │
│         │           └── Java expression            │
│         └── Template processor                     │
└─────────────────────────────────────────────────────┘

Example:
STR."Hello \{name}"        → "Hello Alice"
STR."\{a} + \{b} = \{a+b}"  → "5 + 10 = 15"
```