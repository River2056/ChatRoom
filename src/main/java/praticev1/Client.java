package praticev1;

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

public class Client {
	private Socket socket;

	public Client() {
		try {
			System.out.println("Client chat room initializing...");
			socket = new Socket("localhost", 8089);
			System.out.println("Chat room created!");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		// create output and receive input
		try {
			
			Receiver receiver = new Receiver();
			Thread receive = new Thread(receiver);
			receive.start();
			
			OutputStream out = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
			PrintWriter pw = new PrintWriter(osw, true);
			
			Scanner scan = new Scanner(System.in);
			System.out.println("enter chat message: ...");
			String line = null;

			while (true) {
				
				
				
				line = scan.nextLine();
				pw.println(line);
				

				if ("exit".equals(line)) {
					System.out.println("Good Bye!");
					pw.close();
					break;
				}
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		Client client = new Client();
		client.start();
	}
	
	private class Receiver implements Runnable {
		// main objective: receive replies without disturbing OutputStream
		public void run() {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				
				String reply = null;
				while((reply = br.readLine()) != null) {
					System.out.println("Other client replied: " + reply);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
}
