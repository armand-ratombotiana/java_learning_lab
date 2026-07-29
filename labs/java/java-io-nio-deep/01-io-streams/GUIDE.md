# Deep Dive: I/O Streams

## 1. Stream Hierarchy

Java I/O is built on the decorator pattern. Byte streams (`InputStream`/`OutputStream`) handle binary data; character streams (`Reader`/`Writer`) handle text.

### Byte Streams

```java
// FileInputStream — reading raw bytes from a file
try (FileInputStream fis = new FileInputStream("data.bin")) {
    int b;
    while ((b = fis.read()) != -1) {
        // process byte b
    }
}

// BufferedInputStream — wraps any InputStream for efficiency
try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream("data.bin"))) {
    byte[] buf = new byte[8192];
    int n;
    while ((n = bis.read(buf)) != -1) {
        // process n bytes
    }
}
```

### Character Streams

```java
// InputStreamReader — bridge from bytes to characters
try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data.txt"), StandardCharsets.UTF_8))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
}
```

## 2. Data Streams

`DataInputStream`/`DataOutputStream` read/write Java primitives in a portable binary format:

```java
// Writing primitives
try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("data.bin"))) {
    dos.writeInt(42);
    dos.writeDouble(3.14);
    dos.writeUTF("Hello");
}

// Reading primitives (same order!)
try (DataInputStream dis = new DataInputStream(new FileInputStream("data.bin"))) {
    int i = dis.readInt();
    double d = dis.readDouble();
    String s = dis.readUTF();
}
```

## 3. PushbackInputStream

Allows "unreading" bytes — useful for parsing:

```java
try (PushbackInputStream pbs = new PushbackInputStream(new FileInputStream("data.bin"), 8)) {
    int b = pbs.read();
    if (b != expected) {
        pbs.unread(b);  // put the byte back
    }
}
```

## 4. SequenceInputStream

Concatenates multiple `InputStream`s into a single stream:

```java
Vector<InputStream> streams = new Vector<>();
streams.add(new FileInputStream("part1.bin"));
streams.add(new FileInputStream("part2.bin"));
try (SequenceInputStream sis = new SequenceInputStream(streams.elements())) {
    // reads from part1 then part2 seamlessly
}
```

## 5. try-with-resources

All stream classes implement `AutoCloseable`:

```java
try (FileInputStream fis = new FileInputStream("file.bin");
     BufferedInputStream bis = new BufferedInputStream(fis)) {
    // resources auto-closed in reverse order
}
```
