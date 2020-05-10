package guillaume;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import bri.Service;
import guillaume.mail.MailBuilder;
import utils.Condition;

public class ServiceXML implements Service {

	private Map<String, Integer> typeCounts;
	private Integer sumSubs = 0;
	private Integer countSUbs = 0;

	private final Socket client;

	public ServiceXML(Socket socket) {
		client = socket;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);

			SocketUtil util = new SocketUtil(client, in, out);

			util.ecrire("Bienvenue sur le service d'analyse de fichié xml");

			String xmlLink = "";

			do {
				if (xmlLink == "")
					util.poserQuestion("Merci d'indiquer un lien ftp");
				else
					util.poserQuestion("Lien ftp incorrect ou serveur ftp injoignable");

				xmlLink = util.lire();
				if (xmlLink == null)
					continue;
				if (xmlLink.isEmpty())
					continue;
			} while (!util.check(xmlLink, new Condition<Boolean>() {
				@Override
				public Boolean check(Object t) {

					String link = (String) t;
					if (!link.startsWith("ftp://"))
						return false;

					return isURL(link);
				}
			}));

			util.ecrire("Nous allons débuter l'analyse du fichier :");
			util.ecrire(xmlLink);

			util.ecrire("L'analyse pouvant prendre du temps, nous allons vous envoyer les résultats par email");

			String email = "";

			do {
				if (email == "")
					util.poserQuestion("Merci d'indiquer votre adresse email");
				else
					util.poserQuestion("Adresse email incorrecte");

				email = util.lire();
				if (email == null)
					continue;
				if (email.isEmpty())
					continue;
			} while (!util.check(email, new Condition<Boolean>() {
				@Override
				public Boolean check(Object t) {
					return isValidEmailAddress((String) t);
				}
			}));
			
			util.ecrire("Très bien !");
			util.ecrire("Nous allons traiter votre fichier et vous enverrons les résultats sur votre adresse email.");
			util.terminer();
			
			try {
				String output = analyze(xmlLink);
				System.out.println("Analyse du fichier " + xmlLink + " terminée, envoie en cours de l'email...");
				
				try {
					(new MailBuilder())
					.setAuth("service.xml@poneyhost.eu", "lyntabia93")
					.setFrom("service.xml@poneyhost.eu")
					.setTo(email)
					.setHost("srv2.poneyhost.eu", 587)
					.setSubject("Analyse du fichier terminé !")
					.setContent("Voici les résultats de votre analyse : \n\n\n" + output)
					.send();
					System.out.println("Email envoyé !");
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			} catch (ParserConfigurationException | SAXException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String analyze(String xmlUrl) throws IOException, ParserConfigurationException, SAXException {
		URL url = new URL(xmlUrl);

		URLConnection con = url.openConnection();

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final Document document = builder.parse(con.getInputStream());

		final Element racine = document.getDocumentElement();

		typeCounts = new HashMap<String, Integer>();

		StringBuilder sb = new StringBuilder();
		sb.append("Fichié analysé : " + url + "\n");
		sb.append("Charset du fichier : " + document.getXmlEncoding() + "\n");
		sb.append("Version XML utilisé : " + document.getXmlVersion() + "\n");
		sb.append("Architecture de votre XML: \n");
		deepSearch(sb, racine, 2);

		sb.append("\nAnalyse des différents utilisés : \n");

		typeCounts.entrySet().stream().sorted(Map.Entry.comparingByValue(new Comparator<Object>() {
			@Override
			public int compare(Object a, Object b) {
				if ((Integer) a >= (Integer) b) {
					return -1;
				} else {
					return 1;
				}
			}
		})).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
				.forEach((key, value) -> sb.append("- Le type " + key + " a été utilisé " + value + " fois\n"));

		if (countSUbs != 0 && sumSubs != 0)
			sb.append(
					"\nEn moyenne les nodes de votre xml sont composées de " + (sumSubs / countSUbs) + " enfants");

		return sb.toString();
	}

	private void deepSearch(StringBuilder sb, Node r, Integer parent) {
		List<Node> nodes = convertNodeListToList(r.getChildNodes());

		for (int i = 0; i < (parent * 2); i++)
			sb.append(" ");

		sb.append("| ");
		sb.append(r.getNodeName());

		nodes.stream().filter(node -> (node.getNodeType() != Node.ELEMENT_NODE))
				.filter(node -> !node.getNodeValue().trim().isEmpty()).forEach(node -> {
					String type = findType(node.getNodeValue().trim());
					countType(type);
					sb.append(" (" + type + "::'" + node.getNodeValue().trim() + "')");
				});

		int nbChilds = (int) nodes.stream().filter(node -> (node.getNodeType() != Node.ELEMENT_NODE)).count() - 1;

		if (nbChilds > 0) {
			sumSubs += nbChilds;
			countSUbs++;
		}

		sb.append(" \n");

		nodes.stream().filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
				.forEach(node -> deepSearch(sb, node, parent + 1));
	}

	private void countType(String type) {
		if (!this.typeCounts.containsKey(type))
			this.typeCounts.put(type, 1);
		else {
			this.typeCounts.replace(type, this.typeCounts.get(type) + 1);
		}
	}

	private List<Node> convertNodeListToList(NodeList l) {
		List<Node> nodes = new ArrayList<>();

		for (int i = 0; i < l.getLength(); i++) {
			nodes.add(l.item(i));
		}

		return nodes;
	}

	private String findType(String value) {
		try {
			Integer.parseInt(value);
			return "number";
		} catch (Exception e) {
		}

		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false") || value.equalsIgnoreCase("vrai")
				|| value.equalsIgnoreCase("faux") || value.equalsIgnoreCase("0") || value.equalsIgnoreCase("1"))
			return "boolean";

		try {
			Float.parseFloat(value);
			return "float";
		} catch (Exception e) {
		}

		try {
			new URL(value);
			return "url";
		} catch (Exception e) {
		}

		return "string";
	}

	protected void finalize() throws Throwable {
		client.close();
	}

	public static String toStringue() {
		return "Analyse de fichier xml";
	}

	private boolean isURL(String url) {
		try {
			(new java.net.URL(url)).openStream().close();
			return true;
		} catch (Exception ex) {
		}
		return false;
	}

	private boolean isValidEmailAddress(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}

}
