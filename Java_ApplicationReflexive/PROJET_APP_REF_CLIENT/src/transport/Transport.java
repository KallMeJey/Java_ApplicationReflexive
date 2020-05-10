package transport;

public interface Transport {

	void onMessage(String msg);
	
	void onConnect();
	
	void onDisconnect();
	
	void onError(String error, Exception e);
	
	void send(String msg);
	
	void end();
	
	void exec();
	
}
