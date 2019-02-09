package praticev2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	private Socket socket;
	private String ipAddress;
	public Client() {

		try {
			System.out.println("Enter IP address: ");
			Scanner scanner = new Scanner(System.in);
			ipAddress = scanner.nextLine();
			System.out.println("Client starting...");
			socket = new Socket(ipAddress, 8090);
			System.out.println("Client initialized!");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		try {
			Receiver receiver = new Receiver();
			Thread t = new Thread(receiver);
			t.start();

			OutputStream out = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
			PrintWriter pw = new PrintWriter(osw, true);

			System.out.println("enter chat message...");
			Scanner scan = new Scanner(System.in);
			//String line = null;

			while (true) {
				String line = scan.nextLine();
				pw.println(line);
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Client client = new Client();
		client.start();
	}

	private class Receiver implements Runnable {

		public void run() {
			try {

				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

				String reply = null;
				while ((reply = br.readLine()) != null) {
					System.out.println(reply);
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
