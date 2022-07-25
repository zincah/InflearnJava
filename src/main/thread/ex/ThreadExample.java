package thread.ex;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadExample {
	
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newCachedThreadPool();
		
		// process 1
		executorService.submit(()->{
			log("process 1 start");
			try {
				Thread.sleep(1500);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
			log("process 1 end");
		});
		
		log("process 2 start");
		try {
			Thread.sleep(500);
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
		log("process 2 end");
		
		executorService.shutdown();
	}
	
	// 출력을 어떤 스레드에서 하고 있는지 확인
	private static void log(String content) {
		System.out.println(Thread.currentThread().getName() + "> " + content);
	}

}
