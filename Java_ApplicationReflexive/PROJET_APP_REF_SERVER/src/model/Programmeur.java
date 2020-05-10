package model;

import bri.ServiceRegistry;

public class Programmeur extends Serializable {

	private String login;
	private String password;
	private ServiceRegistry serviceRegistry;
	private Repository repository;
	
	public Programmeur() {}
	
	public Programmeur(String login, String password, String repositoryUrl) {
		this.login = login;
		this.password = password;
		this.repository = new Repository(repositoryUrl, this);
		this.serviceRegistry = new ServiceRegistry(this);
	}
	
	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}
	
	public Repository getRepository() {
		return this.repository;
	}
	
	public String getPackageName() {
		return this.login;
	}

	public String getLogin() {
		return login;
	}
	
	public boolean comparePassword(String pass) {
		return this.password.equals(pass);
	}
	
	@Override
	public Serializable deserialize(String line) {
		String[] parts = line.split(";");
		return new Programmeur(parts[0], parts[1], parts[2]);
	}

	@Override
	public String serialize() {
		return String.join(";", new String[] {login, password, repository.getUrl()});
	}
}
