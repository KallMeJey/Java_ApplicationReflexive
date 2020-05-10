package guillaume.mail;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailBuilder {

	private String fromEmail;

	private String toEmail;

	private String smtpHost;
	private String smtpUsername;
	private String smtpPassword;
	private Integer smtpPort = 587;

	private String subject;
	private String content;

	/**
	 * Spécifier l'email de l'expéditeur
	 * @param fromEmail
	 * @return
	 */
	public MailBuilder setFrom(String fromEmail) {
		assert (fromEmail.length() != 0);
		this.fromEmail = fromEmail;

		return this;
	}

	/**
	 * Spécifier l'email du destinataire
	 * @param toEmail
	 * @return
	 */
	public MailBuilder setTo(String toEmail) {
		assert (toEmail.length() != 0);
		this.toEmail = toEmail;

		return this;
	}

	/**
	 * Spécifier le serveur SMTP utilisé
	 * @param host
	 * @param port
	 * @return
	 */
	public MailBuilder setHost(String host, Integer port) {
		assert (toEmail.length() != 0);
		this.smtpHost = host;
		this.smtpPort = port;

		return this;
	}

	/**
	 * Connexion au compte mail qui servira a authentifier l'email
	 * @param username
	 * @param password
	 * @return
	 */
	public MailBuilder setAuth(String username, String password) {
		assert (username.length() != 0);
		assert (password.length() != 0);
		this.smtpUsername = username;
		this.smtpPassword = password;

		return this;
	}

	/**
	 * Définir le sujet de l'email
	 * @param subject
	 * @return
	 */
	public MailBuilder setSubject(String subject) {
		assert (subject.length() != 0);
		this.subject = subject;
		return this;
	}

	/**
	 * Définir le contenu de l'email
	 * @param content
	 * @return
	 */
	public MailBuilder setContent(String content) {
		assert (content.length() != 0);
		this.content = content;
		return this;
	}

	/**
	 * Envoyer l'email
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public void send() throws UnsupportedEncodingException, MessagingException {
		Properties prop = new Properties();
		prop.put("mail.smtp.host", this.smtpHost);
		prop.put("mail.smtp.port", this.smtpPort);
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");

		final String username = this.smtpUsername;
		final String password = this.smtpPassword;

		Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(this.fromEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(this.toEmail));
			message.setSubject(this.subject);
			message.setText(this.content);

			Transport.send(message);

			System.out.println("Mail Sent !");

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
