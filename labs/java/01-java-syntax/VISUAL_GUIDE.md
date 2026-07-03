# Java Syntax — Visual Guide

## ASCII Diagram: Java Source File Structure

```
  ┌─────────────────────────────────────────────────────────────┐
  │ package com.example;          ← package declaration (opt.) │
  │                                                             │
  │ import java.util.List;        ← import statements (opt.)   │
  │ import java.util.ArrayList;                                  │
  │                                                             │
  │ /**                          ← Javadoc comment             │
  │  * This class demonstrates Java syntax.                    │
  │  */                                                         │
  │ public class HelloWorld {     ← class declaration          │
  │                                                             │
  │     // Field declaration                                     │
  │     private String message;                                 │
  │                                                             │
  │     /** Constructor */                                      │
  │     public HelloWorld(String msg) {                         │
  │         this.message = msg;                                 │
  │     }                                                       │
  │                                                             │
  │     /** Method declaration */                               │
  │     public void greet() {                                   │
  │         /* Multi-line                                       │
  │            comment */                                       │
  │         System.out.println(message);                        │
  │     }                                                       │
  │                                                             │
  │     /** Entry point */                                      │
  │     public static void main(String[] args) {                │
  │         HelloWorld hw = new HelloWorld("Hi!");              │
  │         hw.greet();                                         │
  │     }                                                       │
  │ }                                                           │
  └─────────────────────────────────────────────────────────────┘
```

## Token Visualization

```
Source: int   count  =   42     ;
Token:  KEY   ID     OP  LITERAL SEP
        ────  ─────  ──  ──────  ─
        int   count  =   42      ;
```

## Operator Precedence Pyramid

```
                ()  []  .        ← Highest precedence
                ++  --  + -  ~  !
                *   /   %
                +   -
                <<  >>  >>>
                <   <=  >  >=  instanceof
                ==  !=
                &
                ^
                |
                &&
                ||
                ?:
                =  +=  -=  *=  /=  %=  ← Lowest precedence
```

## Control Flow Diagram (if-else)

```
        ┌─────────┐
        │ Start   │
        └────┬────┘
             │
        ┌────▼────┐   false   ┌──────────┐
        │ if (x>0)├──────────►│ else     │
        └────┬────┘           └────┬─────┘
             │ true                │
        ┌────▼────┐          ┌─────▼─────┐
        │ pos     │          │ neg/zero  │
        └────┬────┘          └─────┬─────┘
             │                     │
             └────────┬────────────┘
                      │
                 ┌────▼────┐
                 │ End    │
                 └─────────┘
```

## Block Nesting Visualization

```
class OuterClass {                      ← Level 0 (class body)
    │
    ├── int field;                      ← Level 1 (field)
    │
    ├── void outerMethod() {            ← Level 1 (method)
    │   │
    │   ├── int localVar;               ← Level 2 (variable)
    │   │
    │   └── if (condition) {            ← Level 2 (if block)
    │       │
    │       └── for (int i=0; i<n; i++) { ← Level 3 (for block)
    │           │
    │           └── System.out.println(i); ← Level 4
    │           }
    │       }
    │
    └── }
}
```

## Statement Types Visual Map

```
STATEMENTS
├── DECLARATION:    int x;  String s;
├── EXPRESSION:     x = 5;  methodCall();  new Object();
├── CONTROL FLOW:
│   ├── SELECTION:  if, switch
│   ├── ITERATION:  for, while, do-while
│   └── BRANCH:     break, continue, return
├── EXCEPTION:      try-catch-finally, throw, assert
└── SYNCHRONIZED:   synchronized(obj) { }
```

## Keyword Categories

```
KEYWORDS (49 total in Java 21)
├── CLASS/INTERFACE:  class, interface, enum, record, extends, implements, permits
├── ACCESS CONTROL:   public, private, protected
├── MODIFIERS:        static, final, abstract, sealed, non-sealed, strictfp
├── PRIMITIVE TYPES:  boolean, byte, char, short, int, long, float, double, void
├── LITERALS:         true, false, null
├── CONTROL FLOW:     if, else, for, while, do, switch, case, default, break, continue, return
├── EXCEPTIONS:       try, catch, finally, throw, throws, assert
├── OBJECT:           new, this, super, instanceof
├── MODULES:          module, requires, exports, opens, provides, uses, transitive
└── RESERVED:         goto, const (unused but reserved)
```

## Method Signature Anatomy

```
 ┌─ access modifier
 │    ┌─ optional modifier
 │    │    ┌─ return type
 │    │    │       ┌─ method name
 │    │    │       │     ┌─ parameter list
 │    │    │       │     │
public static int calculateSum(int a, int b) throws IllegalArgumentException {
 │    │    │       │     │                              └─ exception declaration
 │    │    │       │     └─ parameter: type + name
 │    │    │       └─ lowercase camelCase
 │    │    └─ void or primitive or reference
 │    └─ static/final/abstract/synchronized
 └─ public/private/protected
```
