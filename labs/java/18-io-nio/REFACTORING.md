# Refactoring — I/O & NIO

## File → Path + Files
```java
// Before
File file = new File("/tmp/data.txt");
FileReader fr = new FileReader(file);
BufferedReader br = new BufferedReader(fr);

// After
Path path = Path.of("/tmp/data.txt");
List<String> lines = Files.readAllLines(path, UTF_8);
```

## Legacy Stream → NIO Channel
```java
// Before
try (FileInputStream fis = new FileInputStream("big.dat")) {
    int b; while ((b = fis.read()) != -1) { ... }
}

// After
try (FileChannel ch = FileChannel.open(Path.of("big.dat"), READ)) {
    MappedByteBuffer buf = ch.map(READ_ONLY, 0, ch.size());
    // Direct memory access
}
```

## Loop → Stream API
```java
// Before
BufferedReader br = Files.newBufferedReader(path);
String line; while ((line = br.readLine()) != null) { process(line); }

// After
try (Stream<String> lines = Files.lines(path, UTF_8)) {
    lines.forEach(this::process);
}
```

## Manual Close → try-with-resources
```java
// Before
Scanner s = null; try { s = new Scanner(f); ... } finally { if (s != null) s.close(); }

// After
try (Scanner s = new Scanner(f)) { ... }
```
