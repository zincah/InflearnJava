package test.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class EchoClientHandlerTest extends SimpleChannelInboundHandler<ByteBuf>{
	
	 	@Override
	    public void channelActive(ChannelHandlerContext ctx) throws Exception {
		 	
		 	System.out.println("~.~.~.~.~.~Successful connection with server~.~.~.~.~.~\n"); // 정상 연결 메세지
		 	
		 	StringBuffer sb = pushDatas(); // 데이터 setting
		 	ctx.writeAndFlush(Unpooled.copiedBuffer(sb.toString(), Charset.forName("euc-kr"))); // 채널 활성화 시 메시지 전송
		 	
	    }
	 
	    @Override
	    public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
	    	
	    	System.out.println("\n~.~.~.~.~.~{ print data }~.~.~.~.~.~"); // 수신된 데이터가 있을 경우 출력
	    	System.out.println("\n{Read Data} = " + msg.toString(Charset.forName("euc-kr")) + "\n"); // 수신된 데이터 출력
	    	
	    	ChangeDatas cd = new ChangeDatas();
			cd.checkReadData(msg.toString(Charset.forName("euc-kr"))); // 데이터 출력
	    	
	    	//System.out.println("Client receive : " + msg.toString(Charset.forName("euc-kr")));  // 수신한 메시지 로깅
	    }
	    
	    
	 
	    @Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			System.out.println("channel 연결 해제");
	    	ctx.close(); 
		}

		@Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	        cause.printStackTrace();    // 예외 시 오류를 로깅하고 채널 닫기
	        ctx.close();
	    }
	    
	    public StringBuffer pushDatas() throws Exception{ // 데이터 입력 메서드
	    	
	    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    	
	    	System.out.print("[인터페이스 id] = ");
			String if_idStr = br.readLine();
			
			System.out.print("[이름] = ");
			String nameStr = br.readLine();
			
			System.out.print("[계좌번호] = ");
			String accountStr = br.readLine();
			
			System.out.print("[핸드폰 번호] = ");
			String phoneStr = br.readLine();
			
			ChangeDatas cd = new ChangeDatas();
			StringBuffer tempBuf = cd.makeData(if_idStr, nameStr, accountStr, phoneStr); // 전문 형태로 변환 해주는 메서드

			String line = tempBuf.toString();
			StringBuffer data = checkedDataProc(line); // 길이부 + 데이터

			
			return data;
	    }
	    
	    // 길이부 부착하여 전송
		public StringBuffer checkedDataProc(String line) throws Exception {

			byte[] bytes = line.getBytes("euc-kr"); // byte 배열로 변환
			String len = String.valueOf(bytes.length); // byte 배열 길이 -> string
			
			char[] lenArray = new char[len.length()]; 
			
			for(int i=0; i<len.length(); i++) { 
				char su = len.charAt(i);
				lenArray[i] = su;
			} // lenArray 배열에 len을 문자별로 잘라서 저장

			StringBuffer sb = new StringBuffer();
			for(int i=0; i<10-len.length(); i++) {
				sb.append("0");
			} // len 길이의 나머지는 우선 0으로 저장
			
			for(char ch : lenArray) {
				sb.append(String.valueOf(ch));
			} // 0으로 저장하고 남은 공간은 lenArray의 값을 저장
			
			sb.append(new String(bytes, "euc-kr")); // 전체 데이터 추가
			//sb.append("아");
			return sb;
			
		}

}
