package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import appli.Bri;

public class Repository {

	private String url;
	private URLClassLoader loader;

	public Repository(String url, Programmeur p) {
		this.url = url;
		try {
			List<URL> urls = new ArrayList<>();
			urls.add(new URL(url));
			try {
				URL urlL = new URL(url + p.getLogin() + "/libs.loader");
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(urlL.openStream()));
				
				if(reader.ready()) 
					System.out.println("Un fichier " + urlL + "libs.loader a été detecté");
				
				String line;
				
				while ((line = reader.readLine()) != null) {
					URL tmp = new URL(url + p.getLogin() + "/" + line);
					System.out.println("Ajout dans le class loader de la bibliothèque : " + tmp);
					urls.add(tmp);
				}

				reader.close();
				
			} catch (IOException e) {}
			this.loader = new URLClassLoader(urls.toArray(new URL[] {}));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public URLClassLoader getClassLoader() {
		return this.loader;
	}

	public Class<?> search(String clazz) {
		System.out.println("Searching " + clazz);
		try {
			return this.loader.loadClass(clazz);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public void setUrl(String url) {
		this.url = url;

		try {
			this.loader = new URLClassLoader(new URL[] { new URL(url) });
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				Bri.getInstance().save();
			}
		}).start();
	}

	public String getUrl() {
		return url;
	}
}
