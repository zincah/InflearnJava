package netty.nonblocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import io.netty.channel.socket.SocketChannel;

public class NonBlockingServer {
	
	private Map<SocketChannel, List<byte[]>> keepDataTrack = new HashMap<>();
	private ByteBuffer buffer = ByteBuffer.allocate(2*1024);
	
	private void startEchoServer() {
		try( // 1
			Selector selector = Selector.open(); // 2
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open() // 3
		){
			if((serverSocketChannel.isOpen())&&(selector.isOpen())) { // 4
				serverSocketChannel.configureBlocking(false); // 5
				serverSocketChannel.bind(new InetSocketAddress(8888)); // 6
				
				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // 7
				System.out.println("접속 대기중");
				
				while(true) {
					selector.select();
					Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
					
					while(keys.hasNext()) {
						SelectionKey key = (SelectionKey)keys.next();
						keys.remove();
						
						if(!key.isValid()) {
							continue;
						}
						
						if(key.isAcceptable()) {
							
						}else if(key.isReadable()) {
							
						}else if(key.isWritable()) {
							
						}
					}
				}
			}else {
				System.out.println("서버 소캣을 생성하지 못했습니다.");
			}
			
		}catch(IOException ex) {
			System.err.println(ex);
		}
	}
	
	public static void main(String[] args) {
		NonBlockingServer main = new NonBlockingServer();
		main.startEchoServer();
	}

}
