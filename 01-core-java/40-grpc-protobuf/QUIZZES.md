# Module 40: gRPC & Protocol Buffers - Quizzes

---

## Q1: Protocol Buffers Advantages
What is the primary advantage of using Protocol Buffers over JSON for inter-service communication?

A) Protocol Buffers are human-readable text files just like JSON.
B) Protocol Buffers are natively understood by web browsers.
C) Protocol Buffers serialize data into a compact, binary format, resulting in much faster parsing and smaller payloads over the network.
D) Protocol Buffers require no predefined schema.

**Answer**: C
**Explanation**: Protobuf encodes data in binary. Unlike JSON, it doesn't send the field names (keys) over the wire, only the assigned integer tags and the values, saving significant bandwidth and CPU cycles during parsing.

---

## Q2: HTTP/2 in gRPC
Why does gRPC mandate the use of HTTP/2?

A) Because HTTP/1.1 cannot support binary data.
B) Because HTTP/2 supports multiplexing, allowing multiple concurrent requests and responses (including bidirectional streaming) over a single persistent TCP connection.
C) Because HTTP/2 is open source, whereas HTTP/1.1 is proprietary.
D) To force developers to buy new servers.

**Answer**: B
**Explanation**: HTTP/2 multiplexing removes the need to constantly open and close TCP connections (head-of-line blocking). It also allows full-duplex bidirectional streaming, which is a core feature of gRPC.

---

## Q3: Backward Compatibility
In a `.proto` file, what is the significance of the integer assigned to a field (e.g., `string email = 2;`)?

A) It determines the maximum length of the string.
B) It represents the order the field will appear on a UI.
C) It is the unique tag used to identify the field in the binary payload. Changing it breaks backward compatibility.
D) It is an initial default value.

**Answer**: C
**Explanation**: The field number (tag) is critical. In the binary message, only this number is used to represent the field (not the string "email"). If you change the number in a later version, old clients will not be able to deserialize the new messages correctly.