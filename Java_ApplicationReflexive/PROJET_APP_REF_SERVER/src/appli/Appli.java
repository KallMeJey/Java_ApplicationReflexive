package appli;

public class Appli {
	private static final Integer PORT_AMA = 5000;
	private static final Integer PORT_PROG = 5001;

	public static void main(String[] args) throws ClassNotFoundException, Exception {
		/*
		 * Programmeur p = new Programmeur("guillaume", "test",
		 * "ftp://srv4.poneyhost.eu/");
		 * p.getServiceRegistry().installService("ServiceInversion");
		 * 
		 * System.out.println(p.getServiceRegistry());
		 * 
		 * System.out.println(p.getServiceRegistry().startService(0));
		 * 
		 * System.out.println(p.getServiceRegistry().getRunningServices().size());
		 * System.out.println(p.getServiceRegistry());
		 * 
		 * System.out.println(p.getServiceRegistry().stopService(0));
		 * 
		 * System.out.println(p.getServiceRegistry().getRunningServices().size());
		 * System.out.println(p.getServiceRegistry());
		 * 
		 * System.out.println(p.getServiceRegistry().uninstallService(0));
		 * 
		 * System.out.println(p.getServiceRegistry());
		 */
		
		Bri.getInstance().startServers(PORT_AMA, PORT_PROG);
	}
}
