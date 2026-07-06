package com.javaacademy.lab33.security;

import java.security.*;
import java.util.HexFormat;

public class DigitalSignatureExample {

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    public byte[] sign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    public boolean verify(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }

    public String signToHex(String data, PrivateKey privateKey) throws Exception {
        return HexFormat.of().formatHex(sign(data.getBytes(), privateKey));
    }

    public boolean verifyFromHex(String data, String hexSignature, PublicKey publicKey) throws Exception {
        return verify(data.getBytes(), HexFormat.of().parseHex(hexSignature), publicKey);
    }
}
