package bri.validator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.Arrays;

import bri.Service;

public class BriValidator {

	public static boolean validate(Class<?> clazz) throws Exception {
		
		if(!Arrays.asList(clazz.getInterfaces()).contains(Service.class)) {
			throw new Exception("Implémente pas l'interface");
		}
		
		if(Modifier.isAbstract(clazz.getModifiers())) {
			throw new Exception("Est abstraite");
		}
		
		if(!Modifier.isPublic(clazz.getModifiers())) {
			throw new Exception("N'est pas publique");
		}
		
		try {
			Constructor<?> c = clazz.getConstructor(Socket.class);
			if(c.getExceptionTypes().length > 0) {
				throw new Exception("Mauvais constructor");
			}
		} catch (NoSuchMethodException | SecurityException e) {
			throw new Exception("Mauvais constructor");
		}
		
		if(Arrays.asList(clazz.getDeclaredFields())
				.stream()
				.filter(field -> Modifier.isFinal(field.getModifiers()) && Modifier.isPrivate(field.getModifiers()))
				.map(field -> field.getType())
				.filter(field -> field == Socket.class)
				.count() == 0) {
			throw new Exception("Pas de variable final socket private");
		}
		
		try  {
			Method m = clazz.getMethod("toStringue");
			if(!Modifier.isStatic(m.getModifiers())) {
				throw new Exception("toStringue pas statique");
			}
			
			if(m.getExceptionTypes().length > 0) {
				throw new Exception("toStringue ne doit pas soulever d'execption");
			}
			
			if(m.getReturnType() != String.class) {
				throw new Exception("toStringue doit retourner un string");
			}
		} catch(NoSuchMethodException e) {
			throw new Exception("Manque la fonction toStringue");
		}
		
		return true;
	}
	
}
