package test.poi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class TestPoi {

	public static void main(String[] args) {
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Project");
		
		XSSFRow r0 = sheet.createRow(0);
		r0.createCell(0).setCellValue(123);
		r0.createCell(1).setCellValue(true);
		
	    try {
			FileOutputStream out = 
		            new FileOutputStream(new File("D:\\Downloads\\test.xlsx"));
			workbook.write(out);
		    out.close();
		    workbook.close();
		    System.out.println("Excel written successfully..");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
