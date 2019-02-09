package v2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ��ѫǪA�Ⱥ�
 * 
 * @author User
 *
 */
public class Server {
	/**
	 * java.net.ServerSocket �B��bServer�ݪ�ServerSocket �D�n�@�Φ����:
	 * 1:���t�ΥӽЪA�Ⱥݤf,�Ȥ�ݴN�O�q�L�o�Ӻݤf�P���s���� 2:��ť�ӪA�Ⱥݤf, �@���Ȥ�ݳq�L�o�Ӻݤf�ШD�s��,
	 * �h�Ыؤ@��Socket�P�ӫȤ�ݶi��q�T
	 */
	private ServerSocket server;
	int linkTimes = 0;
	int clientIndex = 0;

	public Server() {
		try {
			/**
			 * ��Ҥ�ServerSocket���P�� �ӽЪA�Ⱥݤf. �Y�Ӻݤf�w�g �Q��L�{�ǥe��, �h�|�ߥX���` address already
			 * in use
			 */
			System.out.println("���b�ҰʪA�Ⱥ�......");
			server = new ServerSocket(8088);
			System.out.println("�A�ȺݱҰʧ���!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		try {
			/**
			 * ServerSocket���Ѫ���k: Socket accept() �Ӥ�k�O�@�Ӫ����k, �եΨ� �Ӥ�k��{��"�d��",
			 * �õ��� �Ȥ�ݪ��s��, �@���@�ӫȤ�ݳs���F, ����N�|��^�@��Socket���, �q�L�ӹ�ҧY�i�P�s�����Ȥ�ݶi��q�T
			 */
			while(true) {
				System.out.println("���ݫȤ�ݳs��......");
				Socket socket = server.accept();
				linkTimes++;
				System.out.println(linkTimes + "�ӫȤ�ݤw�s��!");
				
				// �Ыؽu�{
				ClientHandler handler = new ClientHandler(socket);
				Thread t = new Thread(handler);
				t.start();
				
			}
		
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
			

	

	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}
	/**
	 * �B�z�P�@�ӫȤ�ݪ��椬
	 * @author User
	 *
	 */
	private class ClientHandler implements Runnable {
		private Socket socket;

		ClientHandler(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				System.out.println(linkTimes + "�ӽu�{�ҰʤF!");
				InputStream in = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(in, "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				
				String message = null;
				while(true) {
					if((message = br.readLine()) != null) {
						System.out.println("�Ȥ�ݻ�: " + message);
					} else {
						System.out.println("�Ȥ�ݤw�U�u!");
						br.close();
						break;
					}
				}
				
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}