# Exercises: Sealed Classes

## Exercise 1: Simple Sealed Hierarchy

Define a sealed interface `MediaFile` with permitted subtypes `AudioFile`, `VideoFile`, and `ImageFile`. Each subtype should be a record with appropriate fields (e.g., `AudioFile` has filename, duration, codec). Write a method that returns the file size estimate for each type.

## Exercise 2: Expression Tree

Build an expression tree for boolean logic:
- `sealed interface BoolExpr` with permitted subtypes `Value`, `Not`, `And`, `Or`
- `record Value(boolean v)`, `record Not(BoolExpr expr)`, `record And(BoolExpr left, BoolExpr right)`, `record Or(BoolExpr left, BoolExpr right)`
- Write an `evaluate` method using exhaustive pattern matching
- Write a `simplify` method that implements boolean algebra rules

## Exercise 3: Sealed with Non-Sealed Extension Point

Design a logging framework:
- `sealed interface LogAppender` with permitted subtypes `ConsoleAppender`, `FileAppender`, `CustomAppender`
- `ConsoleAppender` and `FileAppender` are `final` records
- `CustomAppender` is `non-sealed` to allow third-party appenders
- Create an example custom appender that sends logs to a remote HTTP endpoint

## Exercise 4: Nested Sealed Hierarchy

Create a nested hierarchy for geometric shapes:
- `sealed interface Shape` permits `TwoDShape`, `ThreeDShape`
- `sealed interface TwoDShape extends Shape` permits `Circle`, `Rectangle`, `Triangle`
- `sealed interface ThreeDShape extends Shape` permits `Sphere`, `Cube`, `Cylinder`
- Implement `double area(Shape)` for 2D shapes and `double volume(Shape)` for 3D shapes

## Exercise 5: Sealed Class with Shared Implementation

Create a sealed abstract class `Notification` with common fields (timestamp, recipient) and permitted subtypes `EmailNotification`, `SMSNotification`, `PushNotification`. Each subtype implements a `send()` method differently. Use a pattern matching switch in a `Notifier` class to dispatch sending.

## Exercise 6: JSON Validator

Using the JSON sealed hierarchy from the code deep dive, write a validator that:
- Ensures all string values are valid UTF-8
- Ensures numbers are within a valid range
- Validates that object keys follow a naming convention (e.g., camelCase)
- Reports validation errors in a structured format

## Exercise 7: Payment System with Sealed Types

Model a payment processing system:
- `sealed interface PaymentMethod` permits `CreditCard`, `DebitCard`, `BankTransfer`, `CryptoCurrency`, `DigitalWallet`
- Each has appropriate fields (e.g., `CreditCard` has last4, expiry, cardholderName)
- Some subtypes are final, some are sealed (e.g., `DigitalWallet` permits `ApplePay`, `GooglePay`, `SamsungPay`)
- Write an exhaustive `processPayment` method
- Handle the case where a new payment method is added — show the compilation error

## Exercise 8: State Machine Refactoring

Take an existing state machine implemented with enums and switch statements (e.g., a traffic light or a document workflow) and refactor it to use sealed classes with record subtypes that carry state-specific data.

## Exercise 9: Plugin Architecture

Design a plugin system:
- `sealed interface Plugin` permits `BuiltinPlugin`, `ThirdPartyPlugin`
- `BuiltinPlugin` is `sealed` permitting `AuthPlugin`, `LoggingPlugin`, `CachePlugin` (all final records)
- `ThirdPartyPlugin` is `non-sealed`
- Implement a `PluginManager` that loads and executes plugins, handling all builtin types exhaustively while delegating third-party plugins to a generic interface

## Exercise 10: Recursive Data Structure

Create a recursive sealed hierarchy for an HTML document:
- `sealed interface HtmlNode` permits `TextNode`, `ElementNode`, `VoidElement`
- `TextNode(String text)` — simple text content
- `ElementNode(String tag, Map<String, String> attrs, List<HtmlNode> children)` — nested elements
- `VoidElement(String tag, Map<String, String> attrs)` — self-closing elements (br, img, input)
- Write a `render()` method that produces HTML output, handling all node types exhaustively
