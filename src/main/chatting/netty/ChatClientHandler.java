package chatting.netty;


import java.nio.channels.ClosedChannelException;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

// 서버로부터 응답 받아서 화면에 출력해주는 handler class 구현
public class ChatClientHandler extends ChannelInboundHandlerAdapter {

	// ChannelHandlerContext : ChannelHandler가 ChannelPipeline 및 다른 핸들러와 상호 작용할 수 있도록 한다.

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		// 받아오는 값이 없으면 아예 실행이 안됨
		byte[] bytes = ((String)msg).getBytes();
		System.out.println((String)msg);

	} // 수신된 메세지 출력
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("서버 응답 수신 완료");
	}
	
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent)evt;
			if(e.state() == IdleState.READER_IDLE) {
				System.out.println("서버 응답 없음");
				ctx.close();
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		
			if(cause instanceof ClosedChannelException) {
				System.out.println("channel이 닫혔습니다.");
				ctx.close();
			}else {
				cause.printStackTrace();
				ctx.close(); // 채널 close
			}
	} // 예외처리

	
	
	

}
