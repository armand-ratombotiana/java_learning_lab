# Visual Guide to Class Loading

## ClassLoader Hierarchy
```
Bootstrap ClassLoader (C++ native)
    ↑ Platform ClassLoader (JDK modules)
        ↑ Application ClassLoader (classpath)
            ↑ Custom ClassLoaders (webapps, plugins)
```

## Class File Structure
```
┌─────────────────────────────────────┐
│ Magic Number: 0xCAFEBABE (4 bytes)  │
├─────────────────────────────────────┤
│ Version: minor (2) + major (2)       │
├─────────────────────────────────────┤
│ Constant Pool: count + entries      │
│ ┌───────────────────────────────┐  │
│ │ #1 = Methodref               │  │
│ │ #2 = Class                   │  │
│ │ #3 = Utf8 "compute"         │  │
│ │ ...                          │  │
│ └───────────────────────────────┘  │
├─────────────────────────────────────┤
│ Access Flags: public, final, etc.  │
├─────────────────────────────────────┤
│ This Class/Super Class/Interfaces  │
├─────────────────────────────────────┤
│ Fields: count + field_info entries │
├─────────────────────────────────────┤
│ Methods: count + method_info       │
│ ┌───────────────────────────────┐  │
│ │ Method: "compute"             │  │
│ │ Descriptor: "(I)I"            │  │
│ │ Code: bytecode instructions   │  │
│ └───────────────────────────────┘  │
├─────────────────────────────────────┤
│ Attributes: SourceFile, etc.       │
└─────────────────────────────────────┘
```

## ASM Transformation Pipeline
```
.class bytes → ClassReader → ClassVisitor → ClassWriter → new .class bytes
                                ↓
                         (modify/add/remove)
```

## Bytecode Instruction Categories
```
Stack Manipulation: iconst_0, bipush, dup, swap, pop
Local Variables: iload_0, istore_1, aload, astore
Arithmetic: iadd, isub, imul, idiv, irem
Objects: new, getfield, putfield, instanceof, checkcast
Arrays: newarray, arraylength, iaload, iastore
Control Flow: ifeq, if_icmpne, goto, tableswitch
Method Calls: invokevirtual, invokespecial, invokestatic, invokeinterface
```

## invokedynamic Bootstrap
```
invokedynamic #ref → Bootstrap Method (LambdaMetafactory)
                         ↓
                    CallSite
                         ↓
                    MethodHandle (generated implementation)
```
