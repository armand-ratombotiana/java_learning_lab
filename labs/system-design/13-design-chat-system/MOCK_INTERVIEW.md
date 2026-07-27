# Mock Interview: Chat System Design

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Backend Engineer Interviewer  
**Candidate Level**: Senior Engineer (L5)  
**Problem**: Design a real-time chat system like WhatsApp or Messenger.

---

## Transcript

**Interviewer**: "Design a messaging system for 1B users. Requirements: 1:1 and group chat, message delivery in <100ms, multi-device sync, messages persisted for 30 days, end-to-end encryption."

**Candidate**: "Let me estimate scale. 100B messages/day → ~1.2M QPS writes, ~6M QPS reads. 500M concurrent users → 500M persistent connections. Storage: 100B × 200 bytes = 20TB/day, with 30-day retention: 600TB."

**Interviewer**: "Start with the connection management."

**Candidate**: "Persistent TCP connections using a custom protocol (WebSocket or MQTT-inspired). Connection Manager service — each user is assigned to a Connection Manager based on their user_id hash. The CM maintains a map of user_id → WebSocket connection. If the user has multiple devices, each device gets a separate connection linked to the same user_id."

**Interviewer**: "How does message sending work?"

**Candidate**: "User A sends message → Connection Manager for A → Message Router. Message Router: 1) stores message in Message Store (HBase/Cassandra keyed by conversation_id), 2) looks up recipients, 3) forwards to each recipient's Connection Manager, 4) delivers via persistent connection. For group chat: sender sends once, Message Router duplicates."

**Interviewer**: "How do you ensure message ordering?"

**Candidate**: "Each message gets a server-assigned sequence number (monotonically increasing per conversation). The Message Router assigns the sequence number when storing. Messages are delivered in sequence number order. The client displays in order — if there's a gap, it shows a loading indicator and waits for the gap to fill."

**Interviewer**: "What about offline messages?"

**Candidate**: "If a recipient is offline, the Message Store keeps the message. When the user reconnects, their Connection Manager requests all messages since their last acknowledged message_id (the `last_read_message_id` per conversation). The server sends a batch of missed messages. This is also how multi-device sync works — each device tracks its own `last_seen_message_id`."

**Interviewer**: "How do you implement end-to-end encryption?"

**Candidate**: "Signal Protocol (Double Ratchet algorithm). Each device generates a key pair. The public key is uploaded to a Key Directory Service. When User A messages User B: 1) A fetches B's public key from Key Directory, 2) A encrypts message with B's public key, 3) The server stores and forwards the encrypted payload, 4) B decrypts with private key. The server never has the decryption key."

**Interviewer**: "How do you detect and prevent spam?"

**Candidate**: "Rate limiting per user: max N messages/minute to new recipients, rate limiting per conversation. ML-based spam classifier analyzes message content (metadata, not content for E2E conversations). For E2E encrypted messages, use metadata features only (frequency, recipient diversity, account age). Abuse reporting: users report messages → manual review → account action."

---

## Key Takeaways

- **Connection Manager**: Persistent connection handler, sharded by user
- **Message Router**: Store + forward with server-assigned sequence numbers
- **Group chat**: Sender fanout — send once, server duplicates
- **Offline sync**: Store-and-forward with last_seen tracking
- **E2E encryption**: Signal Protocol, server acts as opaque relay
- **Spam detection**: Metadata-based for encrypted messages, content-based for others
