package guillaume;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import utils.Condition;

public class SocketUtil {

	private Socket socket;
	private BufferedReader socketIn;
	private PrintWriter socketOut;
	
	public SocketUtil(Socket s, BufferedReader socketIn, PrintWriter socketOut) {
		this.socket = s;
		this.socketIn = socketIn;
		this.socketOut = socketOut;
	}
	
	/**
	 * Lire un message du client
	 * @return String
	 * @throws IOException
	 */
	public String lire() throws IOException {
		return this.socketIn.readLine();
	}

	/**
	 * Envoyer un message au client
	 * @param msg
	 */
	public void ecrire(String msg) {
		this.socketOut.println("msg:" + msg);
	}

	/**
	 * Poser une question au client,
	 * Va au niveau du client attendre une saisie clavier.
	 * @param msg
	 */
	public void poserQuestion(String msg) {
		this.socketOut.println("ask:" + msg);
	}

	/**
	 * Indiquer au client de sauter une ligne
	 */
	public void sauterUneLigne() {
		this.socketOut.println("newLine");
	}
	
	/**
	 * Indique au client de fermer la connexion,
	 * On l'a ferme �galement de notre cot�.
	 */
	public void terminer() {
		try {
			this.socketOut.println("end");
			this.socket.close();
		} catch (IOException e) {}
	}
	
	/**
	 * Fonction qui est utilis� au niveau de la v�rification des donn�es envoy�s par le client
	 * @param str : donn�e voulant �tre v�rifi�e
	 * @param cond : callback d�finissant l'ensemble des conditions a respecter 
	 * @return true si l'ensemble des conditions sont respect�es
	 */
	public boolean check(String str, Condition<Boolean> cond) {
		return cond.check(str);
	}
	
	public boolean isNumeric(String str) {
		try {
			Integer.valueOf(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isBoolean(String str) {
		if (str.equalsIgnoreCase("oui") || str.equalsIgnoreCase("non") || str.equalsIgnoreCase("true")
				|| str.equalsIgnoreCase("false") || str.equalsIgnoreCase("o") || str.equalsIgnoreCase("n")
				|| str.equalsIgnoreCase("y"))
			return true;
		else
			return false;
	}

	/**
	 * Retourne true si str vaut "oui", "true", "o", "y", false sinon
	 * @param str
	 * @return 
	 */
	public boolean formatBoolean(String str) {
		if (!this.isBoolean(str))
			return false;

		if (str.equalsIgnoreCase("oui") || str.equalsIgnoreCase("true") || str.equalsIgnoreCase("o")
				|| str.equalsIgnoreCase("y"))
			return true;
		else
			return false;
	}
	
	/**
	 * Permet de centrer une chaine
	 * @param width
	 * @param s
	 * @return
	 */
	//https://stackoverflow.com/a/50162404
	public String centerString(int width, String s) {
	    return String.format("%-" + width  + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
	}
	
}
