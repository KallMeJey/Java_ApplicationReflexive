public class AppliProg {

	private static final Integer PORT_PROG = 5001;
	
	public static void main(String[] args) {
		new Thread(new Client("localhost", PORT_PROG)).start();
	}
	
}
