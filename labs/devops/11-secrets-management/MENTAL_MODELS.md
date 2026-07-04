# Mental Models for Secrets Management

## 1. Bank Vault Analogy
- **Vault server** = Bank vault (secure storage, limited access)
- **Secrets** = Cash, jewelry, documents stored in safety deposit boxes
- **Auth methods** = ID verification before entering bank
- **Policies** = Which safety deposit boxes you can open
- **Seal/Unseal** = Bank vault is locked at night (sealed), opened by multiple key holders (unseal)
- **Audit log** = Security camera footage

## 2. Dynamic Secrets as Hotel Key Cards
Static passwords = permanent house key (dangerous if lost). Dynamic secrets = hotel key card (works for your stay, expires at checkout). If lost, the card expires anyway.

## 3. Lease as Library Book
Dynamic secrets are like library books — you borrow them for a limited time (lease), you can renew (lease renewal), but eventually they're returned (revoked). If you don't return them, they expire automatically.

## 4. Secrets as Nuclear Launch Codes
Treat secrets like nuclear launch codes: never write them down, authenticate before access, split authority (multiple unseal keys), log every access, rotate frequently, and short TTL.
