package com.javaacademy.lab32.networking;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.net.*;
import java.io.*;

class NetworkingUltraDeepTest {

    @Test
    void urlParsingComponents() throws Exception {
        URL url = new URL("https://example.com:8080/path?query=1#ref");
        assertEquals("https", url.getProtocol());
        assertEquals("example.com", url.getHost());
        assertEquals(8080, url.getPort());
        assertEquals("/path", url.getPath());
        assertEquals("query=1", url.getQuery());
        assertEquals("ref", url.getRef());
    }

    @Test
    void inetAddressLocalhost() throws Exception {
        InetAddress local = InetAddress.getLocalHost();
        assertNotNull(local);
        assertNotNull(local.getHostName());
    }

    @Test
    void uriCreationAndResolution() throws Exception {
        URI base = new URI("https://example.com/base/");
        URI resolved = base.resolve("child");
        assertEquals("https://example.com/base/child", resolved.toString());
    }

    @Test
    void urlEncoderDecoder() {
        String encoded = URLEncoder.encode("hello world", java.nio.charset.StandardCharsets.UTF_8);
        assertEquals("hello+world", encoded);
        String decoded = URLDecoder.decode(encoded, java.nio.charset.StandardCharsets.UTF_8);
        assertEquals("hello world", decoded);
    }
}
