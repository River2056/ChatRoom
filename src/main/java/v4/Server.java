package v4;

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
import java.util.Arrays;

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
	private int linkTimes = 0;
	/**
	 * �s��Ҧ��Ȥ�ݿ�X�y, �Ω�s������
	 */
	private PrintWriter[] allOut;

	public Server() {
		try {
			/**
			 * ��Ҥ�ServerSocket���P�� �ӽЪA�Ⱥݤf. �Y�Ӻݤf�w�g �Q��L�{�ǥe��, �h�|�ߥX���` address already
			 * in use
			 */
			System.out.println("���b�ҰʪA�Ⱥ�......");
			server = new ServerSocket(8088);
			allOut = new PrintWriter[0];
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
			while (true) {
				System.out.println("���ݫȤ�ݳs��......");
				Socket socket = server.accept();
				linkTimes++;
				System.out.println(linkTimes + "�ӫȤ�ݤw�s��!");

				// �Ыؽu�{
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
	 * �B�z�P�@�ӫȤ�ݪ��椬
	 * 
	 * @author User
	 *
	 */
	private class ClientHandler implements Runnable {
		private Socket socket;
		// ������e�Ȥ�ݪ��a�}�H��
		private String host;

		public ClientHandler(Socket socket) {
			this.socket = socket;
			InetAddress address = socket.getInetAddress();
			host = address.getHostAddress();
		}

		public void run() {
			PrintWriter pw = null;
			try {
				System.out.println(linkTimes + "�ӽu�{�ҰʤF!");
				InputStream in = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(in, "UTF-8");
				BufferedReader br = new BufferedReader(isr);

				OutputStream out = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
				pw = new PrintWriter(osw, true);
				// �N�ӫȤ�ݪ���X�y�s�JallOut
				allOut = Arrays.copyOf(allOut, allOut.length + 1);
				allOut[allOut.length - 1] = pw;
				// System.out.println("allOut length: " + allOut.length);

				String message = null;
				while ((message = br.readLine()) != null) {
					System.out.println("�Ȥ�ݻ�: " + message);
					// �^�ЩҦ��Ȥ��
					for (int i = 0; i < allOut.length; i++) {
						allOut[i].println(host + " ��: " + message);
					}
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				/**
				 * �B�z�Ȥ�ݬq�}�s���᪺�ާ@
				 */
				System.out.println("�@�ӫȤ���_�u�F");
				
				// �N�ӫȤ�ݪ���X�y�q�@�ɶ��X���R��
				// ���: �u�n�ˬd�����pw��null, �N�N�̫�@�ӿ�X�y�ƻs�@�����N��, ���Y�e(���Xwhile�`������, pw��_��null)
				for(int i = 0 ; i < allOut.length ; i++) {
					if(allOut[i] == pw) {
						allOut[i] = allOut[allOut.length-1];
						allOut = Arrays.copyOf(allOut, allOut.length - 1);
						break;
					}
				}
				
				
				// My idea:
				// �C��allOut�]�m�@��boolean
				// �ˬd: ��boolean��true, �P�̫�@�ӹ��
				// PrintWriter pw = allOut[i] ; allOut[i] = allOut[allOut.length-1] ; allOut[allOut.length-1] = pw
				// �Y�e: Arrays.copyOf(allOut, allOut.length-1)
				
				try {
					/**
					 * SOcket������, ��X�y�P��J�y�]�N�����F
					 */
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
}
