# Architecture of Arithmetic

## Java Number Hierarchy

```
Object
  └─ Number (abstract)
       ├─ Byte
       ├─ Short
       ├─ Integer
       ├─ Long
       ├─ Float
       ├─ Double
       └─ BigInteger, BigDecimal (arbitrary precision)
```

## Primitive Types & Sizes

| Type | Size | Min | Max |
|------|------|-----|-----|
| `byte` | 8-bit | -128 | 127 |
| `short` | 16-bit | -32,768 | 32,767 |
| `int` | 32-bit | $-2^{31}$ | $2^{31}-1$ |
| `long` | 64-bit | $-2^{63}$ | $2^{63}-1$ |
| `float` | 32-bit | ~1.4e-45 | ~3.4e38 |
| `double` | 64-bit | ~4.9e-324 | ~1.8e308 |

## Arithmetic Logic Unit (ALU)

The ALU is the digital circuit that performs arithmetic. Key components:

- **Adder**: full-adder cascades
- **Multiplier**: Booth encoding + Wallace tree
- **Divider**: SRT division algorithm
- **FPU**: dedicated floating-point unit (IEEE 754)
