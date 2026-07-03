# Code Deep Dive — I/O & NIO

## Efficient File Copy (NIO)
```java
public static void copyFile(Path source, Path target) throws IOException {
    try (FileChannel in = FileChannel.open(source, StandardOpenOption.READ);
         FileChannel out = FileChannel.open(target, 
             StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
        in.transferTo(0, in.size(), out); // OS-level zero-copy
    }
}
```

## Walking a Directory Tree
```java
Path start = Path.of("/app/logs");
try (Stream<Path> stream = Files.walk(start, 3)) {
    stream.filter(p -> p.toString().endsWith(".log"))
          .forEach(System.out::println);
}
```

## WatchService (File Change Monitor)
```java
WatchService watcher = FileSystems.getDefault().newWatchService();
path.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

WatchKey key = watcher.take(); // blocks
for (WatchEvent<?> event : key.pollEvents()) {
    Path changed = (Path) event.context();
    System.out.println(changed + " was " + event.kind().name());
}
```

## Memory-Mapped File Read
```java
try (FileChannel channel = FileChannel.open(path, READ)) {
    MappedByteBuffer mapped = channel.map(READ_ONLY, 0, channel.size());
    byte[] data = new byte[(int) channel.size()];
    mapped.get(data); // Direct memory read
    return new String(data, StandardCharsets.UTF_8);
}
```
