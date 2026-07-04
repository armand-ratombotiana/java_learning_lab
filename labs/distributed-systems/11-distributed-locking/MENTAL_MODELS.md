# Mental Models for Distributed Locking

## The Bathroom Key Model
- Everyone needs the bathroom key to enter
- One person at a time
- If someone doesn't return it (crash), facility must timeout
- Fencing token: ticket number to prove you had the key

## The Meeting Room Model
- ZooKeeper: Sign-up sheet (sequential nodes)
- Lowest number on sheet gets the room
- When you leave, cross your name off
- Next person on sheet gets the room

## The Parking Spot Model
- Redlock: Must get tokens from 3/5 parking lots
- All lots must agree you have the spot
- Token has expiration time (lease)
- Must renew before expiration
