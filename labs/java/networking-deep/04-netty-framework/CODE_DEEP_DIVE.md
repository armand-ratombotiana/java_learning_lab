# Netty Framework -- Code Deep Dive
## Main Implementation
Package: com.javalab.04

### Netty ChannelHandler Pipeline
ServerBootstrap b = new ServerBootstrap();
b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
 .childHandler(new ChannelInitializer() {
   public void initChannel(SocketChannel ch) {
     ch.pipeline().addLast(new HttpServerCodec());
     ch.pipeline().addLast(new MyHandler()); } });

### ByteBuf Usage
ByteBuf buf = Unpooled.copiedBuffer("Hello", CharsetUtil.UTF_8);
ctx.writeAndFlush(buf);

### Round-Trip Test Pattern
Each implementation includes a local loopback test that verifies the server
can accept connections, process messages, and return responses correctly.
