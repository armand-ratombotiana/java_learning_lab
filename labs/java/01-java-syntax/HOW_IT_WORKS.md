# How Java Syntax Works — Step by Step

## Step 1: Source File Creation

A Java source file is a plain text file with a `.java` extension. The file name must match the public class name exactly (case-sensitive).

```
File: HelloWorld.java
Content:
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

## Step 2: Lexical Analysis (Tokenization)

The Java compiler (`javac`) first performs lexical analysis — it reads the source as a stream of characters and groups them into tokens.

Source text: `int count = 42;`

Token breakdown:
| Token | Type |
|-------|------|
| `int` | Keyword |
| `count` | Identifier |
| `=` | Operator (assignment) |
| `42` | Literal (integer) |
| `;` | Separator |

Whitespace and comments are discarded during this phase.

## Step 3: Syntactic Analysis (Parsing)

The compiler checks the token sequence against Java's grammar rules defined in the Java Language Specification (JLS). This phase builds an Abstract Syntax Tree (AST).

```
CompilationUnit
└── ClassDeclaration
    ├── Modifier: "public"
    ├── "class" "HelloWorld"
    └── ClassBody
        └── MethodDeclaration
            ├── Modifiers: "public" "static" "void"
            ├── "main"
            ├── FormalParameters: "(String[] args)"
            └── Block
                └── ExpressionStatement
                    └── MethodInvocation: "System.out.println"
                        └── Argument: "Hello, World!"
```

If any token sequence violates the grammar, the compiler produces a syntax error.

## Step 4: Semantic Analysis

The compiler checks meaning beyond syntax:

- **Type checking**: Ensures operands are compatible with operators
- **Scope resolution**: Ensures variables are declared before use
- **Access control**: Ensures visibility rules are followed
- **Definite assignment**: Ensures variables are initialized before reading
- **Exception checking**: Ensures checked exceptions are caught or declared

## Step 5: Code Generation

After semantic analysis, the compiler generates Java bytecode stored in `.class` files. The bytecode is a platform-independent binary format understood by the JVM.

## Step 6: Class Loading and Execution

The JVM loads the `.class` file, verifies the bytecode (additional syntax validation), and executes it. The JVM's bytecode verifier ensures:

- No illegal type conversions
- No stack overflow/underflow
- No access to private members from outside
- No forged class pointers

## Key Compilation Rules

### Rule 1: One public class per file
```java
// File: Vehicle.java
public class Vehicle { }   // OK
// public class Car { }    // Error: can only have one public type
class Engine { }           // OK: package-private
```

### Rule 2: Package hierarchy matches directory structure
```java
package com.example.app;  // Must be in com/example/app/ directory
```

### Rule 3: Identifiers are case-sensitive
```java
int value = 5;
int Value = 10;  // Different variable!
System.out.println(value); // Prints 5, not 10
```

### Rule 4: Variable must be initialized before use
```java
int x;
// System.out.println(x);  // Compilation error: variable x might not have been initialized
```

### Rule 5: Unreachable code is rejected
```java
while (true) { }
// System.out.println("never");  // Compilation error: unreachable statement
```

## The javac Compilation Process in Detail

```
Source (.java) → Parser → AST → Semantic Analyzer → Bytecode Generator → Class (.class)
                   │                        │
                   └── Syntax errors        └── Type errors, etc.
```

The parser operates in a single pass — it builds the AST while checking grammar rules. Semantic analysis is a separate pass over the complete AST.
