# Module 40: gRPC & Protocol Buffers - Edge Cases & Pitfalls

---

## Pitfall 1: Modifying Field Numbers in .proto Files

### ❌ Wrong
Changing the integer tag number assigned to a field in an existing `.proto` file when updating the schema.
```protobuf
// V1
message User {
  string name = 1;
}

// V2 (❌ Breaking Change!)
message User {
  string name = 2; // Tag changed!
  int32 age = 1;
}
```

### ✅ Correct
Field tags (`= 1`, `= 2`) dictate how the data is encoded in binary. Changing them breaks backward compatibility. If a field is removed, its tag should be marked as `reserved` to prevent accidental reuse.
```protobuf
// V2 (✅ Safe Change)
message User {
  string name = 1;
  int32 age = 2;
}
```

---

## Pitfall 2: Treating gRPC like a REST API

### ❌ Wrong
Building a public-facing API for browsers directly in gRPC. Browsers do not fully support HTTP/2 trailing headers, making direct gRPC calls from standard JavaScript fetch APIs impossible.

### ✅ Correct
Use gRPC heavily for internal, backend-to-backend microservice communication where performance is critical. If web clients need to access it, use a proxy like Envoy with `grpc-web`, or an API Gateway that translates REST/JSON from the browser into gRPC for the backend.

---

## Pitfall 3: Blocking the gRPC Event Loop

### ❌ Wrong
Executing slow, synchronous database queries directly inside the gRPC server callback thread. gRPC uses a non-blocking Netty event loop under the hood. Blocking it will starve the server and cause connection timeouts.

### ✅ Correct
Similar to Reactive programming, offload heavy blocking tasks to a dedicated executor thread pool, or use asynchronous database drivers (like R2DBC) to keep the gRPC threads free to process incoming requests.