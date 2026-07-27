# GraphQL — Mock Interview Questions

## Fundamentals (3 questions)

**Q1**: What is GraphQL? How does it compare to REST?

**Expected coverage**: Query language for APIs developed by Facebook, single endpoint (/graphql), client specifies exact data shape (fetch what you need), strongly typed schema, introspection, queries/mutations/subscriptions, avoids over-fetching and under-fetching, real-time via subscriptions (usually over WebSocket).

**Q2**: Explain the GraphQL type system. What are the basic building blocks?

**Expected coverage**: Scalar types (String, Int, Float, Boolean, ID), Object types (fields with types), Query and Mutation root types, Input types (for mutation arguments), Enum types, Interface and Union types, List and Non-Null wrappers ([String]!, [String!]!, [String!], String!).

**Q3**: What is a GraphQL resolver? How does data fetching work?

**Expected coverage**: Resolver function per field, parent argument for parent object, args for field arguments, context for shared state (DB, auth), info for query metadata (AST, field selection), default resolver (property lookup on parent), async resolvers, batching via DataLoader to solve N+1 problem.

## Intermediate (3 questions)

**Q4**: Explain the N+1 problem in GraphQL and how to solve it.

**Expected coverage**: Auto-generated resolvers fetch each related record individually (N queries for N items), DataLoader (batching + caching, coalesces individual loads into single batch, caches per request), batch load functions (WHERE id IN (...)), SQL joins vs separate queries, lookahead patterns.

**Q5**: How does GraphQL handle error handling and partial responses?

**Expected coverage**: Top-level errors array (non-null field failure propagates, nullable field returns null with error), error extensions (code, path, timestamp), errors in lists (item can be null with error entry), nullability design (required vs nullable fields), error masking (hide internals in production).

**Q6**: How does GraphQL handle authentication and authorization?

**Expected coverage**: Auth at transport layer (JWT in Authorization header), auth middleware in context population (decode token, attach user to context), field-level authorization (resolve conditionally based on user role), schema directives (@auth, @hasRole), data masking (return null for unauthorized fields), depth limiting and query cost analysis to prevent abuse.

## Advanced (2 questions)

**Q7**: Your GraphQL API is slow. How do you profile and optimize it?

**Expected coverage**: Query complexity analysis (introspection of deeply nested queries), Apollo Tracing/Federated Tracing for resolver timing, DataLoader optimization, query batching, persisted queries (reduced overhead), CDN caching for GET queries, response compression, connection limits, resolving the N+1 problem, pagination (Relay connection spec with cursors).

**Q8**: Compare GraphQL Subscriptions to WebSocket and gRPC streaming for real-time features.

**Expected coverage**: GraphQL Subscriptions (pub/sub over WebSocket, typed schema, subscription filter, best for event-driven UIs), WebSocket (full control, raw frames, best for low-latency bi-di), gRPC streaming (typed streaming, HTTP/2, best for service-to-service). GraphQL Subscriptions trade simplicity for flexibility compared to raw WebSocket.
