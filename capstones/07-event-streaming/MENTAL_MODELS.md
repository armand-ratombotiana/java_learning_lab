# Mental Models: Event Streaming

- **Topic = Mailbox Category**: Events (letters) go into topics (mailboxes) by type.
- **Partition = Ordered Letterbox**: Events within a partition have strict order; like numbered letters in a mailbox.
- **Broker = Post Office**: Physical server storing partitions and serving requests.
- **Producer = Sender**: Writes letters to the appropriate mailbox.
- **Consumer = Recipient**: Reads letters from assigned mailboxes, tracking the last read letter.
- **Consumer Group = Reading Club**: Members split the mailboxes; each letter is read by exactly one member.
- **Offset = Page Number**: Sequential index of events within a partition.
- **Log Compaction = Filing Cabinet**: Only keep the latest document per file (key), discard older versions.
