# Module 49: Kotlin for Java Developers - Mini Project

**Project Name**: Kotlin Migration & Idiomatic Refactor  
**Difficulty Level**: Intermediate  
**Estimated Time**: 2 hours

---

## 🎯 Objective
Migrate a verbose, legacy Java application into Kotlin, demonstrating the translation of POJOs to Data Classes, using Extension Functions, and safely handling nulls with the Elvis operator and safe calls.

## 📝 Requirements

### Core Features

1. **The Legacy Java Code (Conceptual Starting Point)**:
   - Imagine a Java class `Customer` with `id`, `name`, and an optional `Address` class (which has a `city`).
   - A `CustomerService` class with a method `String getCustomerCity(Customer customer)` which contains deep, nested `if (customer != null && customer.getAddress() != null)` checks to return the city or "Unknown".

2. **Kotlin Data Classes**:
   - Rewrite the `Customer` and `Address` classes as Kotlin `data class`es.
   - Enforce non-nullability for `id` and `name`, but explicitly make the `Address` property nullable (`Address?`).

3. **Idiomatic Null Handling**:
   - Write a Kotlin function `fun getCustomerCity(customer: Customer?): String`.
   - Use Kotlin's safe call operator (`?.`) and Elvis operator (`?:`) to reduce the nested Java `if` blocks into a single line of idiomatic Kotlin code.

4. **Extension Functions**:
   - Create an extension function on the `String` class called `fun String.toTitleCase(): String` that capitalizes the first letter of each word.
   - Apply this extension function to the output of `getCustomerCity` before printing it to the console.

---

## 💡 Solution Blueprint

1. **Data Classes**:
   ```kotlin
   data class Address(val city: String)
   data class Customer(val id: Long, val name: String, val address: Address?)
   ```

2. **Idiomatic Null Handling**:
   ```kotlin
   fun getCustomerCity(customer: Customer?): String {
       // Replaces 10 lines of Java nested null checks
       return customer?.address?.city ?: "Unknown"
   }
   ```

3. **Extension Function**:
   ```kotlin
   fun String.toTitleCase(): String {
       return this.split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
   }
   ```

4. **Execution**:
   ```kotlin
   fun main() {
       val customer1 = Customer(1, "Alice", Address("new york"))
       val customer2 = Customer(2, "Bob", null)
       
       println(getCustomerCity(customer1).toTitleCase()) // Output: New York
       println(getCustomerCity(customer2).toTitleCase()) // Output: Unknown
   }
   ```