# Control Flow — Flashcards

1. **Q: Most specific to least specific catch order?** A: Most specific first
2. **Q: `do-while` minimum iterations?** A: One
3. **Q: Switch can use which types?** A: int, char, byte, short, String, enum
4. **Q: `break` in switch does what?** A: Exits switch block
5. **Q: `continue` does what?** A: Skips to next loop iteration
6. **Q: Short-circuit AND?** A: && (stops if left is false)
7. **Q: Labeled break?** A: Exits outer loop: `break label;`
8. **Q: Switch expression requires?** A: Exhaustive cases
9. **Q: tableswitch vs lookupswitch?** A: tableswitch = dense, O(1); lookupswitch = sparse, O(log n)
10. **Q: Can you switch on long?** A: No
11. **Q: Enhanced for-loop on arrays?** A: Same as indexed loop (compiler)
12. **Q: Infinite loop forms?** A: `for(;;)`, `while(true)`
13. **Q: `else` belongs to which `if`?** A: Nearest unmatched if
14. **Q: Dangling else problem?** A: Without braces, else matches closest if
15. **Q: `for` loop parts?** A: init; condition; update
16. **Q: String switch compiled as?** A: Hash code + switch + equals
17. **Q: Operator precedence?** A: Which operators evaluate first
18. **Q: Most common off-by-one?** A: Using <= instead of <
19. **Q: Nested loops use?** A: Inner loop completes each outer iteration
20. **Q: Flow analysis?** A: Compiler detects unreachable code
