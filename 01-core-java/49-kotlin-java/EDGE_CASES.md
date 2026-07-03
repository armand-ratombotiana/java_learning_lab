# Module 49: Kotlin for Java Developers - Edge Cases & Pitfalls

---

## Pitfall 1: The `!!` (Not-Null Assertion) Operator

### ❌ Wrong
Using the `!!` operator everywhere to bypass Kotlin's null safety compiler checks. This defeats the entire purpose of the language and leads to instant `NullPointerException`s if the value happens to be null.
```kotlin
val length = user.address!!.city!!.length // ❌ If address is null, the app crashes!
```

### ✅ Correct
Embrace safe calls (`?.`), the Elvis operator (`?:`), or smart casts.
```kotlin
val length = user.address?.city?.length ?: 0 // ✅ Returns 0 if anything in the chain is null
```

---

## Pitfall 2: Platform Types from Java Interop

### ❌ Wrong
Assuming that a value returned from a Java method is perfectly non-null. When Kotlin calls Java code that lacks `@Nullable` or `@NonNull` annotations, Kotlin treats it as a "Platform Type" (denoted as `String!`). It relies on you to guess if it's nullable or not.
```kotlin
// Java: public String getUsername() { return null; }
val username: String = javaClass.getUsername() // ❌ Will crash at runtime with NullPointerException
```

### ✅ Correct
Always treat platform types cautiously by explicitly typing them as nullable, or enforce strict nullability annotations in your Java codebase.
```kotlin
val username: String? = javaClass.getUsername() // ✅ Safe
```

---

## Pitfall 3: Overusing Extension Functions

### ❌ Wrong
Creating massive files of Extension Functions to add "helper" methods to core JDK classes (like `String` or `List`) for highly specific, narrow business logic (e.g., `String.extractUserIdFromFormatB()`).

### ✅ Correct
Extension functions are globally available if imported and pollute the autocomplete namespace. Only use them for universally applicable, domain-agnostic utility functions. Keep specific business logic inside localized service classes.