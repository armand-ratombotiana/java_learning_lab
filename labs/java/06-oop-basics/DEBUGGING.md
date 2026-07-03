# Debugging OOP Basics

## NullPointerException

Most common OOP bug:
1. Object reference is null when you call a method on it
2. Check if constructor failed or wasn't called
3. Check if method returns null unexpectedly
4. Use "Evaluate Expression" in debugger to check null status

## Unexpected State

When object state is wrong:
1. Breakpoint on field writes — see who modified the field
2. Use "Field Watchpoint" (IntelliJ) to pause on field access
3. Check constructor — was the field initialized correctly?
4. Check setters — are they filtering invalid values?

## Static Context Confusion

When you get `non-static method cannot be referenced from a static context`:
1. Create an instance of the class to call the method
2. Or make the method static
3. Check if you're trying to use `this` in a static method

## Debugger Features for OOP

- "Evaluate Expression": `myObj.getBalance()` during breakpoint
- "Objects" view: expand to see all fields and their values
- Instance breakpoints: break when ANY method of this instance is called
- Class breakpoints: break on any method call in the class
- "Show Referring Objects": find what references this object
