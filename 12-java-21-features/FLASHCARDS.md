# Java 21 Features Flashcards

## Q1: What is a virtual thread?
**A**: A lightweight thread that runs on carrier (platform) threads. Thousands can run on few OS threads.

---

## Q2: What's the difference between platform and virtual threads?
**A**: Platform thread = 1 OS thread = expensive. Virtual thread = many share few OS threads = cheap.

---

## Q3: How do you create a virtual thread?
**A**: Thread.ofVirtual().start(runnable) or Executors.newVirtualThreadPerTaskExecutor()

---

## Q4: What is a sequenced collection?
**A**: An ordered collection with consistent first/last element access and reversed view.

---

## Q5: What's the benefit of record patterns?
**A**: Deconstruct records directly in instanceof and switch without manual extraction.

---

## Q6: Can null be matched in pattern matching switch?
**A**: Yes, add case null -> "null" explicitly as one of the case labels.

---

## Q7: What does String Templates provide?
**A**: Inline string interpolation with \{expression} syntax. Enabled via STR."..." processor.

---

## Q8: What's a guard in pattern matching?
**A**: && condition after pattern: case Integer i && i > 0 -> "positive"

---

## Q9: Is virtual thread better than platform thread for all cases?
**A**: No, for CPU-bound work platform threads are better. Virtual threads shine for I/O-bound work.

---

## Q10: What are sequenced methods?
**A**: addFirst(), addLast(), getFirst(), getLast(), reversed()

---

## Q11: Can synchronized blocks cause issues with virtual threads?
**A**: Yes, virtual threads can be "pinned" in synchronized blocks, causing performance issues.

---

## Q12: What JDK version introduced virtual threads?
**A**: Preview in Java 19/20, final in Java 21.