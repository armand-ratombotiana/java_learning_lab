# Lab 24: Regular Expressions

## 📚 Overview

**Lab Number**: 24  
**Title**: Regular Expressions  
**Difficulty**: Intermediate  
**Duration**: 4 hours  
**Content**: 4,000+ lines  
**Status**: Ready for Implementation  

---

## 🎯 Learning Objectives

By the end of this lab, you will:
- ✅ Master regex pattern syntax
- ✅ Understand character classes
- ✅ Use quantifiers effectively
- ✅ Apply anchors and boundaries
- ✅ Implement groups and capturing
- ✅ Perform pattern matching
- ✅ Process text efficiently
- ✅ Validate data with regex
- ✅ Manipulate strings
- ✅ Optimize regex performance

---

## 📋 Topics Covered

### 1. Pattern Syntax Basics
- Literal characters
- Metacharacters
- Escape sequences
- Pattern compilation
- Flags and modifiers
- Case sensitivity
- Multiline mode
- Dot-all mode

### 2. Character Classes
- Basic character classes
- Negated character classes
- Predefined character classes
- Unicode character classes
- Range specifications
- Intersection and union
- Subtraction
- Real-world examples

### 3. Quantifiers
- Greedy quantifiers
- Lazy quantifiers
- Possessive quantifiers
- Zero or one
- Zero or more
- One or more
- Exact count
- Range count

### 4. Anchors and Boundaries
- Start of string anchor
- End of string anchor
- Word boundaries
- Non-word boundaries
- Line anchors
- Lookahead assertions
- Lookbehind assertions
- Real-world applications

### 5. Groups and Capturing
- Capturing groups
- Non-capturing groups
- Named groups
- Backreferences
- Group nesting
- Group extraction
- Replacement with groups
- Performance considerations

### 6. Pattern Matching
- Matching operations
- Finding patterns
- Replacing patterns
- Splitting strings
- Pattern compilation
- Matcher reuse
- Performance optimization
- Best practices

### 7. Text Processing
- Email validation
- URL validation
- Phone number validation
- Date validation
- Credit card validation
- IP address validation
- HTML tag extraction
- Log file parsing

### 8. Validation Patterns
- Input validation
- Format validation
- Range validation
- Custom validation
- Error handling
- User feedback
- Security considerations
- Best practices

### 9. String Manipulation
- Find and replace
- Text extraction
- String splitting
- String formatting
- Case conversion
- Whitespace handling
- Special character handling
- Performance optimization

### 10. Performance Optimization
- Pattern compilation
- Matcher reuse
- Regex complexity
- Backtracking issues
- Catastrophic backtracking
- Optimization techniques
- Benchmarking
- Best practices

---

## 🏗️ Project: Text Processing and Validation System

### Project Description
Create a comprehensive text processing and validation system using regular expressions with real-world examples, performance optimization, and best practices.

### Key Features
- ✅ Email validation
- ✅ URL validation
- ✅ Phone number validation
- ✅ Date validation
- ✅ Credit card validation
- ✅ IP address validation
- ✅ HTML tag extraction
- ✅ Log file parsing
- ✅ Text transformation
- ✅ Performance benchmarks

### Project Structure
```
24-regular-expressions/
├── src/main/java/com/learning/
│   ├── basics/
│   │   ├── PatternSyntax.java
│   │   ├── CharacterClasses.java
│   │   └── Quantifiers.java
│   ├── matching/
│   │   ├── PatternMatching.java
│   │   ├── FindAndReplace.java
│   │   └── StringSplitting.java
│   ├── validation/
│   │   ├── EmailValidator.java
│   │   ├── URLValidator.java
│   │   ├── PhoneValidator.java
│   │   ├── DateValidator.java
│   │   └── CreditCardValidator.java
│   ├── processing/
│   │   ├── TextProcessor.java
│   │   ├── LogParser.java
│   │   └── HTMLExtractor.java
│   ├── optimization/
│   │   ├── PatternCache.java
│   │   ├── PerformanceBenchmark.java
│   │   └── OptimizationTips.java
│   └── Main.java
├── src/test/java/com/learning/
│   ├── validation/
│   │   ├── EmailValidatorTest.java
│   │   ├── URLValidatorTest.java
│   │   ├── PhoneValidatorTest.java
│   │   ├── DateValidatorTest.java
│   │   └── CreditCardValidatorTest.java
│   ├── processing/
│   │   ├── TextProcessorTest.java
│   │   ├── LogParserTest.java
│   │   └── HTMLExtractorTest.java
│   └── PerformanceTest.java
└── pom.xml
```

---

## 📚 Code Examples

### Example 1: Email Validation
```java
public class EmailValidator {
    private static final String EMAIL_PATTERN = 
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    private static final Pattern pattern = 
        Pattern.compile(EMAIL_PATTERN);
    
    public static boolean isValidEmail(String email) {
        return pattern.matcher(email).matches();
    }
}
```

### Example 2: Find and Replace
```java
public class TextProcessor {
    public static String replacePhoneNumbers(String text) {
        String phonePattern = "\\d{3}-\\d{3}-\\d{4}";
        return text.replaceAll(phonePattern, "[PHONE]");
    }
    
    public static String extractPhoneNumbers(String text) {
        Pattern pattern = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
        Matcher matcher = pattern.matcher(text);
        
        List<String> phones = new ArrayList<>();
        while (matcher.find()) {
            phones.add(matcher.group());
        }
        return phones;
    }
}
```

### Example 3: Log File Parsing
```java
public class LogParser {
    private static final String LOG_PATTERN = 
        "(\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2}:\\d{2}) " +
        "\\[(\\w+)\\] (.+)";
    
    public static void parseLog(String logLine) {
        Pattern pattern = Pattern.compile(LOG_PATTERN);
        Matcher matcher = pattern.matcher(logLine);
        
        if (matcher.find()) {
            String date = matcher.group(1);
            String time = matcher.group(2);
            String level = matcher.group(3);
            String message = matcher.group(4);
            
            System.out.println("Date: " + date);
            System.out.println("Time: " + time);
            System.out.println("Level: " + level);
            System.out.println("Message: " + message);
        }
    }
}
```

### Example 4: URL Validation
```java
public class URLValidator {
    private static final String URL_PATTERN = 
        "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$";
    
    private static final Pattern pattern = 
        Pattern.compile(URL_PATTERN, Pattern.CASE_INSENSITIVE);
    
    public static boolean isValidURL(String url) {
        return pattern.matcher(url).matches();
    }
}
```

---

## 🧪 Unit Tests

### Test Coverage
- ✅ 150+ unit tests
- ✅ Pattern correctness tests
- ✅ Validation tests
- ✅ Performance tests
- ✅ Edge case tests
- ✅ Integration tests
- ✅ Benchmark tests
- ✅ Real-world scenario tests

### Test Categories
1. **Pattern Syntax Tests** (20+ tests)
   - Character classes
   - Quantifiers
   - Anchors
   - Groups

2. **Validation Tests** (40+ tests)
   - Email validation
   - URL validation
   - Phone validation
   - Date validation
   - Credit card validation

3. **Processing Tests** (30+ tests)
   - Find and replace
   - String splitting
   - Text extraction
   - Log parsing

4. **Performance Tests** (20+ tests)
   - Pattern compilation
   - Matcher reuse
   - Optimization
   - Benchmarking

5. **Edge Case Tests** (20+ tests)
   - Empty strings
   - Special characters
   - Unicode
   - Large inputs

6. **Integration Tests** (20+ tests)
   - Real-world scenarios
   - Complex patterns
   - Performance
   - Correctness

---

## 📝 Exercises

### Exercise 1: Email Validator
Create a comprehensive email validator using regex with support for various email formats.

### Exercise 2: Log File Parser
Implement a log file parser that extracts date, time, level, and message from log entries.

### Exercise 3: HTML Tag Extractor
Create a tool to extract specific HTML tags and their content from HTML documents.

### Exercise 4: Phone Number Formatter
Implement a phone number formatter that converts various formats to a standard format.

### Exercise 5: URL Validator
Create a URL validator that supports various URL schemes and formats.

---

## 🎓 Quiz Questions

1. What is the difference between greedy and lazy quantifiers?
2. How do you create a capturing group in regex?
3. What is the purpose of anchors in regex?
4. How do you validate an email address with regex?
5. What are backreferences and how are they used?
6. How do you optimize regex performance?
7. What is catastrophic backtracking?
8. How do you use lookahead and lookbehind assertions?
9. What are character classes and how do you use them?
10. How do you handle special characters in regex?

---

## 🚀 Advanced Challenge

**Challenge**: Implement a comprehensive regex framework that:
- Supports all regex features
- Provides pattern validation
- Includes performance analysis
- Offers pattern recommendations
- Generates pattern documentation
- Validates pattern correctness
- Provides optimization suggestions
- Supports pattern composition

---

## 📊 Performance Benchmarks

### Pattern Compilation
- Simple pattern: ~1 μs
- Complex pattern: ~5 μs
- Very complex pattern: ~20 μs

### Matching Performance
- Simple match: ~0.5 μs
- Complex match: ~5 μs
- Large text: ~50 μs

### Optimization Impact
- Pattern reuse: 10-100x faster
- Compiled patterns: 5-10x faster
- Optimized patterns: 2-5x faster

---

## 🏆 Learning Outcomes

After completing this lab, you will:
- ✅ Master regex pattern syntax
- ✅ Understand all regex features
- ✅ Know when to use regex
- ✅ Implement patterns correctly
- ✅ Optimize regex performance
- ✅ Apply regex to real-world problems
- ✅ Understand regex trade-offs
- ✅ Follow best practices

---

## 📚 Resources

### Documentation
- Java Pattern and Matcher API
- Regular Expression Tutorial
- Regex Pattern Reference
- Performance Optimization Guide

### Tools
- Regex101.com for testing
- RegexBuddy for analysis
- JMH for benchmarking
- JUnit for testing

---

## ✅ Completion Checklist

- [ ] Understand all 10 concepts
- [ ] Review all code examples
- [ ] Complete all 5 exercises
- [ ] Pass all 150+ unit tests
- [ ] Answer all 10 quiz questions
- [ ] Complete advanced challenge
- [ ] Review best practices guide
- [ ] Implement portfolio project

---

**Lab 24: Regular Expressions**

*4,000+ Lines | 80+ Examples | 150+ Tests | 1 Project*

**Status: Ready for Implementation | Quality: Professional | Difficulty: Intermediate**

---

*Ready to master regular expressions!* 🚀