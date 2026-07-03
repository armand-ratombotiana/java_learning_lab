# Refactoring Data Types

## Use Constants Instead of Magic Numbers

Before: `if (x > 100000) { ... }`
After: `private static final int MAX_LIMIT = 100000;`

## Replace Raw Types with Generics

Before: `List list = new ArrayList(); list.add(5); int x = (int) list.get(0);`
After: `List<Integer> list = new ArrayList<>(); list.add(5); int x = list.get(0);`

## Use BigDecimal for Money

Before: `double price = 19.99; double tax = price * 0.08; // rounding errors`
After: `BigDecimal price = new BigDecimal("19.99"); BigDecimal tax = price.multiply(new BigDecimal("0.08"));`

## Replace Wrapper with Primitive in Hot Paths

Before: `Integer sum = 0; for (int i = 0; i < 1000000; i++) { sum += i; }`
After: `int sum = 0; for (int i = 0; i < 1000000; i++) { sum += i; }`

## Use Underscores for Readability

Before: `long phoneNumber = 1234567890L;`
After: `long phoneNumber = 123_456_7890L;`

## Use `var` When Type Is Obvious

Before: `Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();`
After: `var map = new HashMap<String, List<Integer>>();`
