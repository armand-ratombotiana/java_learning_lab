# Mini Project: Custom ClassLoader for Encrypted Classes

## Objective
Build a `ClassLoader` that transparently decrypts and loads encrypted `.class` files at runtime. This demonstrates the security applications of custom class loaders.

## Requirements
1. Implement `EncryptedClassLoader extends ClassLoader`
2. Encrypted class files use AES-256 encryption with a hard-coded key
3. Override `findClass(String name)` to:
   - Resolve the class name to an encrypted file path
   - Read and decrypt the bytes
   - Call `defineClass()` to create the `Class<?>` object
4. Support a directory prefix for where encrypted classes are stored
5. Fail gracefully with `ClassNotFoundException` for missing classes

## Usage Example
```java
// Encrypt: javaacademy.EncryptTool HelloWorld.class
// Run:
EncryptedClassLoader loader = new EncryptedClassLoader(
    Path.of("./encrypted-classes"),
    ClassLoader.getSystemClassLoader()
);
Class<?> clazz = loader.loadClass("com.example.HelloWorld");
```

## Stretch Goals
- Add `-Djava.system.class.loader=EncryptedClassLoader` support
- Implement a key rotation mechanism
- Support class verification after decryption
- Cache decrypted bytes in a `SoftReference` map for efficiency

## Deliverables
- `EncryptedClassLoader.java`
- `EncryptTool.java` for encrypting class files
- `EncryptedClassLoaderTest.java`
- A README explaining the security implications
