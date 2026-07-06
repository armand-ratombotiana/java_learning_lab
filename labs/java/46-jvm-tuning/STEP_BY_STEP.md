# Step-by-Step: JVM Tuning

## Step 1: Run HeapSizingDemo.java
Execute with different heap sizes:
```bash
java -Xms256m -Xmx256m com.javaacademy.lab46.jvm.HeapSizingDemo
java -Xms1g -Xmx1g com.javaacademy.lab46.jvm.HeapSizingDemo
java -Xms4g -Xmx4g com.javaacademy.lab46.jvm.HeapSizingDemo
```
Compare allocation times and note GC activity.

## Step 2: Run CodeCacheDemo.java
Execute with `-XX:+PrintCodeCache`:
```bash
java -XX:+PrintCodeCache -jar lab46-jvm-tuning.jar
```
Observe the code cache usage before and after the 1000 proxies. The hot methods fill the non-profiled nmethod heap.

## Step 3: Run MetaspaceDemo.java
Execute with Metaspace logging:
```bash
java -Xlog:gc+metaspace* -jar lab46-jvm-tuning.jar
```
Observe Metaspace usage growing as classes are generated and loaded.

## Step 4: Run StringDedupDemo.java
Execute with and without string dedup:
```bash
java -XX:+UseStringDeduplication -jar lab46-jvm-tuning.jar
```
Note the memory savings from `String.intern()`.

## Step 5: Run JvmFlagReporter.java
Execute and observe the output. This shows the current JVM configuration. Compare with expected flags.

## Step 6: Experiment with Different Flag Combinations
```bash
java -Xms512m -Xmx2g -Xmn512m -XX:NewRatio=3 -XX:+PrintFlagsFinal 2>&1 | grep -E "MaxHeapSize|NewSize|NewRatio"
```

## Step 7: Run JvmTuningTest.java
Execute JUnit tests to verify all tuning demo code works correctly.
