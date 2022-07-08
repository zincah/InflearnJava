package echo.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx){
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String sendMessage = "";
		try {
			System.out.println("입력 : ");
			sendMessage = br.readLine();
		}catch(IOException e) {
			e.printStackTrace();
		}

		ByteBuf messageBuffer = Unpooled.buffer();
		messageBuffer.writeBytes(sendMessage.getBytes());
		
		StringBuilder builder = new StringBuilder();
		builder.append("[전송한 문자열 : ");
		builder.append(sendMessage);
		builder.append(" ]");
		
		System.out.println(builder.toString());
		ctx.writeAndFlush(messageBuffer);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg){
		String readMessage = ((ByteBuf)msg).toString(Charset.defaultCharset());
		
		StringBuilder builder = new StringBuilder();
		builder.append("[수신한 문자열 : ");
		builder.append(readMessage);
		builder.append(" ]");
		
		System.out.println(builder.toString());
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx){
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){ // 예외처리
		cause.printStackTrace();
		ctx.close();
	}
	
	
	
	
	

}
