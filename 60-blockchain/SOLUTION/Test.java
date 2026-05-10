package com.learning.blockchain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlockchainSolutionTest {

    private BlockchainSolution solution;

    @BeforeEach
    void setUp() {
        solution = new BlockchainSolution();
    }

    @Test
    void testLoadCredentials() {
        Credentials creds = solution.loadCredentials("0xabc123");
        assertNotNull(creds);
    }

    @Test
    void testCreateContractDefinition() {
        BlockchainSolution.SmartContract contract = solution.createContractDefinition("test", new String[]{"func1"});
        assertNotNull(contract);
        assertTrue(contract.getAbi().contains("test"));
    }

    @Test
    void testCreateToken() {
        BlockchainSolution.TokenContract token = solution.createToken("MyToken", "MTK", BigInteger.valueOf(1000000));
        assertEquals("MyToken", token.getName());
        assertEquals("MTK", token.getSymbol());
        assertEquals(BigInteger.valueOf(1000000), token.getTotalSupply());
    }

    @Test
    void testTransferTokens() {
        BlockchainSolution.TokenContract token = solution.createToken("Test", "TST", BigInteger.valueOf(1000));
        token.transfer("0x1234", BigInteger.valueOf(100));
        assertEquals(BigInteger.valueOf(100), token.balanceOf("0x1234"));
    }

    @Test
    void testCreateWallet() {
        BlockchainSolution.Wallet wallet = solution.createWallet();
        assertNotNull(wallet);
        assertNotNull(wallet.getAddress());
    }

    @Test
    void testWalletFromPrivateKey() {
        BlockchainSolution.Wallet wallet = solution.createWalletFromPrivateKey("0xabcdef123456");
        assertNotNull(wallet.getAddress());
    }
}