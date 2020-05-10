package appli;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import model.Programmeur;
import model.Serializable;
import network.Server;
import network.services.ServiceAmateur;
import network.services.ServiceProgammeur;

public class Bri {

	private static Bri instance;
	private static final String populateFolderPath = "populate";
	private List<Programmeur> programmeurs;

	static {
		instance = new Bri();
	}

	private Bri() {
		this.programmeurs = new Vector<Programmeur>();
		this.populate(Programmeur.class);
	}

	public static Bri getInstance() {
		return instance;
	}

	public void startServers(Integer portAma, Integer portProg) {
		new Thread(new Server(portAma, ServiceAmateur.class)).start();
		new Thread(new Server(portProg, ServiceProgammeur.class)).start();
	}

	private void populate(Class<? extends Serializable> type) {
		String typeName = type.getSimpleName();

		try {
			Serializable serializer = type.newInstance();

			try {
				Scanner in = new Scanner(
						new FileInputStream(populateFolderPath + "/" + typeName.toLowerCase() + ".txt"));
				while (in.hasNext()) {
					String line = in.nextLine();
					if (line.isEmpty() == false) {
						if (type == Programmeur.class) {
							this.programmeurs.add((Programmeur) serializer.deserialize(line));
						}
					}
				}
				System.out.println("Chargement de " + this.programmeurs.size() + " programmeurs");
				in.close();
			} catch (FileNotFoundException e) {
				System.out.println("Impossible d'ouvrir le fichier");
				e.printStackTrace();
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		System.out.println("Sauvegarde de " + this.programmeurs.size() + " programmeurs");
		String filePath = populateFolderPath + "/programmeur.txt";
		try {
			FileWriter writer = new FileWriter(filePath, false);
			this.programmeurs.forEach(user -> {
				try {
					writer.write(user.serialize() + System.lineSeparator());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			writer.close();
			System.out.println("Sauvegarde terminée");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public List<Programmeur> getProgrammeurs() {
		System.out.println(this.programmeurs.size());
		return this.programmeurs;
	}

	public Map<Programmeur, List<Class<?>>> getRunningServices() {
		Map<Programmeur, List<Class<?>>> services = new HashMap<Programmeur, List<Class<?>>>();

		this.programmeurs.stream().forEach(prog -> services.put(prog, prog.getServiceRegistry().getRunningServices()));

		return services;
	}
}
