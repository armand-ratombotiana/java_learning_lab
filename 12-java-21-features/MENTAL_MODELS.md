# Mental Models for Java 21 Features

## Virtual Threads: Platform vs Virtual

```
Platform Thread:  One OS thread = One Java thread
                  Expensive to create, limited count

Virtual Thread:   Many virtual threads share few OS threads
                  Cheap to create, can have millions

Analogy:
Platform Thread = Owning a car
Virtual Thread = Using ride-share (share resources)
```

## Sequenced Collections: Order Matters

Think of sequenced collections as a line:
```
[First] ──► [Second] ──► [Third] ──► [Last]
   ↑                                          ↑
addFirst()                              addLast()
removeFirst()                          removeLast()
```

## Pattern Matching: Type Casting Automatic

```
Traditional:   Check type, then manually cast
Pattern Match: Type + extraction in one step

obj instanceof String s
   ├── Checks: Is obj a String?
   ├── Binds: s = (String) obj
   └── Use: s directly in block
```

## String Templates: Embedded Expressions

```
String:     "Hello " + name + "!"
Template:   "Hello {name}!"

The template knows name is a variable,
  no string concatenation needed.
```