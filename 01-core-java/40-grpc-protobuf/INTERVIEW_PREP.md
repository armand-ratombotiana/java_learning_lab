# Module 40: gRPC & Protocol Buffers - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: Compare gRPC to a traditional JSON REST API. When would you choose one over the other?
**Answer**:
- **Payload Format**: REST typically uses JSON, which is text-based, highly verbose, and loosely typed. gRPC uses Protocol Buffers (Protobuf), which are strongly typed, heavily compressed, and serialized into binary. This makes gRPC payloads significantly smaller and much faster to parse.
- **Protocol**: REST usually runs over HTTP/1.1 (stateless, head-of-line blocking). gRPC *requires* HTTP/2, leveraging multiplexing, header compression, and persistent connections.
- **Contract**: REST relies on external documentation (like Swagger/OpenAPI) that can drift from the actual code. gRPC uses a `.proto` file as the absolute source of truth. If the `.proto` file compiles, the client and server are guaranteed to be compatible.
- **Use Case**: Use JSON REST for public-facing APIs (mobile apps, web browsers) because it is universally understood and easy to debug. Use gRPC for high-performance internal Microservice-to-Microservice (backend) communication.

### Q2: What happens if you rename a field in a `.proto` file? What happens if you change a field's tag number?
**Answer**:
- **Renaming a field**: Nothing breaks. In the compiled binary payload, Protobuf does not send the field's string name (e.g., "email_address") over the network to save space. It only sends the integer tag (e.g., `2`) and the data type. So, renaming the field in the source file has absolutely no effect on wire compatibility between old clients and new servers.
- **Changing a field tag number**: **Catastrophic failure**. If you change `string email = 2;` to `string email = 3;`, old clients will still send the email data tagged as `2`. The new server will look for tag `3`, see nothing, and assume the field is empty, resulting in complete data loss or deserialization errors.

### Q3: What is "Streaming" in gRPC, and why is it useful?
**Answer**:
Because gRPC uses HTTP/2, it supports long-lived, full-duplex communication channels. 
- In **Server Streaming**, the client sends one request, and the server pushes a continuous stream of responses (useful for pushing real-time pricing data or large datasets that don't fit in memory).
- In **Bidirectional Streaming**, both the client and server can send an asynchronous stream of messages to each other concurrently without waiting for standard request/response cycles (useful for live chat applications or multiplayer gaming logic).

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Deprecating a Protobuf Field
**Problem**: You have a `.proto` file with a `message User { string first_name = 1; string last_name = 2; }`. Your team decides they want to unify this into a single `string full_name`. How do you safely evolve the schema without breaking legacy clients that are already deployed in production?

**Solution**:
You cannot simply delete tags 1 and 2, because a junior developer might accidentally reuse tags 1 and 2 for something else later, which will cause data corruption when old clients talk to the new server. You must explicitly reserve the deleted tags.

```protobuf
message User {
  // Old fields are deprecated, but left here so the backend can still accept them if needed
  string first_name = 1 [deprecated = true];
  string last_name = 2 [deprecated = true];
  
  // Alternatively, if you actually delete them, you MUST reserve the tags:
  // reserved 1, 2;
  // reserved "first_name", "last_name";

  // New field starts at the next available tag
  string full_name = 3;
}
```
The Java backend logic should be updated to check: if `full_name` is populated, use it. If not, fallback to combining `first_name` and `last_name` for legacy clients.