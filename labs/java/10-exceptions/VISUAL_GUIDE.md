# Exceptions — Visual Guide

## Exception Hierarchy

```
                  ┌───────────┐
                  │ Throwable │
                  └─────┬─────┘
                        │
            ┌───────────┴───────────┐
            │                       │
      ┌─────▼─────┐          ┌──────▼──────┐
      │  Error    │          │  Exception  │  (checked ⇐ except RuntimeException)
      │ ──────────│          │ ─────────── │
      │ OutOfMem  │          │ IOException │
      │ StackOver │          │ SQLExcept   │
      │ ...       │          │ ...         │
      └───────────┘          └──────┬──────┘
                                    │
                          ┌─────────▼─────────┐
                          │ RuntimeException   │  (unchecked)
                          │ ────────────────── │
                          │ NullPointer        │
                          │ IllegalArgument    │
                          │ ArrayIndexOutOf    │
                          │ Arithmetic         │
                          └───────────────────┘
```

## try-catch-finally Flow

```
try {
    riskyOperation();
    │
    ├── success? ──→ skip catch blocks → finally → continue
    │
    └── exception? ──→ check catch types
            │
            ├── matched? ──→ catch block → finally → continue
            │
            └── unmatched? ──→ finally → propagate exception
```

## Stack Trace Visualization

```
Exception in thread "main" java.lang.NullPointerException
    at com.example.Service.process(Service.java:25)  ← origin
    at com.example.Controller.handle(Controller.java:12)
    at com.example.Application.main(Application.java:5)
        ↑                                   ↑
   Stack frames (top = origin)    Line numbers in source
```

## try-with-resources

```
try (FileReader fr = new FileReader("file.txt");
     BufferedReader br = new BufferedReader(fr)) {
    String line = br.readLine();
}  ← br.close(), then fr.close() (reverse order)
   ↑
   Automatically generated finally block

If readLine() throws AND close() throws:
→ Primary exception: from readLine()
→ Suppressed exception: from close() (via addSuppressed())
```

## Checked vs Unchecked

```
CHECKED EXCEPTION                    UNCHECKED EXCEPTION
Compilation error if not handled     No compilation requirement
    │                                        │
    ▼                                        ▼
public void readFile()              public void divide() {
    throws IOException  ← required      int x = 5 / 0;  // runtime
                                            // no throws needed
    OR:                                 }
    try {
        // handle
    } catch (IOException e) {
        // ...
    }
```

## Custom Exception

```
┌─────────────────────────────────┐
│ InsufficientFundsException      │
│ extends Exception               │
├─────────────────────────────────┤
│ Constructor(balance, amount)    │
│   super("Insufficient...")      │
│   this.balance = balance        │
│   this.amount = amount          │
├─────────────────────────────────┤
│ getBalance(): double            │
│ getShortfall(): double          │
└─────────────────────────────────┘

throw new InsufficientFundsException(100.0, 250.0);
// Message: "Insufficient funds: balance=100.0, required=250.0"
// getShortfall() → 150.0
```
