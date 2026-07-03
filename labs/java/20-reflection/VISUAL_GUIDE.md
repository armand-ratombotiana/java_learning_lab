# Visual Guide — Reflection

```
┌─────────────────────────────────────────────────────────┐
│  Reflection API Hierarchy                               │
│                                                         │
│  java.lang.Class<T>                                     │
│    ├── getName(), getModifiers(), getPackage()          │
│    ├── getMethods() / getDeclaredMethods() → Method[]   │
│    ├── getFields() / getDeclaredFields() → Field[]      │
│    ├── getConstructors() → Constructor[]                │
│    ├── getAnnotations() → Annotation[]                  │
│    └── newInstance()                                    │
│                                                         │
│  java.lang.reflect.Method                               │
│    ├── invoke(target, args)                             │
│    ├── getParameterTypes(), getReturnType()             │
│    └── getDeclaredAnnotations()                         │
│                                                         │
│  java.lang.reflect.Field                                │
│    ├── get(target) / set(target, value)                 │
│    ├── setAccessible(boolean)                           │
│    └── getType(), getName()                             │
│                                                         │
│  java.lang.reflect.Proxy                                │
│    └── newProxyInstance(loader, interfaces, handler)    │
└─────────────────────────────────────────────────────────┘

Reflection Flow:
  Class.forName → Class<?> → getMethod → Method → invoke(obj)
                 → newInstance → Object → invoke(method, obj)
```
