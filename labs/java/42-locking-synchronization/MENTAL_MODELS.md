# Mental Models for Locking

## synchronized as a Keyhole
Think of synchronized as a keyhole for a restroom. When a thread enters a synchronized block, it takes the key (monitor). Other threads line up outside (BLOCKED state). If the key-holding thread needs another key (nested synchronized), Java allows reentrant entry (the key has a chain showing who holds it).

## AQS as a Bouncer with a Guest List
AQS is a nightclub bouncer. The `state` field is the number of people in the VIP section. `tryAcquire` asks the bouncer "can I enter?" — if state allows, the bouncer says yes (returns true). If not, the thread joins the CLH queue (the velvet rope line). When a thread leaves, the bouncer signals the next in line (`release` calls `unparkSuccessor`).

## StampedLock as a Photographer
A photographer (writer) needs exclusive access to frame a shot. Visitors (readers) can look through the viewfinder simultaneously (read lock). But some visitors just glance at the scene (optimistic read) — they don't block the photographer and only check later if the scene changed (validate).

## LockSupport as a Walkie-Talkie
`park()` is like putting your walkie-talkie on silent. You're not listening for calls but you can still receive them (the permit accumulates). `unpark()` is like the dispatcher calling you — if you're listening, you respond immediately; if not, you have a pending message (permit). Unlike wait/notify, unpark can be called before park without the thread missing the signal.

## CAS as a Fridge Note
Imagine a shared fridge where family members write their names on leftovers. CAS is like checking the note before adding yours: "If the note still says 'leftovers' (expected value), change it to 'taken by Alice' (new value). If someone else already took it, try again." The retry loop handles contention.

## CLH Queue as a Winter Jacket Line
In a CLH queue, each waiting thread spins on its predecessor's status. Like people in a line for winter jackets: each person watches the person ahead. When the front person gets a jacket, they tap the next person, who taps the next, etc. The queue is lock-free because each thread only modifies its own node.
