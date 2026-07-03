# Control Flow — How It Works

## Step 1: Compilation to Bytecode

Control flow constructs are compiled to branch instructions:

| Java Code | Bytecode |
|-----------|----------|
| `if (cond) {}` | `ifeq label` or `ifne label` |
| `switch (int)` | `tableswitch` or `lookupswitch` |
| `for (i=0; i<n; i++)` | `goto` back to condition check |
| `while (cond)` | Conditional branch to loop start |
| `break` | `goto` to after loop |
| `continue` | `goto` to loop update/condition |

## Step 2: if-else Execution

1. Evaluate boolean condition
2. If true, execute if-block; jump past else-block
3. If false, skip to else-block (or past if no else)

## Step 3: for Loop Execution Sequence

```
int i = 0;          // Initialization (once)
check: i < 5?       // Condition check (before each iteration)
  true → body → i++ → goto check
  false → exit loop
```

## Step 4: Enhanced For-Loop Desugaring

For arrays:
```java
int[] arr = {1, 2, 3};
for (int x : arr) { }
// Desugared to:
for (int i = 0; i < arr.length; i++) { int x = arr[i]; }
```

For Iterables:
```java
List<String> list = ...;
for (String s : list) { }
// Desugared to:
for (Iterator<String> it = list.iterator(); it.hasNext(); ) { String s = it.next(); }
```

## Step 5: Switch Compilation

`tableswitch` for dense cases (e.g., 0, 1, 2, 3):
```
tableswitch 0 to 3:
    0: case0
    1: case1
    2: case2
    3: case3
    default: default
```

`lookupswitch` for sparse cases (e.g., 1, 100, 1000):
```
lookupswitch:
    1: case1
    100: case100
    1000: case1000
    default: default
```
