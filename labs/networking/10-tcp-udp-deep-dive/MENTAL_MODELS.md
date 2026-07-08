# TCP/UDP Deep Dive — Mental Models

## 1. The Pipe Model (TCP Flow)

Think of TCP as a **pipe** connecting two applications:

```
Sender ────[=== Data in Flight ====]──── Receiver
           ▲                         ▲
           │                         │
      Water (bits)              Faucet (read)
      entering pipe             draining pipe
```

**Key insights:**
- The pipe has limited capacity = Bandwidth × RTT (BDP)
- If you push water faster than the faucet drains, the pipe fills up (congestion)
- If the pipe fills completely, water backs up and pressure builds (bufferbloat)
- The optimal flow rate keeps the pipe full but not overflowing

**Application:**
- For maximum throughput, keep data always in flight (pipeline)
- For minimum latency, keep the pipe as empty as possible (BBR approach)
- Buffer sizing should match the pipe capacity (2× BDP for full duplex)

## 2. The Postal Service Model (UDP)

UDP is like sending **postcards**:

```
Sender ──→ Post Office ──→ Network ──→ Post Office ──→ Receiver
          (local)        (routing)     (remote)
```

**Key insights:**
- Each postcard is independent (no connection)
- Postcards may arrive out of order
- Some postcards get lost (no confirmation)
- There's a size limit (MTU = max postcard size)
- Very low overhead (just an address and stamp)

**Application:**
- Use for things where occasional loss is acceptable
- Include enough info in each message to be self-contained
- Fragment large data yourself to avoid IP fragmentation

## 3. The AIMD Thermostat (Congestion Control)

TCP congestion control works like a **thermostat with hysteresis**:

```
     │
Temp │   ┌──┐    ┌──┐    ┌──┐    ┌──┐
     │   │  │    │  │    │  │    │  │
     │   │  │    │  │    │  │    │  │
     │ ──┘  └────┘  └────┘  └────┘  └──
     │
     └────────────────────────────────── Time
         Additive Increase (slow warm up)
         Multiplicative Decrease (fast cool down)
```

**Insights:**
- Slowly probe for more (increase 1 unit at a time)
- When something goes wrong (loss), back off aggressively (halve)
- This conservative behavior ensures network stability
- Multiple flows sharing a link converge to fairness

## 4. The Window Model (Flow Control)

TCP flow control is like a **warehouse with a loading dock**:

```
Sender                    Receiver
┌──────────┐             ┌──────────┐
│ Warehouse│ ──→ Loading│  Dock    │ ──→ Store
│ (send    │     Dock   │  (recv   │     (app read)
│  buffer) │     (net)  │  buffer) │
└──────────┘             └──────────┘

Receiver says: "I have room for 10 more pallets" (window update)
Sender says: "I'll send 10, then wait for you to clear more space"
```

**Insights:**
- The sender never exceeds the receiver's advertised window
- Zero window means: stop sending, I'll tell you when I'm ready
- Silly Window Syndrome: avoid tiny window updates (Clark's algorithm)

## 5. The Clock Model (RTT and Timing)

Network timing is like a **clock with variable tick speed**:

```
Base RTT (no load):  [----20ms----]
Under load:          [----20ms----====30ms====] (queueing delay)
Congestion:          [----20ms----==========200ms==========]
```

**Insights:**
- Base RTT = propagation delay (fixed by physics)
- Variable delay = queueing (indicates congestion)
- RTT variance is a better congestion signal than RTT itself
- BBR uses min RTT (not current RTT) as the base estimate

## 6. The Waterfall Model (Nagle's Algorithm)

Nagle is like a **bucket brigade** that waits until the bucket is full:

```
App writes:  ║  ║  ║  ║  ║  ║ ║║║║║║║║║║
             ║ 1│ 2│ 3│ 4│ 5│ 6║ 7║8║9║...║1460
             ║  ║  ║  ║  ║  ║ ║║║║║║║║║║
             ║ ←────── Wait ──────→ │
             ║                   Bucket full = Send!
             ▼
         Network
```

**Without Nagle**: 1460 packets of 1 byte each (wasteful)
**With Nagle**: 1 packet of 1460 bytes (efficient, but 1-second delay)

## 7. The Chess Game Model (TCP State Machine)

TCP state transitions are like a **chess game with limited moves**:

```
Opening (3-way handshake):
  White plays e4 (SYN)
  Black plays e5 (SYN+ACK)  
  White plays Nf3 (ACK)

Middlegame (ESTABLISHED):
  Exchange pieces (data/ACKs)

Endgame (Termination):
  White resigns (FIN)
  Black accepts (ACK)
  Black resigns (FIN)
  White acknowledges (ACK)
  White leaves the board (TIME_WAIT → CLOSED)
```

**Insights:**
- There are only 11 possible states
- Specific events cause specific transitions
- Invalid transitions are errors (like illegal chess moves)
- TIME_WAIT ensures the final ACK isn't lost

## 8. The Radio Station Model (UDP Multicast)

Multicast is like a **radio broadcast**:

```
Station  ──→ Transmitter ──→ Airwaves ──→ Receiver 1
(239.0.0.1)                     │          Receiver 2
                                │          Receiver 3
                                │
                           Anyone tuned to
                           88.5 FM (joined group)
                           can receive
```

**Insights:**
- One sender, many receivers (potentially millions)
- Receivers "tune in" by joining the group (IGMP)
- Sender doesn't know who's listening
- No per-receiver state management needed

## 9. The Assembly Line Model (Reliable UDP)

R-UDP is like an **assembly line with quality control**:

```
Part 1 ──→ [QC Check] ──→ Accept ──→ Assemble
Part 2 ──→ [QC Check] ──→ Reject ──→ Request replacement
Part 3 ──→ [QC Check] ──→ Accept ──→ Wait (part 2 missing)
Part 2 ──→ [QC Check] ──→ Accept ──→ Assemble 2,3 (finally!)
```

**Insights:**
- Each part needs a serial number (seq num)
- Quality control = checksum verification
- Rejections trigger re-orders (retransmissions)
- Out-of-order parts sit in a holding area (reorder buffer)
- Only contiguous parts can be assembled

## 10. The Toll Road Model (BDP)

Bandwidth-Delay Product is like a **toll road**:

```
On-ramp ────[Cars in transit]──── Off-ramp
(Sender)      (BDP = 100 cars)    (Receiver)

Speed limit = Bandwidth (100 cars/sec)
Travel time = RTT (0.5 sec)
Cars in transit = 100 × 0.5 = 50 cars
```

**Insights:**
- If on-ramp releases < 50 cars at once, road is underutilized
- If on-ramp releases > 50 cars, they queue at the off-ramp (bufferbloat)
- Optimal release rate keeps exactly the right amount in transit
- For a road with 0.5 sec travel time and 100 cars/sec capacity, release 50 cars initially

These mental models make abstract networking concepts tangible and easier to reason about.
