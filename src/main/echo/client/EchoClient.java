package echo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public final class EchoClient { // final class 사용 이유?
	
	public static void main(String[] args) throws Exception{
		
		EventLoopGroup group = new NioEventLoopGroup();
		
		try {
			
			Bootstrap boot = new Bootstrap();
			boot.group(group)
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel socketChannel) throws Exception {
						ChannelPipeline channelPipeline = socketChannel.pipeline();
						channelPipeline.addLast(new EchoClientHandler());
					}

				});
			
			ChannelFuture channelFuture = boot.connect("192.168.1.179", 8888).sync();
			channelFuture.channel().closeFuture().sync();

		}finally {
			group.shutdownGracefully();
		}

	}

}
