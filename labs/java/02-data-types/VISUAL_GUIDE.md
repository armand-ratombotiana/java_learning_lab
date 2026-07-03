# Data Types — Visual Guide

## Type Hierarchy

```
Java Types
├── Primitive Types (stored by value)
│   ├── byte     (8 bits)  ──┐
│   ├── short    (16 bits) ──┤
│   ├── int      (32 bits) ──┤── Integer types
│   ├── long     (64 bits) ──┘
│   ├── float    (32 bits) ──┐
│   ├── double   (64 bits) ──┤── Floating-point types
│   ├── char     (16 bits)    ── Character type (unsigned)
│   └── boolean  (JVM-dep.)   ── Boolean type
│
└── Reference Types (stored by reference)
    ├── Classes (String, ArrayList, etc.)
    ├── Interfaces (List, Runnable, etc.)
    ├── Arrays (int[], String[][])
    ├── Enums (DayOfWeek)
    └── Records (Point)
```

## Memory Layout Comparison

```
Primitive (stack/local):
┌──────────┐
│   value  │  e.g., int = 4 bytes
└──────────┘

Reference (stack + heap):
┌──────────┐     ┌──────────────────┐
│ reference │ ──→ │    Object data    │
└──────────┘     └──────────────────┘

Wrapper Object (heap):
┌──────┬──────┬──────┐
│ mark │ klass│value │  Integer = 16 bytes (vs int = 4)
└──────┴──────┴──────┘
```

## Type Conversion Paths

```
WIDENING (implicit, safe):
byte → short → int → long → float → double
                 ↑
                char

NARROWING (explicit cast, possible loss):
double → float → long → int → short → byte
                                    ↑
                                   char
```

## Autoboxing/Unboxing Flow

```
Primitive → Autoboxing → Wrapper
   int 42  ──────────→  Integer.valueOf(42)
   
Wrapper → Unboxing → Primitive
  Integer 42 ────────→  intValue()
```

## Integer Cache Range

```
Integer.valueOf()
    ↓
┌─────────────────────────────────────────────┐
│  Cached:  -128 to 127  (same instances)      │
│  Outside:  new Integer() (new object each)   │
└─────────────────────────────────────────────┘
```
