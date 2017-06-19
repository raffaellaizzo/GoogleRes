package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;

public class Test {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {


		int rowCount = 0;
		File f = new File("C:\\Users\\r.izzo\\Desktop\\MAIL", "mail.xls");
		FileOutputStream out = null;
		
		try {
			
			
			Set<String> lista = new HashSet<String>();
			lista.add("pippo");
			lista.add("pluto");


			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Mail");
			sheet.setDefaultRowHeightInPoints((short)16);
			HSSFFont font = workbook.createFont();
			font.setFontHeightInPoints((short)11); // dimensione
			font.setColor(HSSFColor.BLACK.index); // testo nero
			font.setBoldweight(Font.BOLDWEIGHT_BOLD); // bold
			font.setFontName("Calibri");

			for (String mail : lista) {

				try {
					
					Row row = sheet.createRow(rowCount);
					row.setHeightInPoints((short)24);
					Cell cell = row.createCell(0);	
					int mailSize = mail.length();			

					HSSFRichTextString richString = new HSSFRichTextString(mail);
					richString.applyFont( 0, mailSize, font );
					cell.setCellValue( richString );

					// STAMPA L'EXCEL SU FILE

					out = new FileOutputStream(f);
					workbook.write(out);
					
					rowCount++; 

				}catch (Exception e) {
					continue;
				}

			}
		}

		finally {
			try { out.close(); } catch (Exception e) { }
		}
	}

}
