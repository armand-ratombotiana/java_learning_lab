# Java Syntax — Internal Mechanics

## How the Java Compiler Parses Syntax

The `javac` compiler uses a hand-written recursive-descent parser (not a generated parser like yacc/bison). This design choice gives better error messages and easier maintenance. The parser is defined in `com.sun.tools.javac.parser` (part of the JDK's internal `javac` source).

### Token Representation

Internally, each token is represented by the `Token` enum in `com.sun.tools.javac.parser.Tokens`. There are over 100 token kinds including:

- `EOF`, `ERROR`, `IDENTIFIER`, `LPAREN`, `RPAREN`, `LBRACE`, `RBRACE`, `SEMI`, `DOT`, `COMMA`
- Keyword tokens: `IF`, `ELSE`, `FOR`, `WHILE`, `CLASS`, `RETURN`, `NEW`, etc.
- Operator tokens: `PLUS`, `MINUS`, `STAR`, `SLASH`, `EQ`, `NE`, `LT`, `GT`, etc.

### Abstract Syntax Tree (AST) Nodes

The parsed code becomes an AST with nodes defined in `com.sun.tools.javac.tree.JCTree`. Key node types include:

| Node | Purpose |
|------|---------|
| JCCompilationUnit | Top-level representation of a .java file |
| JCClassDecl | A class declaration |
| JCMethodDecl | A method declaration |
| JCVariableDecl | A variable declaration |
| JCIf | An if-else statement |
| JCForLoop | A basic for loop |
| JCExpressionStatement | A statement that is an expression |
| JCLiteral | A literal value |
| JCBinary | A binary operation (+, -, etc.) |

### Operator Precedence in the Parser

The parser handles operator precedence through the structure of parsing methods — not through a precedence table. Higher-precedence operators are parsed by methods called deeper in the recursion:

```java
// Pseudocode of parseExpression structure
parseExpression()        → handles assignment (lowest precedence)
parseBinaryExpression()  → handles ||, &&, |, ^, &, ==, !=, <, >, etc.
parseUnaryExpression()   → handles ++, --, +, -, !, ~
parsePrimaryExpression() → handles literals, this, super, parens (highest)
```

### Bytecode Verification

After compilation, the JVM's bytecode verifier checks the `.class` file. Verification is a four-pass process:

1. **Structural check**: Class file format, constant pool validity
2. **Type checking**: Each instruction's operand types
3. **Stack frame analysis**: Ensures the operand stack does not overflow/underflow
4. **Dataflow analysis**: Ensures local variables are initialized before use

### Syntax Sugar

Many Java syntax features are "sugar" that the compiler desugars into simpler bytecode:

| Syntax Sugar | Desugared Form |
|-------------|----------------|
| Enhanced for-loop | Index-based for-loop (arrays) or Iterator-based (Iterables) |
| Autoboxing | `Integer.valueOf()` / `intValue()` calls |
| Varargs | Array creation + method call |
| Diamond operator | Raw type with cast |
| String switch | Hash code + switch + equals checks |
| Try-with-resources | try-finally with close() calls |
| Lambda | `invokedynamic` + functional interface |
| Records | Final class with constructor, accessors, equals/hashCode/toString |

### Example: Desugaring the Enhanced For-Loop

Source code:
```java
List<String> list = Arrays.asList("a", "b");
for (String s : list) {
    System.out.println(s);
}
```

Desugared bytecode equivalent:
```java
List<String> list = Arrays.asList("a", "b");
for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
    String s = it.next();
    System.out.println(s);
}
```

The compiler makes this transformation during AST-to-bytecode translation. The `javap -c` tool can show the actual bytecode to verify.

### The `javap` Disassembler

Use `javap -c -p ClassName` to see how the compiler translates syntax to bytecode. This is invaluable for understanding syntax internals:

```bash
javap -c HelloWorld.class
```

Output shows each bytecode instruction with its offset, revealing how if-else becomes `if_icmpne`, loops become `goto` instructions, and method calls become `invokevirtual`/`invokestatic`.
