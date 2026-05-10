# Blockchain Solution

## Overview
This module covers Web3j and smart contracts.

## Key Features

### Web3j Client
- Creating Web3j instance
- Connecting to Ethereum nodes

### Credentials
- Loading credentials from private key
- Wallet management

### Smart Contracts
- Creating contract definitions
- Deploying contracts
- Calling contract functions

### Token Contracts
- Creating ERC-20 tokens
- Transferring tokens
- Balance queries

### Wallet
- Creating wallets
- Managing addresses
- Balance tracking

## Usage

```java
BlockchainSolution solution = new BlockchainSolution();

// Load credentials
Credentials credentials = solution.loadCredentials("0xprivatekey");

// Create contract definition
SmartContract contract = solution.createContractDefinition("MyContract", new String[]{"set", "get"});

// Create token
TokenContract token = solution.createToken("MyToken", "MTK", BigInteger.valueOf(1000000));
token.transfer("0xrecipient", BigInteger.valueOf(100));

// Create wallet
Wallet wallet = solution.createWallet();
```

## Dependencies
- Web3j
- JUnit 5
- Mockito