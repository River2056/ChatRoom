package v6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

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
	private int linkTimes = 0;
	/**
	 * 存放所有客戶端輸出流, 用於廣播消息
	 */
	private Collection<PrintWriter> allOut;

	public Server() {
		try {
			/**
			 * 實例化ServerSocket的同時 申請服務端口. 若該端口已經 被其他程序占用, 則會拋出異常 address already
			 * in use
			 */
			System.out.println("正在啟動服務端......");
			server = new ServerSocket(8088);
			allOut = new ArrayList<PrintWriter>();
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
			while (true) {
				System.out.println("等待客戶端連接......");
				Socket socket = server.accept();
				linkTimes++;
				System.out.println(linkTimes + "個客戶端已連接!");

				// 創建線程
				ClientHandler handler = new ClientHandler(socket);
				Thread t = new Thread(handler);
				t.start();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}

	/**
	 * 處理與一個客戶端的交互
	 * 
	 * @author User
	 *
	 */
	private class ClientHandler implements Runnable {
		private Socket socket;
		// 紀錄當前客戶端的地址信息
		private String host;

		public ClientHandler(Socket socket) {
			this.socket = socket;
			InetAddress address = socket.getInetAddress();
			host = address.getHostAddress();
		}

		public void run() {
			PrintWriter pw = null;
			try {
				System.out.println(linkTimes + "個線程啟動了!");
				InputStream in = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(in, "UTF-8");
				BufferedReader br = new BufferedReader(isr);

				OutputStream out = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
				pw = new PrintWriter(osw, true);
				// 將該客戶端的輸出流存入allOut
				// 因為大家都對allOut做操作, 所以加鎖防止"搶"的問題
				synchronized (allOut) {
					allOut.add(pw);
				}
				
//				System.out.println("allOut ArrayList size: " + allOut.size());
//				allOut = Arrays.copyOf(allOut, allOut.length + 1);
//				allOut[allOut.length - 1] = pw;
//				System.out.println("allOut length: " + allOut.length);

				String message = null;
				while ((message = br.readLine()) != null) {
					System.out.println("客戶端說: " + message);
					synchronized (allOut) {
						// 回覆所有客戶端
						// 加鎖的目的: 防止大家同時對集合進行遍歷, 造成混亂
						for (PrintWriter pw1 : allOut) {
							pw1.println(host + " 說: " + message);
						}
//					for (int i = 0; i < allOut.length; i++) {
//						allOut[i].println(host + " 說: " + message);
//					}
					}
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				/**
				 * 處理客戶端段開連接後的操作
				 */
				System.out.println("一個客戶端斷線了");
				synchronized (allOut) {
					// 將該客戶端的輸出流從共享集合中刪除
					allOut.remove(pw);
				}

//				System.out.println("allOut size after remove(): " + allOut.size());
				
				// 思路: 只要檢查到哪個pw為null, 就將最後一個輸出流複製一份取代之, 並縮容(跳出while循環之後, pw恢復為null)
//				for(int i = 0 ; i < allOut.length ; i++) {
//					if(allOut[i] == pw) {
//						allOut[i] = allOut[allOut.length-1];
//						allOut = Arrays.copyOf(allOut, allOut.length - 1);
//						break;
//					}
//				}
				
				
				// My idea:
				// 每個allOut設置一個boolean
				// 檢查: 當boolean為true, 與最後一個對調
				// PrintWriter pw = allOut[i] ; allOut[i] = allOut[allOut.length-1] ; allOut[allOut.length-1] = pw
				// 縮容: Arrays.copyOf(allOut, allOut.length-1)
				
				try {
					/**
					 * Socket關閉後, 輸出流與輸入流也就關閉了
					 */
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
}
