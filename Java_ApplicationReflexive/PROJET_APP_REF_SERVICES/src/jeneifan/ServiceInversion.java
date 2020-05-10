package jeneifan;

import java.io.*;
import java.net.*;

import bri.Service;
import guillaume.SocketUtil;
import utils.Condition;

public class ServiceInversion implements Service {

	private final Socket client;

	public ServiceInversion(Socket socket) {
		client = socket;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			SocketUtil util = new SocketUtil(client, in, out);

			String text = "";

			do {
				util.poserQuestion("Merci d'indiquerle texte que vous voulez inverser");

				text = util.lire();
				if (text == null)
					continue;
				if (text.isEmpty())
					continue;
			} while (!util.check(text, new Condition<Boolean>() {
				@Override
				public Boolean check(Object t) {
					return true;
				}
			}));
			
			String invLine = new String(new StringBuffer(text).reverse());

			util.ecrire("Texte inversé en " + invLine);

			util.terminer();
		} catch (IOException e) {
		}
	}

	protected void finalize() throws Throwable {
		client.close();
	}

	public static String toStringue() {
		return "Inversion de texte";
	}
}