package test.server;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class EchoServerHandlerTest extends ChannelInboundHandlerAdapter {
	
	private final int fixedLength = 10;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("Server reading...");
		ByteBuf in = (ByteBuf) msg;
		System.out.println("Server received : " + in.toString(Charset.forName("euc-kr")));
		
		String result = checkData(in.toString(Charset.forName("euc-kr"))); // 길이부 처리한 메세지
		
		byte[] resultArray = result.getBytes("euc-kr");
		ByteBuf message = Unpooled.wrappedBuffer(resultArray); // bytebuf형으로 메세지 전송 준비
		//ByteBuf message = Unpooled.wrappedBuffer(result.getBytes("euc-kr"));
		
		ctx.write(message); // 받은 메시지를 발신자에게로 Echo 시킨다.
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Bye"); // 전송이 완료 되었을 때 출력
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) // 대기중인 메시지를 플러시하고 채널을 닫음
				.addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close(); // 채널 닫기
	}
	
	// check data and change the header
	public String checkData(String msg) throws Exception{
		
		StringBuffer sb = new StringBuffer();
		
		String code = msg.substring(0, fixedLength); // 헤더 영역
		int length = Integer.parseInt(code); // 헤더(문자열) -> 정수형 변환
		
		String realMsg = msg.substring(fixedLength); // 바디
		byte[] msgByte = realMsg.getBytes("euc-kr"); // 바이트로 변환

		if(length == msgByte.length) { // 길이부 길이와 바디의 길이가 같으면
			String successTxt = "정상 처리되었습니다."; // 정상 처리 메세지
			byte[] successByte = successTxt.getBytes("euc-kr"); // 메세지 -> 바이트 변환
			//System.out.println("정상구문 byte : " + successByte.length);
			
			length = length + successByte.length; // 길이부 길이 = 원래 길이 + 정상 처리 메세지 길이
			//System.out.println("재정의 길이 : " + length);
			
			sb = makeHeaderLength(length); // makeHeaderLength 메서드 -> string buffer = 길이부
			sb.append(realMsg); // sb += 바디
			sb.append(successTxt); // sb += 정상처리 메세지
			
			System.out.println(sb);
			return sb.toString(); // string으로 return
		}else {
			return "잘못된 데이터 입니다.";
		}

	}
	
	// make header length
	private StringBuffer makeHeaderLength(int length) {
		
		String len = String.valueOf(length); // ex. length = 110 -> len = "110"
		
		char[] lenArray = new char[len.length()];
		
		for(int i=0; i<len.length(); i++) {
			char su = len.charAt(i);
			lenArray[i] = su;
		}// lenArray 배열에 len을 문자단위로 잘라서 저장
		
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<fixedLength-len.length(); i++) {
			sb.append("0");
		}// 길이부 길이에서 len의 길이를 뺀 만큼을 0으로 채워준다.
		
		for(char ch : lenArray) {
			sb.append(String.valueOf(ch));
		}// 0으로 채운 나머지를 lenArray배열로 채워준다.
		
		// ex. sb = 0000000110
		return sb;
	}
}
