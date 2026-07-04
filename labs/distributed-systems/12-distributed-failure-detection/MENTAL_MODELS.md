# Mental Models for Failure Detection

## The Check-In Model
- Everyone texts "I'm OK" every hour
- If someone misses 3 check-ins, we call to check on them
- Heartbeat = text message, Timeout = 3 missed texts

## The Suspicion Model (Phi-Accrual)
- Your friend is usually 5 minutes late
- Today they're 30 minutes late: suspicious but not declared missing
- The more late they are, the more suspicious you get
- At some threshold, you declare them missing

## The Gossip Model
- You hear a rumor about Bob leaving early
- You tell Alice, who tells Charlie
- Pretty soon everyone knows
- No single point of truth, but everyone eventually knows
