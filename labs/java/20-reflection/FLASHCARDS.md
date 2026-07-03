# Flashcards — Reflection

**Q:** What is `Class<?>`?  
**A:** Runtime representation of a Java class.

**Q:** What is `Class.forName()`?  
**A:** Loads a class by fully qualified name.

**Q:** What does `getDeclaredMethods()` return?  
**A:** All methods declared in the class (public, protected, private).

**Q:** What does `method.invoke(obj, args)` do?  
**A:** Calls the method on obj with given arguments.

**Q:** What is `setAccessible(true)`?  
**A:** Suppresses access control checks for the reflective object.

**Q:** What is `InvocationTargetException`?  
**A:** Wrapper for exception thrown by the invoked method.

**Q:** What is a dynamic proxy?  
**A:** A class created at runtime implementing specified interfaces.

**Q:** What is `InvocationHandler`?  
**A:** Interface for method call interception in proxies.

**Q:** What is `MethodHandle`?  
**A:** A typed, directly executable reference to a method.

**Q:** What is `--add-opens`?  
**A:** JVM flag to open a module for reflective access.
