package chatting.netty;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class ChatClient{
	
	// Bootstrap : netty로 작성한 네트워크 프로그램이 시작할 때 가장 먼저 수행
	// -> 어플리케이션이 수행할 동작을 지정
	// -> 프로그램에 대한 각종 설정을 지정
	
	static final String HOST = System.getProperty("server", "192.168.1.156"); // server ip 연결
	static final int PORT = Integer.parseInt(System.getProperty("port", "8888")); // port 연결
	
	// 전문
	private byte[] length = new byte[10];
	
	public static void main(String[] args) throws Exception{
		
		try {
			new ChatClient().run(); // run 메소드 실행
		}catch(Exception e) {
			System.out.println("종료"); // 그냥 강제 종료 버튼 눌렀을 때
		}
		 
	}
	
	public void run() throws Exception{
		EventLoopGroup group = new NioEventLoopGroup(); // 이벤트 루프 인스턴스 생성
		// NioEventLoopGroup : I/O 작업을 처리하는 다중 스레드 이벤트 루프
		
		try {
			
			// SslContext : 안전한 소켓 프로토콜 구현
			// SslContextBuilder : 새로운 SslContext를 생성하기 위한 빌더
			final SslContext sslCtx = SslContextBuilder.forClient()
					.trustManager(InsecureTrustManagerFactory.INSTANCE).build();
			
			Bootstrap bootstrap = new Bootstrap(); // 클라이언트 측 초기화, 클라이언트를 위한 채널을 쉽게 만들어줌
			bootstrap.group(group) // EventLoopGroup 할당
				.channel(NioSocketChannel.class) // NioSocketChannel(유형)을 사용하도록 세팅
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3*1000) // 3초가 지났는데 접속이 되지 않으면 예외
				.handler(new ChatClientInitializer(sslCtx)); // ChatClientInitializer 핸들러 결합
			
			// Channel : 읽기, 쓰기, 연결 그리고 바인드와 같은 I/O 작업이 가능한 네트워크 소케 또는 구성 요소에 대한 연결고리
			Channel channel = bootstrap.connect(HOST, PORT).sync().channel();
			// 비동기 채널 i/o 작업의 결과, 처리가 완료 되었는지 확인할 수 있음
			ChannelFuture channelFuture = null;
			
			// 보낼 메세지 입력
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
			for(;;) {
				
				Thread.sleep(1000);
				System.out.print("데이터 입력 : ");
				String line = br.readLine();
				StringBuffer data = checkedDataProc(line);

				// bye 입력시 채팅 종료
				if("bye".equals(line.toLowerCase())) {
					channel.closeFuture().sync();
					break;
				}
				
				if("quit".equals(data)) {
					break;
				}
				
				// server로 전송
				channelFuture = channel.writeAndFlush(data + "\n");

				if(channelFuture.isDone()){ // 이거 안됨
					System.out.println("채널 접속이 끊겼습니다. 다시 데이터를 입력하면 재접속을 시도합니다.");
				}
				
			}
			
			// 모든 메세지가 flush 될때까지 기다린다.
			if(channelFuture != null) {
				System.out.println("flush");
				channelFuture.sync(); 
			}

		}finally {
			group.shutdownGracefully(); // EventLoopGroup을 종료 -> 모든 리소스 해제
		}
	}
	
	// 길이부 부착하여 전송
	
	public StringBuffer checkedDataProc(String line) throws Exception {
		
		if(line == null) {
			StringBuffer sb = new StringBuffer();
			sb.append("quit");
			return sb;
		}else {
			byte[] bytes = line.getBytes("euc-kr");
			String len = String.valueOf(bytes.length);
			
			char[] lenArray = new char[len.length()];
			
			for(int i=0; i<len.length(); i++) {
				char su = len.charAt(i);
				lenArray[i] = su;
			}

			StringBuffer sb = new StringBuffer();
			for(int i=0; i<10-len.length(); i++) {
				sb.append("0");
			}
			
			for(char ch : lenArray) {
				sb.append(String.valueOf(ch));
			}
			
			sb.append(new String(bytes, "euc-kr"));
			//sb.append("아");
			return sb;
		}
	}
}
