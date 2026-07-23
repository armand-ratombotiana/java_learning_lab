package networking;

import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * Netty-based Echo Server demonstration (conceptual).
 * 
 * Netty is a non-blocking I/O framework for high-performance network applications.
 * It's used by gRPC, Apache Spark, Vert.x, and many other projects.
 * 
 * Netty architecture:
 * - EventLoopGroup: multiplexed event loops
 * - Channel: I/O operation source
 * - ChannelPipeline: handler chain
 * - ChannelHandler: business logic
 * 
 * Maven dependency:
 *   io.netty:netty-all:4.1.104.Final
 * 
 * This class demonstrates the patterns. Real Netty requires the library.
 */
public class NettyEchoServer {

    /*
    // Real Netty server code:
    public static class EchoServer {
        private final int port;

        EchoServer(int port) { this.port = port; }

        void start() throws Exception {
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                 .channel(NioServerSocketChannel.class)
                 .childHandler(new ChannelInitializer<SocketChannel>() {
                     @Override
                     public void initChannel(SocketChannel ch) {
                         ch.pipeline().addLast(new EchoServerHandler());
                     }
                 })
                 .option(ChannelOption.SO_BACKLOG, 128)
                 .childOption(ChannelOption.SO_KEEPALIVE, true);

                ChannelFuture f = b.bind(port).sync();
                f.channel().closeFuture().sync();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }
    }

    public static class EchoServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ctx.write(msg); // echo back
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
    */

    // Simulated echo server
    static class SimpleEchoServer {
        private final ServerSocket ss;

        SimpleEchoServer(int port) throws Exception {
            ss = new ServerSocket(port);
        }

        void start() throws Exception {
            try (var socket = ss.accept();
                 var out = socket.getOutputStream();
                 var in = socket.getInputStream()) {
                byte[] buf = new byte[1024];
                int n = in.read(buf);
                String received = new String(buf, 0, n, StandardCharsets.UTF_8);
                String response = "Echo: " + received;
                out.write(response.getBytes(StandardCharsets.UTF_8));
            }
            ss.close();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8890;

        // Simulated Netty-like behavior
        var server = new SimpleEchoServer(port);
        var serverThread = new Thread(() -> {
            try { server.start(); } catch (Exception e) { }
        });
        serverThread.start();

        Thread.sleep(300);

        // Client
        try (var socket = new Socket("localhost", port);
             var out = socket.getOutputStream();
             var in = socket.getInputStream()) {
            out.write("Hello Netty!".getBytes(StandardCharsets.UTF_8));
            byte[] buf = new byte[1024];
            int n = in.read(buf);
            String response = new String(buf, 0, n);
            System.out.println("Response: " + response);
            assert response.contains("Hello Netty!");
        }

        serverThread.join();
        System.out.println("All NettyEchoServer tests passed.");
    }
}