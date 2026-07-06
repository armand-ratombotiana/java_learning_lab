# Architecture of Class Loading

## ClassLoader Hierarchy
```
Bootstrap ClassLoader (C++, jrt:/java.base)
    └── Platform ClassLoader (Java, jdk.internal.loader.PlatformClassLoader)
        └── Application ClassLoader (Java, jdk.internal.loader.AppClassLoader)
            └── Custom ClassLoaders (URLClassLoader, etc.)
```

## Class Loading Lifecycle
```
Request → loadClass(name)
   ├── Check namespace cache (already loaded?)
   ├── Delegate to parent (recursive)
   ├── findClass(name) → locate bytes
   │   ├── DefineClass: parse bytes → create Klass
   │   ├── Link: verify + prepare + resolve
   │   └── Initialize: run static initializers
   └── Return Class<?> reference
```

## Class File Parser
```
ClassParser (JVM internal)
├── readMagic() → 0xCAFEBABE
├── readVersion() → major, minor
├── readConstantPool() → cp cache
├── readAccessFlags()
├── readThisClass(), readSuperClass()
├── readInterfaces()
├── readFields() → field array
├── readMethods() → method array
└── readAttributes() → attribute map
```

## ASM Component Architecture
```
ClassReader → byte[] → visit header
    → visitField → visit field info
    → visitMethod → visitMethod(reader) → MethodVisitor
        → visitCode → visit instructions
        → visitEnd
    → visitEnd
ClassWriter ← byte[] ← accept(visitor)
```

## invokedynamic Architecture
```
invokedynamic instruction
    → Bootstrap method lookup (constant pool)
    → Bootstrap method call (LambdaMetafactory)
    → CallSite creation
    → MethodHandle binding
    → Dispatch via MethodHandle
```
