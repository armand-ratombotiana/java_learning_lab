# Debugging JVM Tuning Issues

## JVM Flag Verification
Use these commands to verify active flags:
```bash
java -XX:+PrintCommandLineFlags -version  # flags on command line
java -XX:+PrintFlagsFinal -version         # all flags with values
jcmd <pid> VM.flags                        # flags of running JVM
jcmd <pid> VM.print_flag <name>            # specific flag value
```

## Heap Issues
Diagnose heap problems with:
```bash
jstat -gc <pid> 1s                         # GC stats every second
jmap -heap <pid>                           # heap configuration summary
jcmd <pid> GC.heap_info                    # detailed heap info
jcmd <pid> GC.run                          # force GC
```

## Code Cache Issues
```bash
java -XX:+PrintCodeCache -version          # code cache configuration
jcmd <pid> Compiler.codecache              # code cache usage
jcmd <pid> Compiler.directives_add -       # compiler control directives
```

## Metaspace Issues
```bash
jcmd <pid> GC.class_stats                 # class statistics
jstat -gcmetacapacity <pid>               # metaspace capacity stats
jmap -clstats <pid>                       # class loader statistics
```

## GC Log Analysis
For comprehensive GC analysis:
```bash
-Xlog:gc*:file=gc.log:time,uptime,pid:filecount=5,filesize=20m
-Xlog:gc+heap=debug                       # heap details
-Xlog:gc+metaspace=debug                  # metaspace details
```

## Compilation Diagnostics
```bash
-XX:+PrintCompilation                     # compilation events
-XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining  # inlining decisions
-XX:+UnlockDiagnosticVMOptions -XX:+LogCompilation  # detailed XML log
```

## Common Tuning Scenarios
- **High GC overhead**: Increase heap, tune GC parameters, reduce allocation rate
- **Slow startup**: Use CDS (`-Xshare:auto`), reduce class path, use AppCDS
- **High Metaspace growth**: Check ClassLoader leaks, set MaxMetaspaceSize
- **Code cache full**: Increase ReservedCodeCacheSize, reduce compilation demand
- **Large page errors**: Verify OS huge page configuration, disable if unavailable
