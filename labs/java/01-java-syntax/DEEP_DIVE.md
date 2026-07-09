# Java Syntax — Ultra Deep Dive

## 1. Java Language Specification (JLS) Lexical Structure Deep-Dive

The JLS Chapter 3 defines Java's lexical structure as a Unicode-aware tokenization process. The grammar is formally specified in a modified BNF (Backus-Naur Form). Let's examine the token production pipeline:

### Unicode Escapes — The First Transformation

Before any lexical analysis, the compiler processes Unicode escapes. JLS §3.3 defines six forms of Unicode escape:

```
\ uXXXX    where XXXX is exactly 4 hex digits
```

The compiler applies a *transcoding* step: it reads raw bytes, converts to UTF-16 (or UTF-8 for source files), and then replaces every `\ u X X X X` (with optional spaces after backslash in the source character stream) with the actual Unicode character. This means:

```java
// These three lines are identical after Unicode processing:
int \u0061 = 1;   // \u0061 = 'a'
int a = 1;
// Unicode escapes in comments also get processed!
\u0061 = 2;       // Assigns to 'a'
```

**Critical implication**: Unicode escapes are processed *before* comments are recognized. The escape `\u000A` becomes a newline, even inside `//` comments. This is a known trap:

```java
// This is a comment \u000a System.out.println("BOOM!");
```

After Unicode processing, the above becomes:
```java
// This is a comment
 System.out.println("BOOM!");
```

This is why the JLS states in §3.4 that "Unicode escapes are processed before any other lexical translation."

### Lexer Implementation: The `Scanner` and `Token` Classes

OpenJDK's `com.sun.tools.javac.parser.Scanner` implements the lexer. It does not use regex. Instead, it's a hand-coded state machine operating on characters:

```java
// Simplified lexer logic for identifiers:
if (Character.isJavaIdentifierStart(ch)) {
    int pos = currentPos;
    while (Character.isJavaIdentifierPart(ch)) {
        readChar();
    }
    return new Token(IDENTIFIER, chars.substring(pos, currentPos));
}
```

The scanner distinguishes 84 token types defined in `Tokens.TokenKind` enum. Keywords like `if`, `else`, `class` are recognized by checking against a keyword hash table *after* scanning an identifier — not by special-casing characters.

### Whitespace and Line Terminators

JLS §3.6 defines whitespace as:
- ASCII SP (0x20), HT (0x09), FF (0x0C)
- Line terminators: LF (0x0A), CR (0x0D), CR+LF

Java source is *line-oriented* in some features:
- `//` comments terminate at any line terminator
- Javadoc `/** */` can span lines

### Syntactic Grammar vs Lexical Grammar

Java uses a two-level grammar:
1. **Lexical grammar** (JLS Ch.3): Characters → Tokens
2. **Syntactic grammar** (JLS Ch.4-18): Tokens → Parse Tree

This separation allows the compiler to shift to "modular" parsing when encountering module-info.java files, where the lexer recognizes `requires`, `exports`, `opens` etc. as module-specific tokens.

## 2. Parser Architecture: Recursive Descent with Operator Precedence

Java's parser (`com.sun.tools.javac.parser.Parser`) is a recursive-descent parser — NOT a generated LALR(1) or LL(k) parser. This is unusual for a language with Java's complexity, but it gives:

1. **Better error messages**: The parser knows what it expected at each point
2. **Simpler debugging**: Stack traces map directly to parse rules
3. **Easier maintenance**: No parser generator toolchain dependency

### Operator Precedence Encoded in Parse Methods

Unlike tools like yacc that use grammar rules to encode precedence, Java's parser encodes precedence in the method call hierarchy:

```java
// Precedence hierarchy (lowest to highest):
parseExpression()          // assignment: =, +=, -=, etc.
parseAssignmentExpression()
parseConditionalOr()       // ||
parseConditionalAnd()      // &&
parseInclusiveOr()         // |
parseExclusiveOr()         // ^
parseAnd()                 // &
parseEquality()            // ==, !=
parseRelational()          // <, >, <=, >=, instanceof
parseShift()               // <<, >>, >>>
parseAdditive()            // +, -
parseMultiplicative()      // *, /, %
parseUnary()               // ++expr, --expr, +expr, -expr, ~, !
parsePostfix()             // expr++, expr--
parsePrimary()             // literals, this, super, (expr)
```

Each method calls the next higher-precedence method for its operands. For example, `parseAdditive()`:
```java
// Simplified parseAdditive:
JCExpression parseAdditive() {
    JCExpression left = parseMultiplicative();
    while (token.kind == PLUS || token.kind == MINUS) {
        Token op = token;
        nextToken();
        JCExpression right = parseMultiplicative();
        left = make.Binary(op.kind, left, right);
    }
    return left;
}
```

### The Parser Tree (JCTree)

The parser produces an AST using `com.sun.tools.javac.tree.JCTree` subclasses:

| JCTree subclass | Purpose | Example source |
|----------------|---------|----------------|
| `JCCompilationUnit` | Top-level file node | `package p; class C {}` |
| `JCClassDecl` | Class/interface/enum/record | `class Foo {}` |
| `JCMethodDecl` | Method/constructor declaration | `void m() {}` |
| `JCVariableDecl` | Field/local/param declaration | `int x = 5;` |
| `JCIf` | if-else statement | `if (x) {} else {}` |
| `JCSwitch` | switch block | `switch (x) { case 1: }` |
| `JCTry` | try-catch-finally | `try {} catch(E e) {}` |
| `JCLiteral` | Literal values | `42`, `"hello"`, `true` |
| `JCBinary` | Binary operations | `a + b`, `x && y` |
| `JCLambda` | Lambda expression | `(x) -> x + 1` |

### Error Recovery Strategy

Java's parser uses a "panic mode" error recovery. When a syntax error is detected:
1. The parser skips tokens until a "sync" token is found (semicolons, closing braces, keywords like `class`, `interface`)
2. It inserts "synthetic" tokens to patch the parse tree
3. A single source file with N errors produces only one compilation *attempt* — no re-parse

This is why `javac` can report multiple errors in one invocation. The error recovery is in `checkForBadToken()` and `reportSyntaxError()` methods.

## 3. Try-With-Resources: Full Desugaring Walkthrough

The try-with-resources statement (JLS §14.20.3) is one of the most complex syntactic sugars in Java. Let's trace its complete desugaring:

### Source Code
```java
try (FileReader fr = new FileReader("test.txt");
     BufferedReader br = new BufferedReader(fr)) {
    br.readLine();
}
```

### Desugared Form (Conceptual)
```java
// Resource 1: FileReader
FileReader fr = null;
Throwable primaryExcFr = null;
try {
    fr = new FileReader("test.txt");
    // Resource 2: BufferedReader
    BufferedReader br = null;
    Throwable primaryExcBr = null;
    try {
        br = new BufferedReader(fr);
        br.readLine();
    } catch (Throwable t) {
        primaryExcBr = t;
        throw t;
    } finally {
        if (br != null) {
            if (primaryExcBr != null) {
                try { br.close(); } catch (Throwable suppressed) {
                    primaryExcBr.addSuppressed(suppressed);
                }
            } else {
                br.close();
            }
        }
    }
} catch (Throwable t) {
    primaryExcFr = t;
    throw t;
} finally {
    if (fr != null) {
        if (primaryExcFr != null) {
            try { fr.close(); } catch (Throwable suppressed) {
                primaryExcFr.addSuppressed(suppressed);
            }
        } else {
            fr.close();
        }
    }
}
```

**Key design decisions**:
1. Resources are closed in reverse order of declaration
2. If an exception occurs during close(), it is *suppressed* (added via `Throwable.addSuppressed()`)
3. The primary exception is the one from the try body, not from close()

### Bytecode Verification

Compile and disassemble:
```bash
javac TryWithResources.java
javap -c -p TryWithResources.class
```

The bytecode will show:
- Multiple nested `try-catch-finally` tables
- `invokevirtual Throwable.addSuppressed` calls
- `ifnull` checks for resource initialization
- Each resource gets its own exception table entry

### Java 9 Enhancement: Effectively Final Resources

Java 9 allows resources declared outside the try:
```java
FileReader fr = new FileReader("test.txt");
try (fr) {  // fr is effectively final
    fr.read();
}
```

The desugaring is the same, but the compiler must verify `fr` is *effectively final* (JLS §4.12.4) — not reassigned between initialization and try-with-resources.

## 4. Compilation Pipeline: Source to Bytecode

```
Source (.java)
    ↓ [Scanner: Characters → Tokens]
Token Stream
    ↓ [Parser: Tokens → AST]
JCTree (AST)
    ↓ [Attr/Enter: Name resolution, type checking]
Annotated AST
    ↓ [Desugar: Remove syntactic sugar]
Desugared AST
    ↓ [Flow: Definite assignment, reachability analysis]
Flow-checked AST
    ↓ [TransTypes: Bridge methods, boxing/unboxing]
Transformed AST
    ↓ [Lower: Lower to JVM-level constructs]
Lowered AST
    ↓ [Gen: Generate bytecode]
Class file (.class)
```

### Key Compilation Phases:

1. **Parse**: Tokens → JCTree (AST)
2. **Enter**: Creates symbols for classes in the symbol table
3. **Attr**: Type-checks the AST, resolves names to symbols
4. **Desugar**: Removes syntactic sugar (for-each, try-with-resources, etc.)
5. **Flow**: Definite assignment analysis (DA/DU), reachability
6. **TransTypes**: Handle boxing/unboxing, add bridge methods
7. **Lower**: "Lower" the AST to simpler constructs the bytecode generator can handle
8. **Gen**: Emit bytecode as a stream of bytes conforming to the class file format

## 5. Modules and Syntax: The `module-info.java` Parser

Java 9 introduced a special parser for `module-info.java`. The lexer shifts into "module mode" where tokens like `requires`, `exports`, `opens`, `provides`, `uses` are recognized as keywords only in this context (they remain valid identifiers elsewhere).

```java
module com.example {
    requires transitive java.sql;
    exports com.example.api;
    opens com.example.internal to hibernate;
    provides com.example.spi.Service with com.example.impl.ServiceImpl;
}
```

The `module-info` parser is a separate `JCTree` type: `JCModuleDecl`. The class file stores module data in a dedicated `Module` attribute.

## 6. The `assert` Keyword Under the Hood

The `assert` statement (JLS §14.10) is conditionally compiled:
```java
assert x > 0 : "x must be positive: " + x;
```

Bytecode generated (when assertions are enabled):
```
if (x <= 0) goto L_skip
    // create AssertionError with detail message
L_skip:
```

When assertions are disabled at runtime (`-da` flag), the JVM sets a boolean flag checked at each assertion point. JIT compilation can inline this check and, if assertions are disabled, completely eliminate the branch.

## 7. Advanced Tokenization: Suppressible Warnings

The `@SuppressWarnings` annotation interacts with the compiler's tokenization only indirectly — it affects whether certain lint warnings are emitted during attribute checking. The compiler tracks a "suppressible warning" state per AST node.

## 8. The `var` Keyword: Type Inference Under the Hood

`var` (JEP 286, Java 10+) is a *reserved type name* (not a keyword) — it only acts as a keyword when used as a type name in a local variable declaration context.

```java
var list = new ArrayList<String>();  // infers ArrayList<String>
// var is not a keyword everywhere:
int var = 5;  // Still valid! 'var' as identifier
```

The compiler replaces `var` with the inferred type at the Attr phase. The bytecode always shows the *concrete* type — `var` is entirely erased before bytecode generation.

## 9. Text Blocks (JEP 378): Lexer Integration

Text blocks (Java 15+) are delimited by triple quotes `"""`. The lexer handles them as a single token during scanning:

```java
String html = """
    <html>
        <body>
            <p>Hello, world!</p>
        </body>
    </html>
    """;
```

The lexer strips common leading whitespace using "indentation stripping" — it finds the minimum indentation across all lines and removes it. Trailing spaces are also stripped.

## 10. Patterns and Deconstructors

Java 21+ record patterns feature deconstruction syntax:
```java
if (obj instanceof Point(int x, int y)) {
    System.out.println(x + ", " + y);
}
```

The parser handles this by extending `JCPattern` — a new AST node type added in JDK 16. The lowering phase converts patterns into instanceof checks, casts, and component extraction calls.

## Reference: Key Compiler Source Files

| File | Package | Purpose |
|------|---------|---------|
| `Scanner.java` | `com.sun.tools.javac.parser` | Lexical analysis |
| `Parser.java` | `com.sun.tools.javac.parser` | Recursive-descent parser |
| `JCTree.java` | `com.sun.tools.javac.tree` | AST node definitions |
| `Attr.java` | `com.sun.tools.javac.comp` | Type checking and attribution |
| `Flow.java` | `com.sun.tools.javac.comp` | Definite assignment analysis |
| `Lower.java` | `com.sun.tools.javac.comp` | AST lowering for bytecode gen |
| `Gen.java` | `com.sun.tools.javac.jvm` | Bytecode generation |
| `Tokens.java` | `com.sun.tools.javac.parser` | Token kind definitions |
