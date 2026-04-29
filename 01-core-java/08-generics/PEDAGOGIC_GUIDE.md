# Module 13: Generics - Pedagogic Guide

**Total Study Time**: 8-10 hours  
**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-12  
**Learning Outcomes**: 6 major competencies

---

## 📚 Learning Outcomes

By completing this module, you will be able to:

1. **Understand Generics Fundamentals** - Know what generics are and why they matter
2. **Create Generic Classes** - Design and implement generic classes
3. **Create Generic Methods** - Implement generic methods with proper type parameters
4. **Use Bounded Types** - Apply bounded type parameters effectively
5. **Master Wildcards** - Use wildcards for flexible generic programming
6. **Avoid Common Pitfalls** - Prevent type safety issues and understand type erasure

---

## 🎯 4-Phase Study Path

### Phase 1: Fundamentals (2 hours)

**Goal**: Understand generics basics and benefits

**Activities**:
1. Read DEEP_DIVE.md Introduction section (20 min)
2. Watch generics fundamentals video (30 min)
3. Answer Beginner-level quiz questions (Q1-Q6) (30 min)
4. Code along: Create simple generic class (20 min)

**Key Concepts**:
- What are generics?
- Why use generics?
- Type safety benefits
- Generic class syntax

**Checkpoint**: Can you create a simple generic class?

---

### Phase 2: Generic Classes & Methods (2.5 hours)

**Goal**: Master generic classes and methods

**Activities**:

**Generic Classes (1.25 hours)**:
1. Study generic class syntax (20 min)
   - Read DEEP_DIVE.md Generic Classes section
2. Study multiple type parameters (20 min)
3. Study generic inheritance (20 min)
4. Code along: Implement generic classes (25 min)

**Generic Methods (1.25 hours)**:
1. Study generic method syntax (20 min)
   - Read DEEP_DIVE.md Generic Methods section
2. Study generic methods in generic classes (20 min)
3. Code along: Implement generic methods (25 min)
4. Answer Intermediate quiz (Q7-Q14) (20 min)

**Practice Exercises**:
1. Create Box<T> generic class
2. Create Pair<K,V> with multiple type parameters
3. Implement generic method printArray()
4. Implement generic method max()
5. Create generic class with generic methods

**Checkpoint**: Can you implement generic classes and methods?

---

### Phase 3: Bounded Types & Wildcards (2.5 hours)

**Goal**: Master bounded types and wildcards

**Activities**:

**Bounded Type Parameters (1.25 hours)**:
1. Study upper bounded types (20 min)
   - Read DEEP_DIVE.md Bounded Type Parameters
   - Review EDGE_CASES.md pitfall 7
2. Study multiple bounds (20 min)
3. Study bounded generic methods (20 min)
4. Code along: Implement bounded types (25 min)

**Wildcards (1.25 hours)**:
1. Study unbounded wildcards (20 min)
   - Read DEEP_DIVE.md Wildcards section
2. Study upper bounded wildcards (20 min)
3. Study lower bounded wildcards (20 min)
4. Code along: Use wildcards effectively (25 min)

**Practice Exercises**:
1. Create NumberBox<T extends Number>
2. Implement max() with bounded type
3. Use List<?> for flexible methods
4. Use List<? extends Number> for reading
5. Use List<? super Integer> for writing

**Checkpoint**: Can you use bounded types and wildcards?

---

### Phase 4: Advanced Topics & Mastery (2 hours)

**Goal**: Master advanced generics and avoid pitfalls

**Activities**:

1. **Type Erasure** (30 min)
   - Read DEEP_DIVE.md Type Erasure section
   - Review EDGE_CASES.md pitfalls 1-2
   - Understand runtime implications

2. **Advanced Generics** (30 min)
   - Study recursive type bounds
   - Study generic interfaces
   - Study covariance and contravariance
   - Answer Advanced quiz (Q15-Q20)

3. **Best Practices** (30 min)
   - Read DEEP_DIVE.md Best Practices
   - Review all EDGE_CASES.md pitfalls
   - Answer Expert quiz (Q21-Q24)

4. **Real-World Patterns** (30 min)
   - Study generic collections usage
   - Analyze existing generic code
   - Discuss trade-offs

**Capstone Project**:
Design and implement a generic data structure:

```
Project: Generic Stack Implementation

Requirements:
1. Implement Stack<T> generic class
2. Support push(T), pop(), peek()
3. Implement Iterable<T> interface
4. Add bounded type method: <U extends Comparable<U>> U max()
5. Add wildcard method: void addAll(Collection<? extends T>)
6. Handle edge cases (empty stack, null values)
7. Proper error handling

Deliverables:
- Generic Stack implementation
- Unit tests for all methods
- Tests with different types
- Documentation of type constraints
```

**Checkpoint**: Can you design and implement generic data structures?

---

## 📖 Study Materials

### Required Reading
- DEEP_DIVE.md (75-90 minutes)
- QUIZZES.md (90-120 minutes)
- EDGE_CASES.md (45-60 minutes)

### Recommended Resources
- Java Generics Tutorial
- Effective Java - Generics chapter
- Oracle Java Generics documentation
- Generic patterns and best practices

### Code Examples
- 160+ code examples in DEEP_DIVE.md
- 16 pitfall examples in EDGE_CASES.md
- 24 quiz questions with explanations

---

## 🎓 Practice Exercises

### Exercise 1: Simple Generic Class
**Difficulty**: Beginner  
**Time**: 30 minutes

Implement a Box<T> class:
- Store a single value
- Provide get() and set() methods
- Test with String, Integer, Double

### Exercise 2: Multiple Type Parameters
**Difficulty**: Beginner  
**Time**: 30 minutes

Implement a Pair<K,V> class:
- Store key-value pair
- Provide getKey() and getValue()
- Test with different type combinations

### Exercise 3: Generic Methods
**Difficulty**: Intermediate  
**Time**: 45 minutes

Implement utility methods:
- printArray(T[]) - print any array
- getFirst(T[]) - get first element
- max(T[]) - find maximum (with bound)

### Exercise 4: Bounded Types
**Difficulty**: Intermediate  
**Time**: 45 minutes

Implement bounded generic class:
- NumberBox<T extends Number>
- Implement doubleValue() method
- Test with Integer, Double, Long

### Exercise 5: Wildcards
**Difficulty**: Intermediate  
**Time**: 45 minutes

Implement methods with wildcards:
- printList(List<?>) - print any list
- sumNumbers(List<? extends Number>) - sum numbers
- addIntegers(List<? super Integer>) - add integers

### Exercise 6: Generic Collections
**Difficulty**: Advanced  
**Time**: 60 minutes

Use generic collections:
- Create List<String>, Map<String, Integer>
- Implement type-safe operations
- Avoid raw types and casting

### Exercise 7: Generic Interfaces
**Difficulty**: Advanced  
**Time**: 60 minutes

Implement generic interface:
- Container<T> interface
- ListContainer<T> implementation
- Test with different types

### Exercise 8: Advanced Generics
**Difficulty**: Advanced  
**Time**: 90 minutes

Implement advanced patterns:
- Recursive type bounds
- Covariance and contravariance
- Generic inheritance

---

## 🧪 Assessment Criteria

### Knowledge Assessment
- **Beginner Quiz**: 6 questions (25%)
- **Intermediate Quiz**: 8 questions (33%)
- **Advanced Quiz**: 6 questions (25%)
- **Expert Quiz**: 4 questions (17%)

### Practical Assessment
- **Exercise Completion**: 8 exercises (40%)
- **Code Quality**: Follows best practices (30%)
- **Type Safety**: Proper generic usage (20%)
- **Documentation**: Clear and complete (10%)

### Mastery Criteria
- Score 80%+ on all quiz levels
- Complete all 8 exercises
- Implement capstone project
- Explain generic type constraints

---

## 📊 Progress Tracking

### Week 1: Fundamentals & Classes
- [ ] Read Introduction section
- [ ] Complete Beginner quiz (Q1-Q6)
- [ ] Study generic classes
- [ ] Complete Exercise 1-2

### Week 2: Methods & Bounded Types
- [ ] Study generic methods
- [ ] Study bounded type parameters
- [ ] Complete Intermediate quiz (Q7-Q14)
- [ ] Complete Exercise 3-4

### Week 3: Wildcards & Advanced
- [ ] Study wildcards
- [ ] Study type erasure
- [ ] Study advanced generics
- [ ] Complete Advanced quiz (Q15-Q20)

### Week 4: Mastery & Capstone
- [ ] Review all pitfalls
- [ ] Complete Expert quiz (Q21-Q24)
- [ ] Complete Exercise 5-8
- [ ] Implement capstone project

---

## 🎯 Learning Tips

1. **Understand Why**: Know why generics exist and their benefits
2. **Practice Syntax**: Type out all examples, don't just read
3. **Experiment**: Modify examples to understand behavior
4. **Test Types**: Test generic code with different types
5. **Understand Erasure**: Know what happens at runtime
6. **Learn PECS**: Master the PECS rule for wildcards
7. **Avoid Pitfalls**: Study edge cases and prevention
8. **Read Code**: Analyze existing generic code

---

## 🚀 Next Steps

After completing this module:

1. **Apply Generics**: Use generics in your projects
2. **Study Collections**: Deep dive into generic collections
3. **Advanced Patterns**: Learn more complex generic patterns
4. **Reflection**: Study generics with reflection
5. **Performance**: Understand generic performance implications

---

## 📞 Common Questions

**Q: Why can't I create a generic array?**  
A: Type erasure makes generic arrays unsafe. Use `List<T>` or `List<?>[]` instead.

**Q: When should I use wildcards?**  
A: Use wildcards when you don't need to specify the exact type. Use PECS rule: extends for reading, super for writing.

**Q: What is type erasure?**  
A: Generic type information is removed at runtime and replaced with Object or upper bound.

**Q: Can I use generics with primitives?**  
A: No, use wrapper classes (Integer, Double, etc.) instead.

**Q: How do I handle unchecked warnings?**  
A: Use `@SuppressWarnings("unchecked")` only when necessary, or refactor to avoid the warning.

---

**Module 13 - Generics Pedagogic Guide**  
*Your complete learning roadmap for generics mastery*