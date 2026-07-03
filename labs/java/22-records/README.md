# Lab 22: Records in Java

## Overview

Records, introduced as a preview in Java 14 and finalized in Java 16 (JEP 395), are a special kind of class in Java designed to serve as transparent carriers for immutable data. A record declaration is a compact syntactic form that declares a class whose state is entirely defined by a set of fields called "components," and whose API is derived automatically from that state.

### Key Concepts Covered

- Record declaration and instantiation
- Compact constructors and canonical constructors
- Custom methods and validation
- Records vs. traditional classes
- Local records (Java 16+)
- Record patterns (Java 21)
- Serialization with records
- Best practices and performance considerations

### Prerequisites

- Java 16+ JDK (Java 17+ LTS recommended)
- Understanding of classes, constructors, and access modifiers
- Familiarity with `equals()`, `hashCode()`, and `toString()` contracts

### What You'll Learn

By the end of this lab, you'll understand when and why to use records, how they compare to traditional classes, and how they interact with other modern Java features like pattern matching and sealed classes.

Let's explore Java records in depth.
