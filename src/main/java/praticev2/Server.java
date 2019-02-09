package praticev2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private ServerSocket server;
	private ArrayList<PrintWriter> allOut;
	private int link = 0;

	public Server() {
		try {
			System.out.println("Server starting...");
			server = new ServerSocket(8090);
			allOut = new ArrayList<PrintWriter>();
			System.out.println("Server initialized!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {

		try {
			while (true) {
				System.out.println("Waiting for client connection...");
				Socket socket = server.accept();
				link++;
				System.out.println(link + " person connected!");

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
		private String host;

		public ClientHandler(Socket socket) { // pass in server's socket
			this.socket = socket;
			InetAddress address = socket.getInetAddress();
			host = address.getHostAddress();
		}

		public void run() {
			PrintWriter pw = null;
			try {
				InputStream in = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(in, "UTF-8");
				BufferedReader br = new BufferedReader(isr);

				OutputStream out = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
				pw = new PrintWriter(osw, true);

				synchronized (allOut) {
					allOut.add(pw);
				}

				String message = null;
				while ((message = br.readLine()) != null) {
					System.out.println("Client " + host + " said: " + message);

					synchronized (allOut) {
						for (PrintWriter o : allOut) {
							o.println("Client " + host + " said: " + message);
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				System.out.println("A friend logged off!");

				synchronized (allOut) {
					allOut.remove(pw);
				}

				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}
}
