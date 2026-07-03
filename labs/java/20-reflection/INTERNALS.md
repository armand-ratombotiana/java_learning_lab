# Internals — Reflection

## Class Object Memory Layout
```
Class<?>
├── name (String)
├── accessFlags (int)
├── constantPool (ConstantPool)
├── methodTable (Method[])
├── fieldTable (Field[])
├── annotationData (Annotation[])
└── protectionDomain (ProtectionDomain)
```

## Method.invoke() Optimization
```java
// Inflates from native to Java accessor after threshold
private volatile MethodAccessor methodAccessor;
// threshold = 15 invocations in OpenJDK
```

## Proxy Generation
`ProxyGenerator.generateProxyClass()` produces bytecode:
```
proxies/$Proxy0 extends java.lang.reflect.Proxy
  fields: h (InvocationHandler), m0, m1, ...
  methods: toString, equals, hashCode, + interface methods
  each method: super.h.invoke(this, method, args)
```

## MethodHandle vs Method
```java
// Method — slow, reflection-based
Method m = clazz.getMethod("foo");
m.invoke(obj);

// MethodHandle — faster, no reflection overhead
MethodHandle mh = lookup.findVirtual(clazz, "foo", MethodType.methodType(void.class));
mh.invoke(obj);
```

## sun.reflect.ReflectionFactory
Controls creation of reflection objects. Used by frameworks for optimised field/method access.
