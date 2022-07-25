package http.parse;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class HttpServerExHandler extends ChannelInboundHandlerAdapter{
	
	private int fixedLength = 10; // 길이부 길이
	private byte[] lengthArray = new byte[10]; // 길이부 저장 배열
	private byte[] checkState = new byte[30]; // 처리 상태

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channel active...");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("server reading...");
		
		ByteBuf temp = (ByteBuf)msg;
		System.out.println(temp.toString(Charset.forName("euc-kr")));
		
		String str = temp.toString(Charset.forName("euc-kr"));
		String result = checkLength(str); // 길이부 일치 -> data 변환
		
		System.out.println("결과 데이터 : " + result);
		byte[] resultArray = result.getBytes("euc-kr");
		ByteBuf returnMsg = Unpooled.wrappedBuffer(resultArray);
		ctx.write(returnMsg); // 길이부 일치하지 않으면 데이터를 리턴하지 말아야함
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Bye");
		ctx.flush();
		/*
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
			.addListener(ChannelFutureListener.CLOSE);*/
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();  
		ctx.close(); // 채널 닫기
	}
	
	public String checkLength(String str) throws Exception{
		
		int length = Integer.parseInt(str.substring(0, fixedLength));
		String data = str.substring(fixedLength);
		
		byte[] dataArray = data.getBytes("euc-kr"); // byte[] 로 casting
		
		if(length == dataArray.length) {
			// 길이부 일치
			String successState = "정상 처리되었습니다.";
			byte[] successArray = successState.getBytes("euc-kr");
			
			for(int i=0; i<checkState.length; i++) {
				if(i<successArray.length) {
					checkState[i] = successArray[i];
				}else {
					checkState[i] = (byte)' ';
				}
			}// 좌측정렬 : N

		}
		
		int x = 0;
		for(int i=dataArray.length-checkState.length; i<dataArray.length; i++) {
			dataArray[i] = checkState[x++];
		}
		
		String realData = new String(dataArray, "euc-kr");

		// 길이부 붙이기
		makeHeaderLength(dataArray.length);
		String finalData = new String(lengthArray, "euc-kr") + realData;
		return finalData;
	}
	
	public void makeHeaderLength(int len) { // 길이부 
		String lenStr = String.valueOf(len);

		int lenSu = 0;
		for(int i=0; i<fixedLength; i++) {
			if(i<fixedLength-lenStr.length()) {
				lengthArray[i] = (byte)'0';
			}else {
				lengthArray[i] = (byte)lenStr.charAt(lenSu++);
			}
		}

	}

	
	
}
