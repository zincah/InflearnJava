package chatting.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class EchoServer {

	private final int port;
	
	public EchoServer(int port) {
		super();
		this.port = port;
	}
	
	public static void main(String[] args) throws Exception{
		new EchoServer(11100).run();
	}
	
	public void run() throws Exception{
		// nio 처리를 다루는 이벤트 루프 인스턴스 생성
		// 매개변수에 1이 주어지는 경우 단일 스레드로 동작하는 객체
		// 매개변수가 주어지지 않으면 cpu 코어 수에 따라 설정
		EventLoopGroup bossGroup = new NioEventLoopGroup(1); // serverSocket listen 역할
		EventLoopGroup workerGroup = new NioEventLoopGroup(); // 만들어진 Channel에서 넘어온 이벤트 처리(입출력)
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap(); // 서버 channel 세팅
			bootstrap.group(bossGroup, workerGroup) // group 할당
				.channel(NioServerSocketChannel.class) // NioServerSocketChannel 채널 사용
				.handler(new LoggingHandler(LogLevel.INFO)) // log찍히는 단계 info로 해서 적용(console)
				.childHandler(new EchoServerInitializer()); // 핸들러 등록
			
			bootstrap.bind(port).sync().channel().closeFuture().sync();
			// 서버 소켓에 포트를 바인딩
			// sync() 메소드를 호출해서 바인딩 완료 될때까지 대기
			// channel().closeFuture().sync() channel이 닫힐 때까지 대기
			
		}finally {
			bossGroup.shutdownGracefully(); // 모든 리소스 해제
			workerGroup.shutdownGracefully(); // 모든 리소스 해제
		}
	}

}
