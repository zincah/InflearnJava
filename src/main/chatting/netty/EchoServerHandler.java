package chatting.netty;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

// ChannelInboundHandlerAdapter : 입력된 데이터를 처리하는 이벤트 핸들러(실제 업무 담당)
public class EchoServerHandler extends ChannelInboundHandlerAdapter {


	private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception{

		// 채널 핸들러가 채널 파이프라인에 추가될 때 호출
		System.out.println("handlerAdded of [SERVER]");
		Channel incoming = ctx.channel();
		
		// 사용자가 접속했을 때 기존 사용자에게 알림
		for(Channel channel : channelGroup) {
			channel.write("[SERVER] - " + incoming.remoteAddress() + " has joined!\n");
		}
		channelGroup.add(incoming); // 그룹에 채널 추가
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception{
		
		// 채널핸들러가 채널파이프라인에서 제거될 때 호출
		System.out.println("handlerRemoved of [SERVER]");
		
		Channel incoming = ctx.channel();

		// 사용자가 나갔을 때 기존 사용자에게 알림
		for(Channel channel : channelGroup) {
			channel.write("[SERVER] - " + incoming.remoteAddress() + " has left!\n");
		}
		
		// 그룹에서 채널 삭제
		channelGroup.remove(incoming);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception{
		// 채널 입출력 준비 완료
		// 사용자 접속했을 때 서버에 표시
		System.out.println("User Access!");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{ // 데이터 수신처리 메서드
		
		// 데이터 수신
		// 메시지 들어올 때마다 호출되는 함수
		// 수신한 데이터 모두 처리하기위해 재정의
		// ChannelHandlerContext : ChannelHandler가 ChannelPipeline 및 다른 핸들러와 상호 작용할 수 있도록 한다.
		
		String message = null;
		message = (String)msg;
		System.out.println("channelRead of [SERVER]" + message);
		
		String code = message.substring(0, 10);
		int length = Integer.parseInt(code);
		
		String realMsg = message.substring(10);
		byte[] msgByte = realMsg.getBytes("euc-kr");
		boolean check = false;
		
		if(length == msgByte.length) {
			check = true;
		}else {
			check = false;
		}

		Channel incoming = ctx.channel();
		
		// 생성된 channel에서 들어온 데이터를 구하기 위한 for
		for(Channel channel : channelGroup) {
			
			System.out.println(check);
			if(check == true) {
				if(realMsg.equals("bye")) {
					break;
				}
				channel.writeAndFlush("[" + incoming.remoteAddress() + "]" + message + "\n"); // channel에서 전달받은 메세지 값을 전송
			}else {
				//channel.write("잘못된 정보");
				// 대기 처리를 해야하는데..
			}
			
		}

		if("bye".equals(realMsg.toLowerCase())){
			System.out.println("종료");
			ctx.close();
		}
		

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
		
		// 데이터 수신 완료
		// channelRead 이벤트 처리 완료된 후 자동으로 수행되는 이벤트 메서드. 채널 파이프라인에 저장된 버퍼를 전송하는 flush 메서드 호출
		// 소켓 채널에 더 이상 읽을 데이터가 없을 때 발생
		// 마지막으로 채널을 닫아주는 역할
		ctx.flush();
	}
}
