package chatting.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;


public class EchoServerInitializer extends ChannelInitializer<SocketChannel> {
	
	private final SslContext sslCtx;

	public EchoServerInitializer(SslContext sslCtx) {
		this.sslCtx = sslCtx;
	}

	@Override
	protected void initChannel(SocketChannel arg0) throws Exception {
		// Netty 내부에서 할당한 빈 채널 파이프라인 가져오기
		ChannelPipeline pipeline = arg0.pipeline();

		// 데이터 수신 순서 : DelimiterBasedFrameDecoder > StringDecoder > EchoServerHandler
		pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
		pipeline.addLast(new StringDecoder());
		pipeline.addLast(new StringEncoder());
		pipeline.addLast(new EchoServerHandler()); // 파이프라인에 이벤트 핸들러 등록
	}
}
