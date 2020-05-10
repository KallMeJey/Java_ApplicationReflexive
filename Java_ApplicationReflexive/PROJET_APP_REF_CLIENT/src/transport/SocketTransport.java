package transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class SocketTransport implements Transport, Runnable {

	private Socket socket;
	private Integer port;
	private String hostname;
	private BufferedReader socketIn;
	private PrintWriter socketOut;
	
	public SocketTransport(String host, Integer port) {
		this.hostname = host;
		this.port = port;
	}
	
	@Override
	public void run() {
		this.exec();
	}
	
	@Override
	public void exec() {
		try {
			this.socket = new Socket(this.hostname, this.port);
			this.onConnect();
		} catch (IOException e) {
			this.onError("Erreur de connexion", e);
		}
		
		if(this.socket.isConnected()) {
			try {
				socketIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				socketOut = new PrintWriter(this.socket.getOutputStream(), true);
				
				while(true) {
					if(this.socket.isClosed()) break;
					String line = socketIn.readLine();
					if(line == null) continue;
					if(!line.isEmpty()) this.onMessage(line);
				}
			} catch (IOException e) {
				this.onError("Erreur lors de la manipulation de la socket", e);
			}
		}
		
		this.end();
	}
	
	@Override
	public void send(String msg) {
		this.socketOut.println(msg);
	}
	
	@Override
	public void end() {
		try {
			if(this.socket.isConnected()) {
				this.socket.close();
				this.onDisconnect();
			}
		} catch (IOException e) {
			this.onError("Erreur lors de fermeture de la socket", e);
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.socket.close();
	}
}
