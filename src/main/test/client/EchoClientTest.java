package test.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class EchoClientTest {
	
	static final String HOST = System.getProperty("server", "192.168.1.200"); // server ip 연결
	static final int PORT = Integer.parseInt(System.getProperty("port", "13010")); // port 연결

	public EchoClientTest(){
    }
 
    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();  // bootstrap 생성
            b.group(group)  // 클라이언트 이벤트 처리할 EventLoopGroup을 지정.
                    .channel(NioSocketChannel.class)    // 채널 유형 NIO 지정
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {    // 채널이 생성될 때 파이프라인에 EchoClientHandler 하나를 추가
                            ch.pipeline().addLast(new EchoClientHandlerTest());
                        }
                    });
            ChannelFuture f = b.connect(HOST, PORT).sync();   // 원격 피어로 연결하고 연결이 완료되기를 기다림
            
            f.channel().closeFuture().sync();   // 채널이 닫힐 때까지 블로킹함.

        } finally {
            group.shutdownGracefully().sync();  // 스레드 풀을 종료하고 모든 리소스를 해제함
        }
    }
 
    public static void main(String[] args) throws Exception {
        new EchoClientTest().start();
    }

}
