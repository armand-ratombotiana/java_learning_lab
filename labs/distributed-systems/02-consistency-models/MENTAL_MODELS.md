# Mental Models for Consistency

## The Timeline Model
Each consistency model defines what a "correct" history looks like:
- **Linearizable**: Single timeline matching wall-clock time
- **Sequential**: Single timeline matching program order
- **Causal**: DAG of causally related events
- **Eventual**: Multiple timelines that eventually merge

## The Shared Whiteboard Model
- **Strong consistency**: Whiteboard with global lock (only one person writes at a time)
- **Causal consistency**: Everyone sees edits in cause-and-effect order
- **Eventual consistency**: Copies of whiteboard that sync periodically

## The Phone Call Model
- **Linearizable**: Everyone on the same phone line hears everything in order
- **Causal**: Important announcements reach everyone in order, side conversations can be out of order
- **Eventual**: Voicemail messages that eventually reach all recipients
