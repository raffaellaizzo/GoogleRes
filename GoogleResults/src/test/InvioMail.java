package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.mail.Authenticator;
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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;




public class InvioMail {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {


		File f = new File("C:\\Users\\r.izzo\\Desktop\\MAIL", "mail_Matera.xls");
		FileOutputStream out = null;

		try {
			
		  POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(f));
	      HSSFWorkbook wb = new HSSFWorkbook(fs);
	      HSSFSheet sheet = wb.getSheetAt(0);
	      for(int row_index = 0; row_index< sheet.getPhysicalNumberOfRows(); row_index++){
	      HSSFRow row = sheet.getRow(row_index);
	      HSSFCell cell_esempio = row.getCell(1); //indice della colonna
	      String to = cell_esempio.getStringCellValue(); 
	      System.out.println(to);
	     
		

			
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");


			String from = "raffaella.izzo@gmail.com";


//			Session ses = Session.getInstance(sessionProps);
			
			Authenticator auth = new SMTPAuthenticator();
	        Session ses = Session.getDefaultInstance(props, auth);
			
			MimeMessage message = new MimeMessage(ses);

			message.setFrom(new InternetAddress(from));

			message.setRecipients(Message.RecipientType.TO, to);
			message.setSubject("oggetto");

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent("testo", "text/html");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			
			
			
			message.setContent(multipart);
			Transport.send(message);
	      }
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				// TODO: handle exception
			}
			
		finally {
			try { out.close(); } catch (Exception e) { }
		}
	}
	
}
  class SMTPAuthenticator extends javax.mail.Authenticator {
    public PasswordAuthentication getPasswordAuthentication() {
       String username = "raffaella.izzo@gmail.com";
       String password = "Gabriella1";
       return new PasswordAuthentication(username, password);
    }
}
