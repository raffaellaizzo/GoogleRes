package it.free;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.activation.MimetypesFileTypeMap;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.sun.mail.smtp.SMTPMessage;

public class InvioMail {

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		String path = "C:\\Users\\Rocco\\Desktop\\Lavoro\\Food estero\\IEFOOD\\Email";
		InvioMail sender = new InvioMail();
		MimeMessage message = null;
		// File file = new File(path, "mail_Matera.xls");
		// FileOutputStream out = null;
		try {

			message = sender.configureEmail();

			sender.createMessage(path, sender, message);
			

			//message.setRecipients(Message.RecipientType.TO, "figliuolor@gmail.com");
			//Transport.send(message);
			File contactsFile = new File(path+"\\Contatti"+"\\mail_Monaco.xls");
			sender.sendEmail(message, contactsFile);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		/*
		 * finally { try { out.close(); } catch (Exception e) { } }
		 */
	}

	private void sendEmail(MimeMessage message, File file) throws IOException, FileNotFoundException, MessagingException {
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);
		for (int row_index = 0; row_index < sheet.getPhysicalNumberOfRows(); row_index++) {
			HSSFRow row = sheet.getRow(row_index);
			HSSFCell emailCell = row.getCell(1); // indice della colonna
			String emailValue = emailCell.getStringCellValue();
			HSSFCell restaurantCell = row.getCell(0); // indice della colonna
			String restaurantValue = restaurantCell.getStringCellValue();
			
			if(emailValue.equals("") || !emailValue.contains("@")) {
				System.out.println("Email non Inviata a "+restaurantValue +" indirizzo email: "+emailValue);
				continue;
			}
			System.out.println("Email Inviata a "+restaurantValue +" indirizzo email: "+emailValue);
			message.setRecipients(Message.RecipientType.TO, emailValue);
			try {
				Transport.send(message);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		

		}
	}

	private void createMessage(String path, InvioMail sender, MimeMessage message) throws MessagingException {
		message.setSubject("Delitaly: Special Offers!");

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		MimetypesFileTypeMap mimeType = new MimetypesFileTypeMap();
		mimeType.addMimeTypes("image/png png PNG");
		mimeType.addMimeTypes("image/jpeg jpeg JPEG");
		mimeType.addMimeTypes("text/html html HTML");
		mimeType.addMimeTypes("image/jpg jpg JPG");
		// HTML mail content
		// Set key values
		Map<String, String> input = new HashMap<String, String>();
		input.put("Author", "Rocco Figliuolo");
		input.put("Topic", "HTML Template for Email");
		input.put("Content In", "English");
		String htmlText = sender.readEmailFromHtml(path + "\\email.html", input);
		messageBodyPart.setContent(htmlText, "text/html");
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		
		// ContentID is used by both parts
	    String cid [] = new String [] {"salsiccia.png","box_scatolo.png","Logo-Delitaly-btstudio-colore.png","Vino.png"};//ContentIdGenerator.getContentId();
		for (int i = 0; i < cid.length; i++) {
			  multipart = addImage(path, sender, messageBodyPart, input, cid[i], multipart);			
				message.setContent(multipart);
		}
	  
	}

	private Multipart addImage(String path, InvioMail sender, MimeBodyPart messageBodyPart, Map<String, String> input, 
			String cid, Multipart multipart)
			throws MessagingException {
		
	    // Image part
		MimeBodyPart imageBodyPart = new MimeBodyPart();
	    try {
	    	imageBodyPart.attachFile(path + "\\"+cid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    imageBodyPart.setContentID("<" + cid + ">");
	    imageBodyPart.setDisposition(MimeBodyPart.INLINE);
	    multipart.addBodyPart(imageBodyPart);
		return multipart;
	}
			
	private MimeMessage configureEmail() throws MessagingException, AddressException {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtps.aruba.it");
		props.put("mail.smtp.port", "465");
    	props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.quitwait", "false");
		
		String from = "info@delitaly.eu";//"figliuolor@gmail.com";
		final String username = "info@delitaly.eu";
		final String password = "Picerno_2017";

		Session ses = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		MimeMessage message = new MimeMessage(ses);

		message.setFrom(new InternetAddress(from));
		return message;
	}

	// Method to replace the values for keys
	private String readEmailFromHtml(String filePath, Map<String, String> input) {
		String msg = readContentFromFile(filePath);

		try {
			Set<Entry<String, String>> entries = input.entrySet();
			for (Map.Entry<String, String> entry : entries) {
				msg = msg.replace(entry.getKey().trim(), entry.getValue().trim());
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return msg;
	}

	// Method to read HTML file as a String
	private String readContentFromFile(String fileName) {
		StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;
		try {
			// use buffering, reading one line at a time
			reader = new BufferedReader(new FileReader(fileName));
			String line = null;
			while ((line = reader.readLine()) != null) {
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return contents.toString();
	}
}

//class SMTPAuthenticator extends javax.mail.Authenticator {
//	public PasswordAuthentication getPasswordAuthentication() {
//		String username = "figliuolor@gmail.com";
//		String password = "Aglianico";
//		return new PasswordAuthentication(username, password);
//	}
//}