# Real-World Project: Secure File Encryption Toolkit

## Objective
Build a production-quality file encryption tool using RSA for key exchange and AES for bulk encryption.

## Architecture
1. **Key Management**: Generate and store RSA key pairs (PEM format)
2. **Key Exchange**: RSA-encrypted AES session key
3. **Bulk Encryption**: AES-256-GCM for file content
4. **File Format**: Custom format with header (metadata, encrypted key, IV, ciphertext)
5. **Verification**: HMAC or digital signature for integrity

## Components
- KeyGenerator: creates RSA key pairs
- FileEncryptor: encrypts files with hybrid scheme
- FileDecryptor: decrypts files
- KeyStore: manages public/private key storage
- Signer: digital signatures for authenticity

## Evaluation Criteria
- Security (proper key sizes, authenticated encryption)
- Performance (acceptable encryption/decryption speed)
- Usability (command-line interface)
- Robustness (handles large files, errors gracefully)
- Format interoperability (standard formats where possible)
