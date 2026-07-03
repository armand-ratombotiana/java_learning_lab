# Debugging: JVM Internals

## Tools

### jps - List Java Processes
```bash
jps -lvm   # Show PID, main class, JVM flags
```

### jstat - GC Statistics
```bash
jstat -gcutil <pid> 1000 10   # Every 1s, 10 times
# S0/S1: survivor utilization, E: eden, O: old, M: metaspace
# YGC/YGCT: young GC count/time, FGC/FGCT: full GC count/time
```

### jmap - Memory Map
```bash
jmap -heap <pid>               # Heap summary
jmap -histo:live <pid>         # Live object histogram
jmap -dump:live,format=b,file=dump.hprof <pid>   # Heap dump
```

### jstack - Thread Dump
```bash
jstack -l <pid>                 # Thread dump with lock info
jstack -m <pid>                 # Mixed (Java + native) frames
```

### jcmd - Versatile Diagnostic
```bash
jcmd <pid> help                          # Available commands
jcmd <pid> VM.version                    # JVM version
jcmd <pid> VM.flags                      # JVM flags
jcmd <pid> VM.command_line               # Command line
jcmd <pid> GC.class_stats                # Class statistics
jcmd <pid> Thread.print                  # Thread dump
jcmd <pid> GC.heap_info                  # Heap info
jcmd <pid> VM.native_memory              # Native memory (requires -XX:NativeMemoryTracking=summary)
```

### JFR - Java Flight Recorder
```bash
jcmd <pid> JFR.start duration=60s filename=recording.jfr
jcmd <pid> JFR.dump filename=recording.jfr
```

## GC Log Analysis
```bash
# Modern GC logging (Java 9+)
java -Xlog:gc*:file=gc.log:time,level,tags MyApp

# Analyze with GCeasy or gceasy.io
# Look for: pause times > 200ms, full GC frequency, heap occupancy trends
```

## Common Debugging Scenarios
- **High CPU**: Thread dump → look for RUNNABLE threads in hot methods
- **OutOfMemoryError**: Heap dump → find largest objects, leak suspects
- **Deadlock**: Thread dump → look for BLOCKED threads waiting on the same locks
- **Long GC pauses**: GC log → check full GC frequency, old gen occupancy
