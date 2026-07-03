# Java Syntax — Theoretical Foundation

## Lexical Structure

Java source files are Unicode text. The compiler breaks source code into a sequence of tokens: identifiers, keywords, literals, operators, and separators. Whitespace (spaces, tabs, line terminators) is used to separate tokens but is otherwise ignored.

### Tokens

- **Identifiers**: Names for variables, methods, classes, etc. Must start with a letter (`A-Z`, `a-z`, `_`, or `$`) and continue with letters or digits. Case-sensitive.
- **Keywords**: Reserved words with special meaning (e.g., `class`, `if`, `for`, `new`, `return`). Cannot be used as identifiers.
- **Literals**: Representations of fixed values — integer literals (`42`, `0xFF`), floating-point (`3.14`, `1e5`), boolean (`true`, `false`), character (`'A'`), string (`"hello"`), and null literal (`null`).
- **Operators**: Tokens that perform operations — arithmetic (`+`, `-`, `*`, `/`), relational (`<`, `==`, `!=`), logical (`&&`, `||`), assignment (`=`), bitwise (`&`, `|`, `^`), and ternary (`? :`).
- **Separators**: Punctuation that groups code — `{ }`, `[ ]`, `( )`, `;`, `,`, `.`, `...`.

### Comments

Java supports three comment styles:

- `//` Single-line comment: extends to end of line
- `/* ... */` Multi-line comment: can span multiple lines, does not nest
- `/** ... */` Documentation comment (Javadoc): used by the `javadoc` tool to generate API documentation

## Program Structure

A Java source file has this structure:

1. **Package declaration** (optional, but conventional)
2. **Import statements** (optional)
3. **Type declarations** (class, interface, enum, record — at least one)

Every application must have a class containing a `main` method with signature `public static void main(String[])`.

## Naming Conventions

Java follows CamelCase conventions defined in the Java Language Specification:

| Construct | Convention | Example |
|-----------|------------|---------|
| Class/Interface | PascalCase | `ArrayList`, `Runnable` |
| Method | camelCase | `getValue()`, `setName()` |
| Variable | camelCase | `firstName`, `totalCount` |
| Constant | UPPER_SNAKE_CASE | `MAX_VALUE`, `PI` |
| Package | lowercase with dots | `java.util`, `com.example` |
| Type Parameter | single uppercase | `T`, `E`, `K`, `V` |

## Operators Precedence (highest to lowest)

1. Postfix: `expr++`, `expr--`
2. Unary: `++expr`, `--expr`, `+expr`, `-expr`, `~`, `!`
3. Multiplicative: `*`, `/`, `%`
4. Additive: `+`, `-`
5. Shift: `<<`, `>>`, `>>>`
6. Relational: `<`, `>`, `<=`, `>=`, `instanceof`
7. Equality: `==`, `!=`
8. Bitwise AND: `&`
9. Bitwise XOR: `^`
10. Bitwise OR: `|`
11. Logical AND: `&&`
12. Logical OR: `||`
13. Ternary: `? :`
14. Assignment: `=`, `+=`, `-=`, `*=`, `/=`, `%=`, `&=`, `^=`, `|=`, `<<=`, `>>=`, `>>>=`

## Blocks and Statements

A block is a sequence of statements enclosed in braces `{}`. A statement can be:

- **Expression statement**: `x = 5;`, `methodCall();`
- **Declaration statement**: `int x;`, `String s;`
- **Control flow statement**: `if`, `switch`, `for`, `while`, `do-while`
- **Return statement**: `return value;`
- **Throw statement**: `throw new Exception();`
- **Try statement**: `try { ... } catch ...`
- **Assert statement**: `assert condition;`
- **Synchronized statement**: `synchronized(obj) { ... }`

## Separators

- `;` terminates statements
- `,` separates variables in declarations and parameters
- `.` accesses members and separates package names
- `...` indicates varargs
- `@` precedes annotations
- `::` method reference operator (Java 8+)
- `->` lambda arrow operator (Java 8+)
