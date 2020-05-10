package network;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

import network.services.Service;


public class Server implements Runnable {

	private ServerSocket srv;
	private Integer port;
	private Class<? extends Service> service;
	
	public Server(Integer port, Class<? extends Service> service) {
		this.port = port;
		this.service = service;
	}

	@Override
	public void run() {
		try {
			this.srv = new ServerSocket(this.port);
			System.out.println("Le service " + this.service.getSimpleName() + " est lancée et écoute sur le port " + this.port);
			while(true) {
				Socket client = this.srv.accept();
				
				try {
					Object instance = this.service.getConstructor(new Class[] { Socket.class }).newInstance(client);
					new Thread((Runnable) instance).start();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.srv.close();
	}
	
}
