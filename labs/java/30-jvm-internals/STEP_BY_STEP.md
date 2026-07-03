# Step-by-Step: JVM Internals

## Step 1: Compile a Simple Class
```bash
javac MyClass.java
```

## Step 2: Examine Bytecode
```bash
javap -c -p MyClass
# Shows bytecode instructions with line numbers
javap -v MyClass    # Verbose: constant pool, flags, stack map, exceptions
```

## Step 3: Run with GC Logging
```bash
java -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.log MyClass
```

## Step 4: Monitor Heap
```bash
jhsdb jmap --heap --pid <pid>
# Shows heap regions, used/total space, GC configuration
```

## Step 5: Take Thread Dump
```bash
jstack <pid>
# Shows all thread stacks, waiting states, locks held
```

## Step 6: Take Heap Dump
```bash
jmap -dump:live,format=b,file=heap.hprof <pid>
# Or use jcmd
jcmd <pid> GC.heap_dump heap.hprof
```

## Step 7: Analyze with JFR
```bash
# Start flight recording
jcmd <pid> JFR.start duration=60s filename=recording.jfr

# Dump
jcmd <pid> JFR.dump filename=recording.jfr
```

## Step 8: View JIT Compilation
```bash
java -XX:+PrintCompilation MyClass
# Shows methods compiled, tiers, timestamps
```

## Step 9: See Object Header Size
```bash
# Use JOL (Java Object Layout)
java -jar jol-cli.jar internals MyClass
# Shows object header size, field offsets, padding
```
