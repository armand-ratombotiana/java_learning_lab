# Security — Lambdas

## Serialisation Attacks
Lambdas are not serializable by default. If a lambda is captured across a serialization boundary (RMI, session replication), prevent it:

```java
// Instead of:
Runnable r = () -> doSomething(); // Not serializable

// Use a concrete class:
Runnable r = new SerializableRunnable();
```

## Permission Checks
If security manager is enabled, lambdas execute with the permissions of the enclosing code. Be careful when passing lambdas to untrusted code — they may access captured local variables that expose sensitive data.

## Code Injection
Never construct lambdas from untrusted strings. Unlike scripting languages, Java lambdas are compile-time constructs, but you should still avoid `javax.script.ScriptEngine` + user input combos.

## Capturing Secrets
```java
String password = getPassword();
// Anyone receiving this lambda can call it to read the password
Supplier<String> leak = () -> password;
```
**Fix:** Use a privileged scope or avoid capturing secrets.
