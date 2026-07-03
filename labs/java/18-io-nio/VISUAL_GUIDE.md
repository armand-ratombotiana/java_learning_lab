# Visual Guide — I/O & NIO

```
Byte Stream Hierarchy:
InputStream (abstract)
├── FileInputStream        ← bytes from file
├── ByteArrayInputStream   ← bytes from array
├── BufferedInputStream    ← adds buffering
├── DataInputStream        ← reads primitives
├── ObjectInputStream      ← reads objects
└── FilterInputStream      ← decorator base

Character Stream Hierarchy:
Reader (abstract)
├── FileReader             ← chars from file
├── BufferedReader         ← add buffering + readLine()
├── InputStreamReader      ← byte→char bridge
└── StringReader           ← chars from String

NIO ByteBuffer Flow:
allocate → put → flip → get → compact → put → ...
            │               │
        Writing mode    Reading mode

Selector Loop:
[Channel1] ─┐
[Channel2] ─┤──► Selector ─► process events
[Channel3] ─┘
```
