# GUIDE — Reflection Basics

## Step 1: Obtaining Class Objects
- `String.class`, `obj.getClass()`, `Class.forName("java.util.List")`

## Step 2: Inspecting Constructors
- `getDeclaredConstructors()`, `newInstance()`

## Step 3: Inspecting Methods and Fields
- `getDeclaredMethods()`, `getDeclaredFields()`
- Invoke methods via `Method.invoke()`
- Read/write fields via `Field.get()/set()`

## Step 4: Access Checks
- `setAccessible(true)` to bypass `private`
- Understand module‑level access (Java 9+)

## Step 5: Exercises
1. Print all methods of a class with their parameter types
2. Invoke a private method reflectively
3. Create an instance using a private constructor
4. Benchmark reflective vs direct calls
