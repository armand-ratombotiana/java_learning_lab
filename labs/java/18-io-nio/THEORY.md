# Theory — I/O and NIO

## Byte Streams (InputStream / OutputStream)
Handle binary data (bytes):
```java
InputStream in = new FileInputStream("file.bin");
int b = in.read(); // single byte (0-255)
byte[] buf = new byte[4096];
int bytesRead = in.read(buf);
in.close();
```

## Character Streams (Reader / Writer)
Handle text (chars, using encoding):
```java
Reader r = new FileReader("file.txt", StandardCharsets.UTF_8);
int c = r.read(); // single char
BufferedReader br = new BufferedReader(r);
String line = br.readLine();
```

## NIO.2 Overview (Java 7+)
New I/O with `Path`, `Files`, `FileSystem`, `FileVisitor`:
```java
Path path = Path.of("/tmp/file.txt");
List<String> lines = Files.readAllLines(path);
Files.write(path, "Hello".getBytes());
```

## Channels & Buffers
`FileChannel`, `SocketChannel`, `ByteBuffer` — low-level, non-blocking I/O:
```java
FileChannel channel = FileChannel.open(path, READ);
ByteBuffer buf = ByteBuffer.allocate(4096);
channel.read(buf);
```

## Memory-Mapped Files
Map file content directly to memory:
```java
MappedByteBuffer mbb = channel.map(READ_ONLY, 0, channel.size());
```
