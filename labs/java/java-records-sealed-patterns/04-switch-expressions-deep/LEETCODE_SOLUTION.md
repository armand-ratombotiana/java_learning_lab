# LeetCode Solution: Roman to Integer (Switch Expression)

**Problem:** [13. Roman to Integer](https://leetcode.com/problems/roman-to-integer/)

Uses a switch expression with arrow syntax to map each Roman numeral character.

## Java 21 Solution

```java
class Solution {
    public int romanToInt(String s) {
        int total = 0;
        int prev = 0;

        for (int i = s.length() - 1; i >= 0; i--) {
            int cur = switch (s.charAt(i)) {
                case 'I' -> 1;
                case 'V' -> 5;
                case 'X' -> 10;
                case 'L' -> 50;
                case 'C' -> 100;
                case 'D' -> 500;
                case 'M' -> 1000;
                default -> throw new IllegalArgumentException("Invalid char");
            };
            total += cur < prev ? -cur : cur;
            prev = cur;
        }
        return total;
    }
}
```

## Alternative: Pattern-Matched Switch on String

```java
public int romanToInt(String s) {
    int total = 0;
    int i = 0;
    while (i < s.length()) {
        total += switch (s.substring(i, Math.min(i + 2, s.length()))) {
            case "CM" -> { i += 2; yield 900; }
            case "CD" -> { i += 2; yield 400; }
            case "XC" -> { i += 2; yield 90; }
            case "XL" -> { i += 2; yield 40; }
            case "IX" -> { i += 2; yield 9; }
            case "IV" -> { i += 2; yield 4; }
            default -> {
                yield switch (s.charAt(i++)) {
                    case 'M' -> 1000; case 'D' -> 500; case 'C' -> 100;
                    case 'L' -> 50;   case 'X' -> 10;  case 'V' -> 5;
                    case 'I' -> 1;
                    default -> throw new IllegalArgumentException();
                };
            }
        };
    }
    return total;
}
```

## Key Takeaway

Switch expressions with arrow syntax eliminate accidental fall-through and make value-mapping code **concise and readable**.
