package network.services;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import appli.Bri;
import model.Programmeur;
import utils.Condition;
import utils.Pair;

public class ServiceAmateur extends Service {

	public ServiceAmateur(Socket socket) {
		super(socket);
	}

	@Override
	public void exec() throws IOException {
		Bri bri = Bri.getInstance();
		final ServiceAmateur instance = this;
		
		this.ecrire("######################################################");
		this.ecrire("Bienvenue sur la plateforme de services dynamiques");
		this.ecrire(this.centerString(54, "BRI"));
		this.ecrire("######################################################");
		
		this.sauterUneLigne();
		
		this.ecrire("Liste des services disponibles actuellement ::");
		
		List<Pair<Programmeur, Class<?>>> tmp = new ArrayList<Pair<Programmeur, Class<?>>>();
		
		bri.getRunningServices().forEach((prog, services) -> {
			services.stream().forEach(clazz -> {
				tmp.add(new Pair<>(prog, clazz));
				this.ecrire("    #" + tmp.size() + " " + clazz.getSimpleName() + " par " + prog.getLogin());
			});
		});
		
		String indexService = "";
		
		do {
			if (indexService == "")
				this.poserQuestion("Merci d'indiquer le numéro du service que vous souhaitez utiliser");
			else
				this.poserQuestion("Merci d'indiquer un numéro correct");

			indexService = this.lire();
			if (indexService == null)
				continue;
			if (indexService.isEmpty())
				continue;
		} while (!this.check(indexService, new Condition<Boolean>() {
			@Override
			public Boolean check(Object t) {
				if (!instance.isNumeric((String) t)) {
					return false;
				}

				if(Integer.valueOf((String) t) > 0 && tmp.size() >= Integer.valueOf((String) t)) {
					return true;
				}
				
				return false;
			}
		}));
		
		Class<?> serviceClass = tmp.get(Integer.valueOf(indexService) - 1).getValue();
		
		System.out.println("Execution du service : " + serviceClass.getName());
		
		try {
			bri.Service runnable = (bri.Service) serviceClass.getDeclaredConstructor(Socket.class).newInstance(this.getSocket());
			Thread t = new Thread(runnable);
			t.start();
			
			t.join();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			this.ecrire("Un problème est survenue, impossible d'éxecuter ce service.");
		} catch (InterruptedException e) {
			e.printStackTrace();
			this.ecrire("Un problème est survenue, le service s'est arreté subitement.");
		}
		
		this.sauterUneLigne();
		this.ecrire("A très bientot");
		this.terminer();
	}
}
