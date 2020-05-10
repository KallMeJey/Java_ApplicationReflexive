package network.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import utils.Condition;

public abstract class Service implements Runnable {

	private Socket socket;
	private BufferedReader socketIn;
	private PrintWriter socketOut;

	public Service(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			socketIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			socketOut = new PrintWriter(this.socket.getOutputStream(), true);

			this.exec();
		} catch (IOException e) {
			if(!e.getMessage().equals("socket closed") && !e.getMessage().equals("Connection reset")) {
				System.err.println("Une erreur est survenue lors de la manipulation de la socket");
				e.printStackTrace();
			}
		} finally {
			try {
				this.socket.close();
			} catch (IOException e) {
				System.err.println("Impossible de fermer le socket");
				e.printStackTrace();
			}
		}
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
	 * On l'a ferme également de notre coté.
	 */
	public void terminer() {
		try {
			this.socketOut.println("end");
			this.socket.close();
		} catch (IOException e) {}
	}
	
	/**
	 * Fonction qui est utilisé au niveau de la vérification des données envoyés par le client
	 * @param str : donnée voulant être vérifiée
	 * @param cond : callback définissant l'ensemble des conditions a respecter 
	 * @return true si l'ensemble des conditions sont respectées
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

	public abstract void exec() throws IOException;

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.socket.close();
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
	
	public Socket getSocket() {
		return socket;
	}
	
}
