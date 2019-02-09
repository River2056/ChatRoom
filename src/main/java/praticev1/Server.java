package praticev1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private ServerSocket server;
	private int linkTimes = 0;
	private ArrayList<PrintWriter> streams;

	public Server() {
		try {

			System.out.println("Server initializing...");
			server = new ServerSocket(8089);
			streams = new ArrayList<PrintWriter>();
			System.out.println("Server created!");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		// get InputStream to receive message
		try {
			while (true) {
				System.out.println("Waiting for connection...");
				Socket socket = server.accept();
				linkTimes++;
				System.out.println(linkTimes + " client connected!");

				ClientHandler handler = new ClientHandler(socket);
				Thread t = new Thread(handler);
				t.start();

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}

	private class ClientHandler implements Runnable {
		private Socket socket;

		ClientHandler(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			PrintWriter pw = null;
			try {
				// getting message from client
				InputStream in = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(in, "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				

				// reply message back to client
				OutputStream out = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
				pw = new PrintWriter(osw, true);
				streams.add(pw);
				// System.out.println("streams length: " + streams.size());
				
				String message = null;
				while ((message = br.readLine()) != null) {
					System.out.println("Client says: " + message);
					for (int i = 0; i < streams.size() ; i++) {
						streams.get(i).println(message);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				for(int i = 0 ; i < streams.size() ; i++) {
					if(streams.get(i) == pw) { // pw returns null after leaving while loop
						streams.remove(i);  // check if null, remove it
						break;
					}
				}
				
				try {
					System.out.println("A friend logged off!");
					socket.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				
			}

		}

	}

}
