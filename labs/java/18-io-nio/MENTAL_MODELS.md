# Mental Models — I/O & NIO

## Stream as Water Pipe
Data flows through a pipe. You can read from one end or write to the other. Pipes can be chained (filter streams).

## Buffer as Reservoir
A `ByteBuffer` is a reservoir. You pump data in (put), redirect the flow (flip), then pump it out (get).

## Channel as Toll Road
A `FileChannel` is a direct highway to the file system — faster than using streams because it avoids user-space detours.

## Path as Address
`Path` is a file address (not the file itself). Use `Files` methods to read/write/check existence.

## Selector as Switchboard
A `Selector` monitors many channels (calls). When one has data, it rings — no need to check each one individually.

## Memory-Mapped File as Window
`MappedByteBuffer` is like looking through a window at a file. Changes you see through the window appear on the file directly.
