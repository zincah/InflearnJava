package test.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServerTest {
	
	private final int port;
	 
    public EchoServerTest() {
        this.port = 11101; 
    }
 
    public static void main(String[] args) throws Exception {
        new EchoServerTest().start();   // 서버의 start() 메소드 호출
    }
 
    private void start() throws Exception {
        final EchoServerHandlerTest serverHandler = new EchoServerHandlerTest(); // handler 선언
        //EventLoopGroup group = new NioEventLoopGroup(); // EventLoopGroup 생성
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // 부모 스레드 : 클라이언트 요청을 수락하는 역할
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 자식 스레드 : IO와 이벤트 처리 담당
        
        try {
            ServerBootstrap b = new ServerBootstrap();  // ServerBootstrap 생성
            b.group(bossGroup, workerGroup) // .group 이벤트 루프 설정
                    .channel(NioServerSocketChannel.class)  // NIO 전송채널을 이용하도록 지정, 서버에 연결된 클라이언트 소캣 채널은 NIO로 동작
                    .handler(new LoggingHandler(LogLevel.INFO)) // 채널에서 발생하는 모든 이벤트를 로그로 출력
                    .childHandler(new ChannelInitializer<SocketChannel>() { // childHandler : 소켓 채널의 데이터 가공 핸들러 설정
                        @Override// 자식 채널 초기화 (이너 클래스)
                        protected void initChannel(SocketChannel ch) throws Exception { // EchoServerHandler 하나를 채널의 Channel Pipeline 으로 추가
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            ChannelFuture f = b.bind(port).sync();  // 서버를 비동기식으로 바인딩
            f.channel().closeFuture().sync();   // 채널의 CloseFuture를 얻고 완료될 때까지 현재 스레드를 블로킹
        } finally {
            //group.shutdownGracefully().sync();  // EventLoopGroup을 종료하고 모든 리소스 해제
        	bossGroup.shutdownGracefully().sync();
        	workerGroup.shutdownGracefully().sync();
        }
    }

}
