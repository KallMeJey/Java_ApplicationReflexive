public class AppliAma {
	
	private static final Integer PORT_AMA = 5000;
	
	public static void main(String[] args) {
		new Thread(new Client("localhost", PORT_AMA)).start();
	}
}
