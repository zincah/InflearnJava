package chatting.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

// ChannelInitializer : 채널을 이벤트 루프에 등록한 후 쉽게 초기화할 수 있는 방법을 제공
// 채널 파이프라인 생성 클래스
public class ChatClientInitializer extends ChannelInitializer<SocketChannel> {

	private final SslContext sslCtx;
	// SslContext : SslEngine 및 SslHandler의 팩토리로써 동작하는 안전한 소켓 프로토콜 구현
	
	public ChatClientInitializer(SslContext sslCtx) {
		this.sslCtx = sslCtx;
	}

	@Override
	protected void initChannel(SocketChannel sc) throws Exception {
		// socket channel의 pipeline 참조값을 get
		// pipeline에는 여러개의 handler를 추가할 수 있고, 순차적으로 진행된다.
		ChannelPipeline pipeline = sc.pipeline(); 
		
		// delimiters(구분 기호) .lineDelimiter() : \r, \n 구분 기호를 텍스트 기반 프로토콜에 사용할 수 있게 반환
		// 8192 : maxFrameLength -> decoding할 수 있는 최대 byte
		// DelimiterBasedFrameDecoder : 수신된 ByteBuf를 하나 이상의 구분 기호로 분리하는 디코더
		// null또는 개행 문자와 같은 구분 기호로 끝나는 프레임을 디코딩 할 때 유용
		pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
		pipeline.addLast(new StringDecoder()); // decoder 등록
		pipeline.addLast(new StringEncoder()); // encoder 등록 -> string을 bytebuf로
		//pipeline.addLast("readTimeoutHandler", new ReadTimeoutHandler(30)); // 데이터가 정해진 시간 내에 들어오지 않을 때
		pipeline.addLast(new IdleStateHandler(5, 0, 0)); // 서버에서 응답이 없을 때
		pipeline.addLast("myHandler", new MyHandler());
		pipeline.addLast(new ChatClientHandler()); // 이벤트 핸들러 등록
		
	}

}

