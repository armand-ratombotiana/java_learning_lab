# gRPC — Mock Interview Questions

## Fundamentals (3 questions)

**Q1**: What is gRPC? How does it compare to REST?

**Expected coverage**: RPC framework by Google, Protocol Buffers for serialization, HTTP/2 transport, strong typing with .proto files, code generation in 11+ languages, streaming (unary, server-stream, client-stream, bidirectional), performance benefits (binary serialization, HTTP/2 multiplexing).

**Q2**: Explain Protocol Buffers. How does it achieve smaller and faster serialization than JSON?

**Expected coverage**: .proto schema definition, varint encoding for integers, field wire types (0=varint, 1=64-bit, 2=length-delimited, 3=start group, 4=end group, 5=32-bit), tagged fields (field number + wire type = 1-5 bytes key), no schema in each message (unlike JSON field names), backward compatibility via field numbers and optional/required rules.

**Q3**: What are the four types of gRPC service methods?

**Expected coverage**: Unary (request → response), Server streaming (request → stream of responses), Client streaming (stream of requests → response), Bidirectional streaming (both sides stream independently). HTTP/2 multiplexing enables independent streams within single TCP connection.

## Intermediate (3 questions)

**Q4**: Explain gRPC error handling and status codes.

**Expected coverage**: Status codes defined in google.rpc.Code: OK, Canceled, Unknown, InvalidArgument, DeadlineExceeded, NotFound, AlreadyExists, PermissionDenied, Unauthenticated, ResourceExhausted, FailedPrecondition, Aborted, OutOfRange, Unimplemented, Internal, Unavailable, DataLoss. Rich error model with ErrorInfo, RetryInfo, DebugInfo, QuotaFailure.

**Q5**: How does gRPC handle load balancing? What are the considerations?

**Expected coverage**: Client-side load balancing (pick_first, round_robin), look-aside DNS resolution, subchannel connections to each backend, health checking via gRPC Health Checking Protocol, weighted targets, least_request policy, xDS-based traffic management for advanced routing.

**Q6**: What is gRPC deadline propagation? Why is it important?

**Expected coverage**: Each gRPC call can set a deadline/timeout, propagated to downstream services via gRPC metadata, prevents cascading failures in distributed systems, enables early cancellation, TimeToLive in protobuf headers, client deadline > server deadline for safety.

## Advanced (2 questions)

**Q7**: You are migrating a REST API to gRPC. What challenges do you anticipate?

**Expected coverage**: Browser support (needs gRPC-web proxy), caching (no native HTTP caching — implement at application layer), debugging (binary format — need grpcurl, gRPC reflection), load balancer compatibility (needs HTTP/2), learning curve (proto files, code generation), API gateway integration (Envoy, Kong, Traefik).

**Q8**: Compare gRPC streaming vs WebSocket. When do you reach for each?

**Expected coverage**: gRPC (structured streaming, typed messages, multiplexed streams over single HTTP/2 connection, code-generated clients, ideal for microservices), WebSocket (raw bidirectional, lower-level, browser-native API, ideal for real-time user-facing features like chat and gaming), gRPC for internal services, WebSocket for client-facing real-time.
