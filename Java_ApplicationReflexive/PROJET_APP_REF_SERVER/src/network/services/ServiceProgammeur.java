package network.services;

import java.io.IOException;
import java.net.Socket;
import java.util.stream.Collectors;

import appli.Bri;
import bri.State;
import bri.validator.BriValidator;
import model.Programmeur;
import utils.Condition;
import utils.Pair;

public class ServiceProgammeur extends Service {

	public ServiceProgammeur(Socket socket) {
		super(socket);
	}

	@Override
	public void exec() throws IOException {
		Bri bri = Bri.getInstance();

		this.ecrire("######################################################");
		this.ecrire("Bienvenue sur la plateforme de services dynamiques");
		this.ecrire(this.centerString(54, "BRI"));
		this.ecrire("######################################################");

		this.sauterUneLigne();

		this.ecrire("Vous devez être authentifé pour acceder à ce service.");

		this.sauterUneLigne();

		String username = "";

		do {
			if (username == "")
				this.poserQuestion("Merci d'indiquer votre nom d'utilisateur");
			else
				this.poserQuestion("Nom d'utilisateur incorrect");

			username = this.lire();
			if (username == null)
				continue;
			if (username.isEmpty())
				continue;
		} while (!this.check(username, new Condition<Boolean>() {
			@Override
			public Boolean check(Object t) {
				if (bri.getProgrammeurs().stream().filter(prog -> prog.getLogin().equals((String) t)).count() > 0) {
					return true;
				}

				return false;
			}
		}));

		final String uTmp = username;

		Programmeur session = bri.getProgrammeurs().stream().filter(prog -> prog.getLogin().equals(uTmp)).findFirst()
				.get();

		String password = "";

		do {
			if (password == "")
				this.poserQuestion("Merci d'indiquer votre mot de passe");
			else
				this.poserQuestion("Mot de passe incorrect");

			password = this.lire();
			if (password == null)
				continue;
			if (password.isEmpty())
				continue;
		} while (!this.check(password, new Condition<Boolean>() {
			@Override
			public Boolean check(Object t) {
				return session.comparePassword((String) t);
			}
		}));

		this.ecrire("Bienvenue " + session.getLogin() + " !");

		this.showMenu(session);
	}

	private void showMenu(Programmeur session) throws IOException {
		final ServiceProgammeur instance = this;
		this.ecrire("######################################################");
		this.sauterUneLigne();
		this.ecrire(this.centerString(54, "[ MENU ]"));
		this.ecrire("    #1 Lister mes services");
		this.ecrire("    #2 Installer un nouveau service");
		this.ecrire("    #3 Modifier mon lien ftp");
		this.sauterUneLigne();
		this.ecrire(this.centerString(54, "[ MENU ]"));
		this.ecrire("######################################################");

		String indexAction = "";

		do {
			if (indexAction == "")
				this.poserQuestion("Merci d'indiquer le numéro de l'action que vous souhaitez faire");
			else
				this.poserQuestion("Merci d'indiquer un numéro correct");

			indexAction = this.lire();
			if (indexAction == null)
				continue;
			if (indexAction.isEmpty())
				continue;
		} while (!this.check(indexAction, new Condition<Boolean>() {
			@Override
			public Boolean check(Object t) {
				if (!instance.isNumeric((String) t)) {
					return false;
				}

				return Integer.valueOf((String) t) >= 1 && Integer.valueOf((String) t) <= 3;
			}
		}));

		switch (indexAction) {
		case "1":
			this.listServices(session);
			break;
		case "2":
			this.installService(session);
			break;
		case "3":
			this.editFtp(session);
			break;
		}
	}

	private void listServices(Programmeur session) throws IOException {
		final ServiceProgammeur instance = this;

		this.ecrire("######################################################");
		this.ecrire(this.centerString(54, "[ MES SERVICES ]"));

		session.getServiceRegistry().getAllServices().forEach((clazz, state) -> {
			int index = session.getServiceRegistry().getAllServices().keySet().stream().collect(Collectors.toList())
					.indexOf(clazz) + 1;
			this.ecrire("    #" + index + " " + clazz.getSimpleName() + " [" + state.toString() + "]");
		});

		if (session.getServiceRegistry().getAllServices().size() == 0) {
			this.ecrire("Vous n'avez installer encore aucun service.");
		} else {
			this.poserQuestion("Voulez-vous administrer un service ? (oui/non)");
		}

		String rep2Str = this.lire();
		boolean poursuivre = this.formatBoolean(rep2Str);

		if (poursuivre) {
			String indexService = "";

			do {
				if (indexService == "")
					this.poserQuestion("Merci d'indiquer le numéro du service que vous souhaitez administrer");
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

					if (Integer.valueOf((String) t) > 0
							&& session.getServiceRegistry().getAllServices().size() >= Integer.valueOf((String) t)) {
						return true;
					}

					return false;
				}
			}));

			Class<?> clazz = session.getServiceRegistry().getAllServices().keySet().stream()
					.collect(Collectors.toList()).get(Integer.valueOf(indexService) - 1);

			Pair<Class<?>, State> serv = new Pair<>(clazz, session.getServiceRegistry().getAllServices().get(clazz));

			this.manageService(session, serv);
		}

		this.showEnd(session);
	}

	private void installService(Programmeur session) throws IOException {
		this.ecrire(this.centerString(54, "[ INSTALLER UN NOUVEAU SERVICES ]"));
		this.ecrire("Attention, vous allez installer un nouveau service.");
		this.ecrire("Ce service doit respecter la norme bri, sinon elle sera refusée.");
		this.sauterUneLigne();

		String classPath = "";

		do {
			if (classPath == "")
				this.poserQuestion("Merci d'indiquer le service a installer");
			else
				this.poserQuestion("Ce service est introuvable dans votre ftp");

			classPath = this.lire();
			if (classPath == null)
				continue;
			if (classPath.isEmpty())
				continue;
		} while (!this.check(classPath, new Condition<Boolean>() {
			@Override
			public Boolean check(Object t) {
				String className = (String) t;

				if (!className.startsWith(session.getPackageName() + ".")) {
					className = session.getPackageName() + "." + className;
				}

				if (session.getRepository().search(className) == null)
					return false;
				return true;
			}
		}));

		if (!classPath.startsWith(session.getPackageName() + ".")) {
			classPath = session.getPackageName() + "." + classPath;
		}

		this.ecrire("Le service " + classPath + " à bien été trouvée ! Verificiation en cours...");

		try {
			if (BriValidator.validate(session.getRepository().search(classPath))) {
				try {
					session.getServiceRegistry().installService(classPath);
					this.ecrire("Le service " + classPath + " a bien été installé !");
				} catch (Exception e) {
					e.printStackTrace();
					this.ecrire("Une erreur est survenue lors de la tentative d'installation du service");
					this.ecrire("Motif: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			this.ecrire("Votre classe ne respecte pas toutes les contraintes bri");
			this.ecrire("Erreur: " + e.getMessage());
		}

		this.showEnd(session);
	}

	private void editFtp(Programmeur session) throws IOException {
		this.ecrire("######################################################");
		this.ecrire(this.centerString(54, "[ EDITION DE MON LIEN FTP ]"));

		this.ecrire("FTP actuellement définit sur : " + session.getRepository().getUrl());

		this.poserQuestion("Voulez-vous editer votre lien ? (oui/non)");

		String rep2Str = this.lire();
		boolean poursuivre = this.formatBoolean(rep2Str);

		if (poursuivre) {
			String newFtp = "";

			do {
				if (newFtp == "")
					this.poserQuestion("Merci d'indiquer votre nouveau lien ftp");
				else
					this.poserQuestion("Lien ftp incorrect ou serveur ftp injoignable");

				newFtp = this.lire();
				if (newFtp == null)
					continue;
				if (newFtp.isEmpty())
					continue;
			} while (!this.check(newFtp, new Condition<Boolean>() {
				@Override
				public Boolean check(Object t) {

					String link = (String) t;
					if (!link.startsWith("ftp://"))
						return false;

					return isURL(link);
				}
			}));

			this.ecrire("Modification de votre lien ftp en " +  newFtp + " effectuée");
			session.getRepository().setUrl(newFtp);
		}

		this.showEnd(session);
	}

	private void showEnd(Programmeur session) throws IOException {
		this.poserQuestion("Voulez-vous retourner au menu principal ? (oui/non)");

		String rep2Str = this.lire();
		boolean poursuivre = this.formatBoolean(rep2Str);

		if (poursuivre)
			this.showMenu(session);
		else {
			this.sauterUneLigne();
			this.ecrire("A très bientot");
			this.terminer();
		}
	}

	private void manageService(Programmeur session, Pair<Class<?>, State> service) throws IOException {
		final ServiceProgammeur instance = this;
		this.ecrire("######################################################");
		this.ecrire(this.centerString(54, "[ ADMINISTRATION DU SERVICE ]"));
		this.ecrire("Nom du service : " + service.getKey().getSimpleName());
		this.ecrire("Nom complet du service : " + service.getKey().getName());
		this.ecrire("Auteur : " + session.getLogin());
		this.ecrire("Etat : " + service.getValue().toString());

		this.sauterUneLigne();

		this.ecrire("Taper : ");
		this.ecrire("  #1 " + ((service.getValue() == State.OFFLINE) ? "Allumer le service" : "Eteindre le service"));
		this.ecrire("  #2 Désinstaller le service");
		this.ecrire("  #3 Mettre à jour le service");
		this.ecrire("  #4 Retour");

		String indexAction = "";

		do {
			if (indexAction == "")
				this.poserQuestion("Merci d'indiquer le numéro de l'action que vous souhaitez faire");
			else
				this.poserQuestion("Merci d'indiquer un numéro correct");

			indexAction = this.lire();
			if (indexAction == null)
				continue;
			if (indexAction.isEmpty())
				continue;
		} while (!this.check(indexAction, new Condition<Boolean>() {
			@Override
			public Boolean check(Object t) {
				if (!instance.isNumeric((String) t)) {
					return false;
				}

				return Integer.valueOf((String) t) >= 1 && Integer.valueOf((String) t) <= 4;
			}
		}));

		int index = session.getServiceRegistry().getAllServices().keySet().stream().collect(Collectors.toList())
				.indexOf(service.getKey());

		switch (indexAction) {
		case "1":
			if (service.getValue() == State.OFFLINE)
				session.getServiceRegistry().startService(index);
			else
				session.getServiceRegistry().stopService(index);

			break;
		case "2":
			if (session.getServiceRegistry().uninstallService(index))
				this.ecrire("Le service à bien été désinstallé");
			else
				this.ecrire("Impossible de supprimer ce service, peut-être n'existe t-il pas ou plus.");

			this.listServices(session);
			return;
		case "3":
			this.ecrire("Mise à jour du service en cours...");
			this.ecrire("Désintallation en cours...");
			if (session.getServiceRegistry().uninstallService(index))
				this.ecrire("Le service à bien été désinstallé");
			else
				this.ecrire("Impossible de supprimer ce service, peut-être n'existe t-il pas ou plus.");
			this.ecrire("Installation de la nouvelle version en cours...");
			try {
				if (BriValidator.validate(session.getRepository().search(service.getKey().getName()))) {
					try {
						session.getServiceRegistry().installService(service.getKey().getName());
						this.ecrire("Le service " + service.getKey().getName() + " a bien été mis à jour");
					} catch (Exception e) {
						e.printStackTrace();
						this.ecrire("Une erreur est survenue lors de la tentative de mise à jour du service");
						this.ecrire("Motif: " + e.getMessage());
					}
				}
			} catch (Exception e) {
				this.ecrire("Votre classe ne respecte pas toutes les contraintes bri");
				this.ecrire("Erreur: " + e.getMessage());
			}
			this.ecrire("Restauration de l'état du service en cours...");
			if (service.getValue() == State.ONLINE)
				session.getServiceRegistry().startService(session.getServiceRegistry().getAllServices().size() - 1);
			this.ecrire("Etat du service restauré");
			this.ecrire("Mise à jour terminée !");
			this.listServices(session);
			return;
		case "4":
			this.listServices(session);
			return;
		}

		this.manageService(session,
				new Pair<>(service.getKey(), session.getServiceRegistry().getAllServices().get(service.getKey())));
	}

	private boolean isURL(String url) {
		try {
			(new java.net.URL(url)).openStream().close();
			return true;
		} catch (Exception ex) {
		}
		return false;
	}

}
