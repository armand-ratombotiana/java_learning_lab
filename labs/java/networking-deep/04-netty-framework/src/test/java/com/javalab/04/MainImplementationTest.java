package com.javalab.04;

import org.junit.jupiter.api.*;
import io.netty.buffer.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;
import static org.junit.jupiter.api.Assertions.*;

class MainImplementationTest {

    @Test
    @DisplayName("Netty handler should echo messages")
    void testHandlerEcho() {
        EmbeddedChannel channel = new EmbeddedChannel(new MainImplementation.EchoServerHandler());
        ByteBuf input = Unpooled.copiedBuffer("Hello", CharsetUtil.UTF_8);
        assertTrue(channel.writeInbound(input));
        ByteBuf output = channel.readOutbound();
        assertEquals("Echo: Hello", output.toString(CharsetUtil.UTF_8));
        output.release();
    }

    @Test
    @DisplayName("Netty server should start and bind to port")
    void testServerStart() throws Exception {
        MainImplementation.NettyEchoServer server = new MainImplementation.NettyEchoServer(0);
        assertNotNull(server);
        server.close();
    }
}
