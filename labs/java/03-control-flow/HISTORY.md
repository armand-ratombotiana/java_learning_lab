# Control Flow — Evolution Across Java Versions

## Java 1.0 (1996)

Original control flow: `if-else`, `switch` (with `int`, `char`, `byte`, `short` only), `for`, `while`, `do-while`, `break`, `continue`, labeled statements.

No enhanced for-loop. No `assert`.

## Java 1.4 (2002)

The `assert` keyword became fully functional (previously present but disabled). Syntax: `assert condition;` or `assert condition : "message";`.

## Java 5 (2004)

- **Enhanced for-loop** (for-each): `for (Type var : iterable)`. Works with arrays and any `Iterable`.
- **Switch on enums**: `switch (day) { case MONDAY: ... }`

## Java 7 (2011)

- **Switch on Strings**: `switch (str) { case "hello": ... }`. Compiled as hash-code switch + equals checks.

## Java 12 (2019)

- **Switch expressions** (preview): Arrow syntax `case X -> result`, no fall-through
- **Multi-label case**: `case MONDAY, FRIDAY ->`

## Java 14 (2020)

- **Switch expressions** (standard): Can be assigned to variables, must be exhaustive

## Java 17 (2021) — LTS

- **Pattern matching for switch** (preview): `case String s -> s.length()`

## Java 21 (2023)

- **Pattern matching for switch** (standard): Supports type patterns, guarded patterns, null cases
- **Record patterns**: Deconstruct records in switch cases
- **Unnamed patterns**: `case _ ->` for wildcard matching

## Future Trends

- Pattern matching continues to expand, making switch more powerful
- Control flow analysis improving for pattern matching exhaustiveness
- Unnamed variables (`int _ = getValue();`) further refine control flow syntax
