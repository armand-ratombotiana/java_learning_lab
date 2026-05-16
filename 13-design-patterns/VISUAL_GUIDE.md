# Visual Guide to Design Patterns

## Singleton Pattern

```
┌─────────────────────────────────────────────┐
│           SINGLETON                         │
├─────────────────────────────────────────────┤
│                                             │
│  Class: Singleton                            │
│  ┌───────────────────────────────────────┐  │
│  │ - instance: Singleton (static)       │  │
│  ├───────────────────────────────────────┤  │
│  │ - Singleton() (private)              │  │
│  │ + getInstance(): Singleton           │  │
│  └───────────────────────────────────────┘  │
│           │                                  │
│           ▼                                  │
│      getInstance() ──► Returns single        │
│                         instance             │
└─────────────────────────────────────────────┘
```

## Factory Method

```
┌─────────────────────────────────────────────┐
│           FACTORY METHOD                     │
├─────────────────────────────────────────────┤
│                                             │
│   Creator                                   │
│   ┌─────────────────────────────────────┐   │
│   │ + createProduct(): Product          │   │
│   └─────────────────────────────────────┘   │
│         │                                    │
│         │ creates                            │
│         ▼                                    │
│   ┌───────────┐    implements    ┌────────┐│
│   │ Product   │ ◄──────────────── │Concrete│
│   │(interface)│                  │Product │ │
│   └───────────┘                  └────────┘│
│                                             │
└─────────────────────────────────────────────┘
```

## Observer Pattern

```
┌─────────────────────────────────────────────┐
│             OBSERVER                         │
├─────────────────────────────────────────────┤
│                                             │
│   Subject          observers           Observer
│   ────────        ──────────         ───────
│   +attach()      ───────────►       +update()
│   +detach()      ───────────►       
│   +notify()      ───────────►       
│                                             │
│   State ─────────────────────────► Each    │
│   changed         notifications       observer
│                                           notified
└─────────────────────────────────────────────┘
```

## Pattern Selection Guide

```
Need                    Pattern
─────────────────────────────────────────────
One instance            → Singleton
Create objects          → Factory/Builder
Extend behavior        → Decorator
Access differently     → Adapter
Many algorithms        → Strategy
Notify changes         → Observer
```