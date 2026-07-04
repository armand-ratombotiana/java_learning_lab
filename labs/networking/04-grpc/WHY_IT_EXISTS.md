# gRPC - Why It Exists

## The Problem gRPC Solved

Traditional REST APIs struggle with:
1. **Performance**: JSON serialization is slow and verbose
2. **Streaming**: REST has no native streaming support
3. **Contract enforcement**: No strict typing across services
4. **Code generation**: Manual client SDK maintenance

## gRPC Advantages
- Binary serialization (Protobuf) is 5-10x faster than JSON
- Strongly typed contracts enforced by proto files
- Native streaming support (server, client, bidirectional)
- Automatic code generation for multiple languages
- Built-in authentication, load balancing, health checking
