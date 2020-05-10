package bri;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import bri.validator.BriValidator;
import model.Programmeur;

public class ServiceRegistry {

	private Map<Class<?>, State> services;
	private Programmeur programmeur;

	public ServiceRegistry(Programmeur programmeur) {
		this.services = new ConcurrentHashMap<Class<?>, State>();
		this.programmeur = programmeur;
	}

	private void addService(Class<?> clazz) throws Exception {
		if (BriValidator.validate(clazz)) {
			if (clazz.getPackage().getName().equals(programmeur.getPackageName())) {
				System.out.println("Installing " + clazz.getName());
				services.put(clazz, State.OFFLINE);
			} else
				throw new Exception("Pas bon package");
		}
	}

	public Class<?> getServiceClassByName(String className) {
		try {
			return this.services.keySet().stream().filter(clazz -> clazz.getSimpleName().equals(className)).findFirst()
					.get();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	public Class<?> getServiceClassByIndex(Integer index) {
		if (this.services.size() > index)
			return this.services.keySet().stream().skip(index).findFirst().get();

		return null;
	}

	public State getServiceStatus(Integer index) {
		return getServiceStatus(getServiceClassByIndex(index));
	}

	public State getServiceStatus(Class<?> clazz) {
		if (this.services.containsKey(clazz))
			return this.services.get(clazz);

		else
			return null;
	}

	public boolean startService(Integer index) {
		Class<?> clazz = getServiceClassByIndex(index);
		if (clazz != null) {
			State state = getServiceStatus(clazz);
			if (state == State.OFFLINE) {
				System.out.println("Starting service " + clazz.getName());
				this.services.replace(clazz, State.ONLINE);
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	public boolean stopService(Integer index) {
		Class<?> clazz = getServiceClassByIndex(index);
		if (clazz != null) {
			State state = getServiceStatus(clazz);
			if (state == State.ONLINE) {
				System.out.println("Stopping service " + clazz.getName());
				this.services.replace(clazz, State.OFFLINE);
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	public boolean uninstallService(Integer index) {
		Class<?> clazz = getServiceClassByIndex(index);
		if (clazz != null) {
			System.out.println("Unninstalling service " + clazz.getName());
			this.services.remove(clazz);
			return true;
		}
		return false;
	}

	public boolean installService(String className) throws Exception {
		if (!className.startsWith(programmeur.getPackageName() + ".")) {
			className = programmeur.getPackageName() + "." + className;
		}

		Class<?> c = this.programmeur.getRepository().search(className);
		if (c == null)
			return false;

		this.addService(c);

		return true;
	}
	
	public List<Class<?>> getRunningServices(){
		return this.services.keySet().stream().filter(clazz -> {
			return this.services.get(clazz) == State.ONLINE;
		}).collect(Collectors.toList());
	}
	
	public Map<Class<?>, State> getAllServices(){
		return this.services;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Liste des services :\n");
		this.services.keySet().stream().forEach(
				clazz -> sb.append("- " + clazz.getSimpleName() + " (" + this.services.get(clazz).toString() + ")\n"));
		return sb.toString();
	}
}