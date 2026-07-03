# Visual Guide to Records

## Record Structure Diagram

```
record Point(int x, int y) {}

┌──────────────────────────────────────────────────┐
│              Point (extends Record)              │
├──────────────────────────────────────────────────┤
│  Components:                                     │
│    ┌──────────┐    ┌──────────┐                  │
│    │  x: int  │    │  y: int  │                  │
│    └──────────┘    └──────────┘                  │
│                                                   │
│  Generated Members:                               │
│    Constructor: Point(int x, int y)               │
│    Accessors:   x() → int   y() → int            │
│    equals():   component-wise equality            │
│    hashCode(): derived from x and y              │
│    toString(): "Point[x=..., y=...]"             │
└──────────────────────────────────────────────────┘
```

## Record vs. Class Comparison

```
record Point(int x, int y) {}          class Point {
                                          private final int x;
                                          private final int y;

                                          public Point(int x, int y) {
                                              this.x = x;
                                              this.y = y;
                                          }

                                          public int x() { return x; }
                                          public int y() { return y; }

  // 1 line                        //   @Override
                                      public boolean equals(Object o) { ... }
                                      @Override
                                      public int hashCode() { ... }
                                      @Override
                                      public String toString() { ... }
                                  }
```

## The Record Hierarchy

```
        java.lang.Object
              │
     java.lang.Record (abstract)
         ╱    │    ╲    ╲
    final  final  final  final
    Point   User  Circle Range
   (record)(record)(record)(record)
```

## Constructor Validation Flow

```
Call: new Range(5, 3)

  ┌─ Enter canonical constructor ─────────────────┐
  │                                                │
  │  1. Validate: if (start > end)                │
  │     └─ start=5, end=3 → 5 > 3 → TRUE          │
  │        └─ Throw IllegalArgumentException       │
  │                                                │
  │  (Fields NOT assigned — validation failed)     │
  └────────────────────────────────────────────────┘

Call: new Range(1, 10)

  ┌─ Enter canonical constructor ─────────────────┐
  │                                                │
  │  1. Validate: if (start > end)                │
  │     └─ start=1, end=10 → 1 > 10 → FALSE       │
  │                                                │
  │  2. Assign fields:                            │
  │     this.start = start;                        │
  │     this.end = end;                            │
  │                                                │
  │  3. Return new Range instance                   │
  └────────────────────────────────────────────────┘
```

## Serialization Flow

```
Serialization:
  record Person(String name, int age) implements Serializable {}

  ObjectOutputStream:
    ├─ Write class descriptor for Person
    ├─ Write "name" value = "Alice"
    ├─ Write "age" value = 30
    └─ Done

  ObjectInputStream:
    ├─ Read class descriptor for Person
    ├─ Read "name" = "Alice"
    ├─ Read "age" = 30
    ├─ Call canonical constructor Person("Alice", 30)
    │   └─ Validation runs (guaranteed!)
    └─ Return Person instance
```

## Pattern Matching Flow

```
Object obj = new Circle(new Point(3, 4), 5.0);

if (obj instanceof Circle(Point(int x, int y), double r)) {

Match flow:
  1. obj instanceof Circle? → YES (obj is Circle)
  2. Extract .center() → Point(3, 4)
  3. Extract .radius() → 5.0
  4. center instanceof Point? → YES (center is Point)
  5. Extract .x() → 3
  6. Extract .y() → 4
  7. Bind x=3, y=4, r=5.0

  // Use x, y, r directly
  System.out.println("Circle at (" + x + "," + y + "), r=" + r);
}
```

## Local Record Usage

```
public void process() {
    // ── Local record ──
    //   Scope: within this method only
    //   Purpose: intermediate data structure
    record Summary(String name, long count) {}
    
    List<Summary> result = data.stream()
        .map(item -> new Summary(item.name(), item.quantity()))
        .toList();
    
    result.forEach(s -> System.out.println(s));
    // Summary is not accessible outside this method
}
```
