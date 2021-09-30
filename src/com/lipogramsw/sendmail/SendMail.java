package com.lipogramsw.sendmail;

import java.io.File;
import java.nio.file.Paths;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.lipogramsw.exfilter.LogHandler;

public class SendMail {

	
	private String server = "";
	private String port = "";
	private String user = "";
	private String passwd = "";
	private String mailFrom = "";
	private String[] mailTo = new String[0];
	private String subject = "";
	private String body = "";
	private boolean starttls = false;
	private boolean ssl = false;
	private boolean useDebug = false;
	
	private String[] fileAttach = new String[0];
	
	
	public boolean send()
	{
		LogHandler.getInstance().writeLog("Sending message...");

		checkServer();
		
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", this.server);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", this.port);
		
		// If option --mstarttls is provided, enable StartTLS
	    props.put("mail.smtp.starttls.enable", this.starttls);
	    
	    // If option --mssl is provided, enable SSL
	    if (this.ssl) 
	    {
	    	props.put("mail.smtp.socketFactory.port", this.port);
	    	props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	    }
		
	    // Set authentication user and password
	    Session session;
	    if (this.user == null || this.user.trim().isEmpty())
	    {
	    	// If no user was provided, try to authenticate with blanks. 
	    	// Works fine for TR Message Relay Servers. 
	    	session = Session.getInstance(props, new javax.mail.Authenticator() { 
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {  
				return new PasswordAuthentication("","");  
				}  
			});
	    }
	    else
	    {
	    	// Any other server that asks for authentication will need the user and pass. 
	    	// Authenticates as usual. 
			session = Session.getInstance(props, new javax.mail.Authenticator() { 
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {  
				return new PasswordAuthentication(user, passwd);  
				}  
			});
	    }
			
	    // Uses (or not) SMTP debug. See parameter --mdebug 
		session.setDebug(useDebug); 
		
		try {
			
			// Create a MIME message, sets 'from' and 'subject'
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(this.mailFrom));
			message.setSubject(this.subject);

			// System accepts a list of --mto options; sets all of them
			// as TO field (each address need a --mto option)
			for (String toDest : this.mailTo)
			{
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toDest));
			}
			
			// First message part: body
			BodyPart messageBodyText = new MimeBodyPart();
			messageBodyText.setText(this.body);
			Multipart multipart = new MimeMultipart();  
		    multipart.addBodyPart(messageBodyText);  
			
			// Second message part: attachments (if any)
		    if (this.fileAttach.length > 0)
			{
		    	// Handle multiple files
		    	for (String attachFileName : this.fileAttach)
		    	{
		    		LogHandler.getInstance().writeLog("Attaching file '" + attachFileName + "'");
		    		File fAttach = new File(attachFileName);
		    		if (fAttach.isFile())
		    		{
						// DataHandler will ensure the correct choice between data type. 
						// No need to check if it is plain text or binary; also, it will
						// automatically encode binary as BASE64.
						MimeBodyPart messageAttachment = new MimeBodyPart();  
						String filename = attachFileName; 
						DataSource source = new FileDataSource(filename);  
						messageAttachment.setDataHandler(new DataHandler(source));  
						messageAttachment.setFileName(Paths.get(filename).getFileName().toString());  
						multipart.addBodyPart(messageAttachment);
		    		}
		    		else
		    		{
			    		LogHandler.getInstance().writeLog("WARNING: file '" + attachFileName + "' does not exist. Skipping.");
		    		}
		    	}
			}
			
		    // Set the Multipart object as the whole message (embedding body and attachments)  
		    message.setContent(multipart);  
		     
		    // Send message using Javax Mail Transport system 
		    Transport.send(message);  
		}
		catch (MessagingException e)
		{
			// In case of errors, show message log and leave
			LogHandler.getInstance().writeLog("ERROR: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public void setSmtpDebug(boolean setDebug)
	{
		if (setDebug) LogHandler.getInstance().writeLog("WARNING: SMTP Debug messages will be shown.");
		this.useDebug = setDebug;
	}
	
	private void checkServer()
	{
		if (this.server == null || this.server.trim().isEmpty())
		{
    		LogHandler.getInstance().writeLog("ABORTING: MAIL SERVER must be specified!");
    		System.exit(4);
		}
	}
	
	public void setFileAttach(String[] fileName)
	{
		// TODO: Add file exists check. 
		this.fileAttach = fileName;
	}
	
	public void setServer(String server) {
		this.server = server;
		checkServer();
	}
	
	public void setPort(String port) {
		this.port = port;
	}
	
	public void setStartTLS(boolean starttls)
	{
		// Default is FALSE 
		this.starttls = starttls;
	}
	
	public void setSSL(boolean ssl)
	{
		// Default is FALSE
		this.ssl = ssl;
	}
	
	public void setUser(String user) {
		// If null or empty, will ignore password as well
		this.user = user;
	}
	
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}
	
	public void setMailTo(String[] mailTo) {
		this.mailTo = mailTo;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	
	
}
