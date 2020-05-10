import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

import transport.SocketTransport;

public class Client extends SocketTransport {

	private Scanner sc;
	
	public Client(String hostname, Integer port) {
		super(hostname, port);
		this.sc = new Scanner(System.in);
	}

	@Override
	public void onMessage(String msg) {
		String[] parts = msg.split(":");

		switch (parts[0]) {
			case "end":
				this.end();
				break;
			case "newLine":
				System.out.println("");
				break;
			case "ask":
				if(parts.length >= 2) System.out.println(formatMsg(parts));
				System.out.print("> ");
				String rep = this.sc.nextLine();
				this.send(rep);
				break;
			case "msg":
				if(parts.length >= 2) System.out.println(formatMsg(parts));
				break;
		}
	}
	
	private String formatMsg(String[] parts) {
		return String.join(": ", Arrays.asList(parts).stream().skip(1).collect(Collectors.toList()));
	}

	@Override
	public void onConnect() {
		System.out.println("Vous êtes désormais connecté au service !");
		System.out.println("");
	}

	@Override
	public void onError(String error, Exception e) {
		System.err.println("Une erreur est survenue");
		System.err.println(error);
		e.printStackTrace();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.sc.close();
	}

	@Override
	public void onDisconnect() {
		System.out.println("Vous êtes désormais deconnecté");
	}
}