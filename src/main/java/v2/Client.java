package v2;

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
 * ��ѫǫȤ��
 * @author User
 *
 */
public class Client {
	/**
	 * java.net.Socket
	 * �ʸˤFTCP�q�T��ĳ, ����½Ķ�O: �M���r
	 * �ϥ�Socket���j�P�B�J:
	 * 1. ��Ҥ�Socket, �P�ɫ��w�s�����A�Ⱥݪ�IP�M�ݤf�ûP�A�Ⱥݫإ߳s��
	 * 2. �q�LSocket�Ыب�Ӭy, �@�ӿ�J�y�@�ӿ�X�y
	 * �q�L��J�yŪ�����ݭp����o�e�L�Ӫ��ƾ�
	 * �q�L��X�y�N�ƾڵo�e�����p���
	 */
	private Socket socket;
	private int id;
	
	/**
	 * �ΨӪ�l�ƫȤ��
	 */
	public Client() {
		try {
			System.out.println("���b�s���A�Ⱥ�......");
			socket = new Socket("localhost", 8088);
			System.out.println("�P�A�Ⱥݫإ߳s��!");
			/**
			 * ��Ҥ�Socket�ɻݭn�ǤJ��ӰѼ�
			 * 1. IP�a�}, �q�LIP�i�H�������W�����w�p���
			 * 2. �ݤf, �Ψӳs���ӭp����W���������ε{��
			 * ��Ҥ�Socket���L�{�N�O�P�A�Ⱥݳs�����L�{
			 */
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * �{�Ǫ��Ұʤ�k
	 */
	public void start() {
		try {
			OutputStream out = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
			PrintWriter pw = new PrintWriter(osw, true);
			Scanner scan = new Scanner(System.in);
			System.out.println("�п�J�T��:...");
			String line = null;
			while(true) {
				if("exit".equals(line)) {
					System.out.println("See you soon!");
					pw.close();
					break;
				}
				line = scan.nextLine();
				pw.println(line);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		client.start();
	}
	
}
