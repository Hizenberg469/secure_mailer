package com.secure_mailer.backend.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.InternetAddress;
import com.secure_mailer.backend.EmailAccount;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.crypto.SecretKey;
import java.util.Base64;

import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;

public class EmailSendingService extends Service<Void> {

	private EmailAccount emailAccount;
	private String subject;
	private String recipient;
	private String content; 
	private List<File> attachments = new ArrayList<File>();
	//private SecretKey sKey;
	
	class SecretKeyToString {
	    public static String secretKeyToString(SecretKey secretKey) {
	        // Convert SecretKey to byte array and then to Base64 string
	        byte[] encodedKey = secretKey.getEncoded();
	        return Base64.getEncoder().encodeToString(encodedKey);
	    }
	}
	
	public EmailSendingService(EmailAccount emailAccount, String subject, String recipient, String content, List<File> list
			,SecretKey sKey
			) {
		this.emailAccount = emailAccount;
		this.subject = subject;
		this.recipient = recipient;
		this.content = content;
		this.attachments = list;
		//this.sKey = sKey;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					
//					System.out.println("From email id : "+emailAccount.getAddress());
					MimeMessage mimeMessage = new MimeMessage(emailAccount.getSession());
					mimeMessage.setFrom(new InternetAddress(emailAccount.getAddress()+"@peachy.in.net"));
					mimeMessage.addRecipients(Message.RecipientType.TO, recipient);
					mimeMessage.setSubject(subject);
					
					//Embedding Secret Key.
//					MimeBodyPart customDataPart = new MimeBodyPart();
//				    customDataPart.setText(SecretKeyToString.secretKeyToString(sKey), "UTF-8");
//				    customDataPart.setHeader("Secret-Key", "<custom-data>");
					
					Multipart multipart = new MimeMultipart();
					BodyPart messageBodyPart = new MimeBodyPart();	
					messageBodyPart.setContent(content, "text/html");
					multipart.addBodyPart(messageBodyPart);
					
					
					//Adding Secret Key to message.
					//multipart.addBodyPart(customDataPart);
					
					mimeMessage.setContent(multipart);
					
					if(attachments.size()>0) {
						for (File file: attachments) {
							MimeBodyPart mimeBodyPart = new MimeBodyPart();
							DataSource source = new FileDataSource(file.getAbsolutePath());
							mimeBodyPart.setDataHandler(new DataHandler(source));
							mimeBodyPart.setFileName(file.getName());
							multipart.addBodyPart(mimeBodyPart);
						}
					}
					
					Transport transport = emailAccount.getSession().getTransport();
					transport.connect(
							emailAccount.getProperties().getProperty("outgoingHost"),
							emailAccount.getAddress(),
							emailAccount.getPassword());
					transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
					transport.close();
					
					storeInSentFolder("peachy.in.net", emailAccount.getAddress(), emailAccount.getPassword(), mimeMessage);
					
				} catch(MessagingException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} return null; 
			}
		};
	}
	
	
    private static void storeInSentFolder(String imapHost, String username, String password, MimeMessage message) {
        // IMAP server properties
        Properties imapProps = new Properties();
        imapProps.put("mail.store.protocol", "imaps");
        imapProps.put("mail.imap.host", imapHost);
        imapProps.put("mail.imap.port", "993");
        imapProps.put("mail.imap.ssl.enable", "true");  // Use SSL for IMAP
        
        imapProps.setProperty("mail.mime.encodeparameters", "true");
        //  properties.setProperty("mail.mime.decodeparameters","true");
        imapProps.setProperty("mail.mime.encodefilename", "true");
        // properties.setProperty("mail.mime.decodefilename","true");

        try {
            // Connect to the IMAP store
            Session session = Session.getDefaultInstance(imapProps);
            Store store = session.getStore("imaps");
            store.connect(username, password);

            // Open the "Sent" folder
            Folder sentFolder = store.getFolder("Sent");  // Adjust based on provider's folder name
            if (!sentFolder.exists()) {
                sentFolder.create(Folder.HOLDS_MESSAGES);
            }
            sentFolder.open(Folder.READ_WRITE);

            try {

                sentFolder.appendMessages(new Message[]{message});
               // Message[] msgs = folder.getMessages();
                //message.setFlag(FLAGS.Flag.RECENT, true);
                System.out.println("Msg send and saved ....");

            } catch (Exception ignore) {
                System.out.println("error processing message " + ignore.getMessage());
            } finally {
                store.close();
               // folder.close(false);
            }
            
            // Close the folder and store connection
            sentFolder.close(false);
            store.close();

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
	
}