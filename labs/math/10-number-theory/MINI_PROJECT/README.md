# Mini-Project: RSA Encrypted Chat

## Objective
Build a simple encrypted chat system that demonstrates RSA key exchange and message encryption.

## Requirements
1. Generate RSA key pairs (2048-bit recommended)
2. Exchange public keys between two parties
3. Encrypt messages with recipient's public key
4. Decrypt messages with your private key
5. Handle text encoding properly (convert String to BigInteger and back)

## Extensions
- Add message signing for authenticity
- Implement hybrid encryption (RSA + AES)
- Add a simple protocol layer (message format, sequence numbers)
- Create a networked version using sockets

## Evaluation Criteria
- Correct encryption/decryption
- Proper key generation (adequate bit length)
- Error handling for invalid messages
- Code organization and documentation
