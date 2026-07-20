package com.javalab.04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class MainImplementation {

    public static class EchoServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ByteBuf in = (ByteBuf) msg;
            ByteBuf out = Unpooled.copiedBuffer("Echo: ", CharsetUtil.UTF_8);
            out.writeBytes(in);
            ctx.writeAndFlush(out);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }

    public static class NettyEchoServer implements AutoCloseable {
        private final EventLoopGroup bossGroup;
        private final EventLoopGroup workerGroup;
        private Channel channel;

        public NettyEchoServer(int port) throws InterruptedException {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) {
                     ch.pipeline().addLast(new EchoServerHandler());
                 }
             });
            channel = b.bind(port).sync().channel();
        }

        public void close() {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
