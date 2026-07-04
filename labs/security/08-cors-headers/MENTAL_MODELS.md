# Mental Models for 08-cors-headers

## The Bouncer Model (Authentication)

Think of authentication as a bouncer at a club entrance:
- The bouncer checks ID (credentials)
- Validates it against a trusted source
- Issues a stamp (session/token) for access
- Different levels of access map to authorization

## The Passport Model (JWT/Sessions)

JWTs and session tokens work like international passports:
- Contains identity information (claims)
- Issued by a trusted authority
- Has an expiration date
- Can be verified without contacting the issuer

## The Chain of Responsibility (Filter Chain)

Spring Security's filter chain is like an assembly line:
- Each station (filter) has a specific job
- Items (requests) pass through stations in order
- Any station can reject or modify the item

## The Vault Model (Encryption)

Encryption is like a secure vault:
- A lock (algorithm) protects contents
- A key (cryptographic key) operates the lock
- Symmetric encryption = one key to lock and unlock
- Asymmetric encryption = public key locks, private key unlocks

## Application

When designing security for a Java application, map each component to these mental models to ensure complete coverage.
