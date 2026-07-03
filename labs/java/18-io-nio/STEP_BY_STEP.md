# Step by Step — I/O & NIO

## Step 1: Classic InputStream (byte-oriented)
```java
try (InputStream is = new FileInputStream("data.bin")) {
    byte[] buf = new byte[4096];
    int read;
    while ((read = is.read(buf)) != -1) {
        process(buf, read);
    }
}
```

## Step 2: Add Buffering
```java
try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream("data.bin"))) {
    byte[] buf = new byte[4096];
    int read;
    while ((read = bis.read(buf)) != -1) {
        process(buf, read);
    }
}
```

## Step 3: Character-Oriented (Reader)
```java
try (BufferedReader br = new BufferedReader(new FileReader("data.txt", UTF_8))) {
    String line;
    while ((line = br.readLine()) != null) {
        process(line);
    }
}
```

## Step 4: NIO.2 Path & Files (simpler API)
```java
List<String> lines = Files.readAllLines(Path.of("data.txt"), UTF_8);
```

## Step 5: Stream Integration (Java 8+)
```java
try (Stream<String> lines = Files.lines(Path.of("data.txt"), UTF_8)) {
    lines.filter(l -> !l.isBlank())
         .limit(100)
         .forEach(System.out::println);
}
```

## Step 6: NIO Channel (performance)
```java
try (FileChannel ch = FileChannel.open(Path.of("data.bin"), READ)) {
    ByteBuffer buf = ByteBuffer.allocateDirect(8192); // off-heap
    while (ch.read(buf) != -1) {
        buf.flip();
        process(buf);
        buf.clear();
    }
}
```
