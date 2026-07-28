# Mock Interview: Switch Expressions

**Interviewer:** "Implement a calculator that evaluates a simple expression like '3 + 5' using modern Java features."

**Candidate:** "I'll parse the tokens and use a switch expression with arrow syntax:

```java
int calculate(String input) {
    var parts = input.split(" ");
    if (parts.length != 3) throw new IllegalArgumentException();
    int a = Integer.parseInt(parts[0]);
    int b = Integer.parseInt(parts[2]);

    return switch (parts[1]) {
        case "+" -> a + b;
        case "-" -> a - b;
        case "*" -> a * b;
        case "/" -> {
            if (b == 0) throw new ArithmeticException("Division by zero");
            yield a / b;
        }
        default -> throw new IllegalArgumentException("Unknown op: " + parts[1]);
    };
}
```

The arrow syntax makes each case self-contained — no `break`, no fall-through. The `default` case catches invalid operators at runtime."

**Interviewer:** "What if I want to extend this to handle strings like '1 + 2 * 3'?"

**Candidate:** "I'd add operator precedence using records and a sealed hierarchy:

```java
sealed interface Token {}
record Num(int value) implements Token {}
record Op(String sym) implements Token {}
record Paren(char ch) implements Token {}

List<Token> tokens = ...; // tokenizer
```

Then use a switch expression to classify each token. The exhaustiveness check on the sealed `Token` type ensures that adding a new token (like a function name) forces me to handle it everywhere."
