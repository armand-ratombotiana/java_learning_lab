# OOP Basics — Visual Guide

## Class vs Object

```
CLASS (Blueprint)                      OBJECT (Instance)
┌─────────────────────┐               ┌─────────────────────┐
│ class Car           │               │ myCar               │
├─────────────────────┤               ├─────────────────────┤
│ Fields:             │               │ model = "Tesla"     │
│   String model      │    new Car()  │ year = 2024         │
│   int year          │──────────────►│ speed = 0           │
│   int speed         │               │                     │
├─────────────────────┤               ├─────────────────────┤
│ Methods:            │               │ Methods:            │
│   void accelerate() │               │   accelerate()      │
│   void brake()      │               │   brake()           │
└─────────────────────┘               └─────────────────────┘
```

## Object Memory Layout

```
Object on Heap (64-bit, compressed OOPs):
┌────────────┬────────────┬──────────┬──────────┬──────────┐
│ Mark Word  │ Klass Ptr  │ field 1  │ field 2  │ padding  │
│   (8 bytes)│  (4 bytes) │ variable │ variable │ (to 8B)  │
└────────────┴────────────┴──────────┴──────────┴──────────┘
Total: 16+ bytes depending on fields
```

## Static vs Instance

```
CLASS: Student
┌─────────────────────────────────────────────┐
│  Static (shared across all instances)        │
│  ┌─────────────────────┐                    │
│  │ static int count = 3│                    │
│  │ static String school│                    │
│  └─────────────────────┘                    │
│                                              │
│  Instance (one per object)                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ Student  │  │ Student  │  │ Student  │  │
│  │ alice    │  │ bob      │  │ carol    │  │
│  ├──────────┤  ├──────────┤  ├──────────┤  │
│  │name=Alice│  │name=Bob  │  │name=Carol│  │
│  │id=1001   │  │id=1002   │  │id=1003   │  │
│  └──────────┘  └──────────┘  └──────────┘  │
└─────────────────────────────────────────────┘
```

## Constructor Chaining

```
new Manager("Alice", 100000, 5000)
    ↓
┌─ Manager(String, double, double)
│  │
│  ├─ super("Alice", 100000)  ──→  ┌─ Employee(String, double)
│  │                                │  │
│  │                                │  ├─ super()  ──→  ┌─ Object()
│  │                                │  │                └── (done)
│  │                                │  │
│  │                                │  └─ this.name = "Alice"
│  │                                │     this.salary = 100000
│  │                                └─────────── (done)
│  │
│  └─ this.bonus = 5000
└──────────────── (done)
```
