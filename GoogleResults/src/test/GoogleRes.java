package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.xml.XmlPage;

public class GoogleRes {


	static {
		LogHelper.setRootloggerName("test");
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws FailingHttpStatusCodeException 
	 */
	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {


		Logger logger = Logger.getLogger("F1");
		//		Set<String> lista = new HashSet<String>();
		List<BeanRes> lista = null;
		Boolean result = true;
		String next_page = "";
		String urlNew = "";

		// TODO Auto-generated method stub
		WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);				
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setCssEnabled(false);

		//		ProxyConfig proxyConfig = new ProxyConfig();
		//		proxyConfig.setProxyHost("10.55.32.23");
		//		proxyConfig.setProxyPort(80);
		//		webClient.getOptions().setProxyConfig(proxyConfig);

		logger.info("Inizio...");

		String citta = "Matera";
		String key = "AIzaSyD1ezQ62XUDXri4hvQnuUElCThtpcSypPY";
//		String key = "AIzaSyCJHdNtJE3fSBe0QgfC8-ho6swNlWJ0ciw";
		String url = "https://maps.googleapis.com/maps/api/place/textsearch/xml?query=restaurants+in+"+citta+"&key="+key;
		int rowCount = 0;
		File f = new File("C:\\Users\\r.izzo\\Desktop\\MAIL", "mail_"+citta+".xls");
		FileOutputStream out = null;

		try {

			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Mail");
			sheet.setDefaultRowHeightInPoints((short)16);
			HSSFFont font = workbook.createFont();
			font.setFontHeightInPoints((short)11); // dimensione
			font.setColor(HSSFColor.BLACK.index); // testo nero
			font.setBoldweight(Font.BOLDWEIGHT_BOLD); // bold
			font.setFontName("Calibri");

			while(result){

				urlNew = url+"&pagetoken="+ next_page;
				System.out.println("url: "+urlNew);

				XmlPage page = webClient.getPage(urlNew);
				logger.info(urlNew);
				//		System.out.println(page.asXml());

				DomNodeList<DomElement> ids = page.getElementsByTagName("place_id");	
				lista = new ArrayList<BeanRes>();


				for (DomElement id : ids) {

					try {

						String url2 = "https://maps.googleapis.com/maps/api/place/details/xml?placeid="+id.asText()+"&key="+key+"&pagetoken="+ next_page;
						logger.info(url2);
						XmlPage page2 = webClient.getPage(url2);
						System.out.println("url2: "+url2);

						DomNodeList<DomElement> id2s = page2.getElementsByTagName("website");	
						String name = page2.getElementsByTagName("name").get(0).getFirstChild().toString();
						String mail = "";

						BeanRes bean = new BeanRes(mail,name,""); 
						
						for (DomElement id2 : id2s) {
							String url3 = id2.getFirstChild().toString();
							logger.info(url3);
							HtmlPage page3 = webClient.getPage(url3);
							System.out.println("url3: " + url3);
							//	    HtmlPage page3 = webClient.getPage("http://www.lanticatrattoria.com/");

							List<HtmlAnchor> anchors = page3.getAnchors();
							for (HtmlAnchor htmlAnchor : anchors) {
								if (htmlAnchor.getHrefAttribute().startsWith("mailto:"))
									mail= htmlAnchor.getHrefAttribute().substring(7);
							}														

							System.out.println(!mail.trim().equals("") ? "Tovata: " + mail: "Non Trovata");
//							BeanRes bean = new BeanRes(mail,name,url3); 
//							lista.add(bean);
							bean.setWebsite(url3);
							bean.setMail(mail);

						}

						lista.add(bean);
					}
					catch (Exception e) {
						logger.error(e);
						continue;
					}
				}

				DomNodeList<DomElement> next_page_token= page.getElementsByTagName("next_page_token");
				if(next_page_token.size()!=0)
					next_page = next_page_token.get(0).getFirstChild().toString();
				else{

					result= false;

				}





				for (BeanRes res : lista) {

					String mail = res.getMail();
					try {

						Row row = sheet.createRow(rowCount);
						row.setHeightInPoints((short)24);

						Cell cell = row.createCell(0);	
						int nameSize =res.getDescrizione().length();			

						HSSFRichTextString richName = new HSSFRichTextString(res.getDescrizione());
						richName.applyFont( 0, nameSize, font );
						cell.setCellValue( richName);


						Cell cell1 = row.createCell(1);	
						int mailSize =mail.length();			

						HSSFRichTextString richMail = new HSSFRichTextString(mail);
						richMail.applyFont( 0, mailSize, font );
						cell1.setCellValue( richMail );


						Cell cell2 = row.createCell(2);	
						int webSize =res.getWebsite().length();			

						HSSFRichTextString richWeb = new HSSFRichTextString(res.getWebsite());
						richWeb.applyFont( 0, webSize, font );
						cell2.setCellValue( richWeb );


						// STAMPA L'EXCEL SU FILE

						out = new FileOutputStream(f);
						workbook.write(out);

						rowCount++; 

					}catch (Exception e) {
						logger.error(e);
						continue;
					}

				}
			}


		}


		finally {
			System.out.println("Fine...");
			LogHelper.info("Fine...");
			try { out.close(); } catch (Exception e) { }
		}
	}

}
