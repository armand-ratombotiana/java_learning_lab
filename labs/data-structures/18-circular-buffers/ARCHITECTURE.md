# Architecture

Circular buffers integrate as:
`
Producer â†’ CircularBuffer â†’ Consumer
`

Used in pipelines:
`
Network â†’ PacketBuffer â†’ Decoder â†’ AudioBuffer â†’ Speaker
`

Design patterns:
- Producer-Consumer pattern
- Pipeline pattern
- Observer pattern (with buffer)
