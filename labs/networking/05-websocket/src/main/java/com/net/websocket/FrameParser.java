package com.net.websocket;

import java.util.*;

public class FrameParser {

    public enum Opcode {
        CONTINUATION(0x0), TEXT(0x1), BINARY(0x2), CLOSE(0x8), PING(0x9), PONG(0xA);

        private final int code;
        Opcode(int code) { this.code = code; }
        public int getCode() { return code; }

        public static Opcode fromCode(int code) {
            for (Opcode o : values()) if (o.code == code) return o;
            throw new IllegalArgumentException("Unknown opcode: " + code);
        }
    }

    public static class WebSocketFrame {
        public final boolean fin;
        public final Opcode opcode;
        public final byte[] payload;
        public final boolean masked;

        public WebSocketFrame(boolean fin, Opcode opcode, byte[] payload, boolean masked) {
            this.fin = fin;
            this.opcode = opcode;
            this.payload = payload;
            this.masked = masked;
        }

        public String getPayloadAsText() {
            return new String(payload, java.nio.charset.StandardCharsets.UTF_8);
        }

        public byte[] encode() {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bos.write((fin ? 0x80 : 0) | opcode.getCode());

                int len = payload.length;
                if (len < 126) {
                    bos.write((masked ? 0x80 : 0) | len);
                } else if (len < 65536) {
                    bos.write((masked ? 0x80 : 0) | 126);
                    bos.write((len >> 8) & 0xFF);
                    bos.write(len & 0xFF);
                } else {
                    bos.write((masked ? 0x80 : 0) | 127);
                    for (int i = 56; i >= 0; i -= 8) {
                        bos.write((int)((long)len >> i) & 0xFF);
                    }
                }

                if (masked) {
                    byte[] mask = new byte[4];
                    new Random().nextBytes(mask);
                    bos.write(mask);
                    for (int i = 0; i < payload.length; i++) {
                        bos.write(payload[i] ^ mask[i % 4]);
                    }
                } else {
                    bos.write(payload);
                }

                return bos.toByteArray();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String toString() {
            return "Frame{fin=" + fin + ", opcode=" + opcode
                + ", payload=" + getPayloadAsText() + ", masked=" + masked + "}";
        }
    }

    private static class ByteArrayOutputStream extends java.io.ByteArrayOutputStream {
        public void write(int b) { super.write(b); }
    }

    public static void main(String[] args) {
        String message = "Hello, WebSocket!";

        WebSocketFrame frame = new WebSocketFrame(true, Opcode.TEXT,
            message.getBytes(java.nio.charset.StandardCharsets.UTF_8), false);

        System.out.println("Original: " + frame);
        byte[] encoded = frame.encode();
        System.out.println("Encoded frame size: " + encoded.length + " bytes");

        WebSocketFrame decoded = new WebSocketFrame(true, Opcode.TEXT,
            message.getBytes(java.nio.charset.StandardCharsets.UTF_8), true);
        System.out.println("Decoded payload: " + decoded.getPayloadAsText());
    }
}
