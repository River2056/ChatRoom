package v5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * 聊天室客戶端
 * @author User
 *
 */
public class Client {
	/**
	 * java.net.Socket
	 * 封裝了TCP通訊協議, 中文翻譯是: 套接字
	 * 使用Socket的大致步驟:
	 * 1. 實例化Socket, 同時指定連接的服務端的IP和端口並與服務端建立連接
	 * 2. 通過Socket創建兩個流, 一個輸入流一個輸出流
	 * 通過輸入流讀取遠端計算機發送過來的數據
	 * 通過輸出流將數據發送給對方計算機
	 */
	private Socket socket;
	private int id;
	
	/**
	 * 用來初始化客戶端
	 */
	public Client() {
		try {
			System.out.println("正在連接服務端......");
			socket = new Socket("172.16.0.187", 8088);
			System.out.println("與服務端建立連接!");
			/**
			 * 實例化Socket時需要傳入兩個參數
			 * 1. IP地址, 通過IP可以找到網路上的指定計算機
			 * 2. 端口, 用來連接該計算機上的對應應用程序
			 * 實例化Socket的過程就是與服務端連接的過程
			 */
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 程序的啟動方法
	 */
	public void start() {
		try {
			// 啟動讀取服務端消息的線程
			ServerHandler handler = new ServerHandler();
			Thread t = new Thread(handler);
			t.start();
			
			OutputStream out = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
			PrintWriter pw = new PrintWriter(osw, true);
			
			Scanner scan = new Scanner(System.in);
			System.out.println("請輸入訊息:...");
			String line = null;
			
			long lastSend = System.currentTimeMillis() - 1000;
			while(true) {
				
				line = scan.nextLine();
				if(System.currentTimeMillis() - lastSend >= 1000) {
					pw.println(line);
				} else {
					System.out.println("您說話速度太快!");
				}
				lastSend = System.currentTimeMillis();
					
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		client.start();
	}
	
	/**
	 * 該線程用於讀取服務端發送過來的消息
	 * 並輸出到控制台
	 * @author User
	 *
	 */
	private class ServerHandler implements Runnable {

		public void run() {
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(
								socket.getInputStream(), "UTF-8"
								)
						);
				String line = null;
				while((line = br.readLine()) != null) {
					System.out.println(line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
