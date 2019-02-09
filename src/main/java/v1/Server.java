package v1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 聊天室服務端
 * 
 * @author User
 *
 */
public class Server {
	/**
	 * java.net.ServerSocket 運行在Server端的ServerSocket 主要作用有兩個:
	 * 1:項系統申請服務端口,客戶端就是通過這個端口與之連接的 2:監聽該服務端口, 一旦客戶端通過這個端口請求連接,
	 * 則創建一個Socket與該客戶端進行通訊
	 */
	private ServerSocket server;

	public Server() {
		try {
			/**
			 * 實例化ServerSocket的同時 申請服務端口. 若該端口已經 被其他程序占用, 則會拋出異常 address already
			 * in use
			 */
			System.out.println("正在啟動服務端......");
			server = new ServerSocket(8088);
			System.out.println("服務端啟動完畢!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		try {
			/**
			 * ServerSocket提供的方法: Socket accept() 該方法是一個阻塞方法, 調用到 該方法後程序"卡住",
			 * 並等待 客戶端的連接, 一旦一個客戶端連接了, 那麼就會返回一個Socket實例, 通過該實例即可與連接的客戶端進行通訊
			 */
			int linkTimes = 0;
			System.out.println("等待客戶端連接......");
			Socket socket = server.accept();
			linkTimes++;
			System.out.println(linkTimes + "個客戶端已連接!");

			InputStream in = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(in, "UTF-8");
			BufferedReader br = new BufferedReader(isr);

			while (true) {
				String message = null;
				if ((message = br.readLine()) != null) {
					System.out.println("客戶端說: " + message);
				} else {
					System.out.println("客戶端已下線!");
					br.close();
					break;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}

}
