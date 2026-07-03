# Generics — Visual Guide

## Generic Class Structure

```
┌─────────────────────────────────────────────┐
│                  Box<T>                      │
├─────────────────────────────────────────────┤
│  - item: T                                   │
├─────────────────────────────────────────────┤
│  + set(T): void                              │
│  + get(): T                                  │
└─────────────────────────────────────────────┘
         ▲                    ▲
         │                    │
         │  T = String        │  T = Integer
         │                    │
┌─────────────────┐  ┌─────────────────┐
│   Box<String>   │  │  Box<Integer>   │
├─────────────────┤  ├─────────────────┤
│  - item: String  │  │ - item: Integer │
└─────────────────┘  └─────────────────┘
```

## Type Erasure Flow

```
Source Code:
  List<String> list = new ArrayList<>();
  String s = list.get(0);

       │
       ▼  Compiler type-checking
       │
  Generic is safe ✓  →  Proceed to erasure
       │
       ▼  Type erasure
       │
Bytecode (equivalent):
  List list = new ArrayList();        // Raw type
  String s = (String) list.get(0);   // Cast inserted
```

## Variance Diagram

```
    ┌─────────────────────────────────────┐
    │              Object                  │
    ▲                 ▲                    │
    │                 │                    │
  String            Number                 │
    ▲              ▲     ▲                 │
    │              │     │                 │
  String[]       Integer Double            │
  (covariant)                              │
                                           │
    Generics are INVARIANT by default:
    List<Integer>  ─/─>  List<Number>
    List<Number>   ─/─>  List<Object>
    
    Use wildcards for variance:
    List<? extends Number>  ◄──  List<Integer>
    List<? super Number>    ──►  List<Object>
```

## PECS Visual

```
Producer (read):     ? extends T
┌──────────────┐     ┌────────────────┐
│   I read T's  │────│  List<? extends │
│   from here   │     │    Number>      │
└──────────────┘     └────────────────┘
                     Can read Number values
                     Cannot add (except null)

Consumer (write):    ? super T
┌──────────────┐     ┌────────────────┐
│  I write T's  │────│  List<? super   │
│   to here     │     │   Integer>      │
└──────────────┘     └────────────────┘
                     Can add Integer values
                     Can only read Object
```

## Bounded Type Parameter Syntax

```
<T extends Number>
    ↑         ↑
  Type      Upper bound
  parameter

<T extends Comparable<T> & Serializable>
    ↑         ↑               ↑
  Type     Class bound     Interface bound
          (must be first)    (any number)
```

## Wildcard Decision Tree

```
Do you need to READ values?
  ├─ Yes → Do you need to ADD values?
  │        ├─ Yes → Use EXACT type <T>
  │        └─ No  → Use <? extends T>
  └─ No  → Do you need to ADD values?
           ├─ Yes → Use <? super T>
           └─ No  → Use <?> (unbounded)
```
