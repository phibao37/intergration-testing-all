package core.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import api.IProject;
import api.models.IFunction;
import api.models.IFunctionTestResult;
import api.models.ITestpath;
import api.parser.IExporter;
import api.solver.ISolution;
import core.Config;

public class ExcelExporter implements IExporter {

	private IProject project;
	private Workbook workbook;
	private File out;
	
	public ExcelExporter(IProject project) {
		this.project = project;
	}
	
	@Override
	public IProject getProject() {
		return project;
	}
	
	protected Workbook getWorkbook(){
		if (workbook == null){
			
			switch (Config.EXPORT_FORMAT.toLowerCase()){
			case "xls":
				workbook = new HSSFWorkbook();
				break;
			case "xlsx":
				workbook = new XSSFWorkbook();
				break;
			}
			
			String d = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss")
					.format(new Date());
			out = new File(Config.DIR_EXPORT, getProject().getRoot()
					.getName() + "#" + d + "." + Config.EXPORT_FORMAT);
		}
		return workbook;
	}

	@Override
	public void addFunction(IFunction tested) {
		IFunctionTestResult result = tested.getTestResult();
		if (result == null) return;
		
		CellStyle _BOLD = createStyle(true, false, false, false, false);
		CellStyle[] STYLE = new CellStyle[8];
		for (int i = 0; i < 8; i++){
			STYLE[i] = createStyle(has(i, BOLD), 
					has(i, TOP), has(i, DOWN), true, true);
		}
		
		String sheet_name = tested.getSourceInfo()
				.getFile().getName() + "#" + tested.getName();
		Sheet sheet = workbook.getSheet(sheet_name);
		
		if (sheet != null)
			workbook.removeSheetAt(workbook.getSheetIndex(sheet));
		sheet = workbook.createSheet(sheet_name);
		String[] COVER_NAMES = {"Statement coverage", "Branch coverage",
				"Sub condition coverage", "All path", "Error path", "Loop path"};
		String[] colsName = {"Id", "Testpath", "Argument", "Return", "Expected", 
				"Pass", "Solver"};
		int[] colsWidth = {4, 40, 30, 15, 15, 10, 10};
		int row = 0;
		
		for (int c = 0; c < COVER_NAMES.length; c++){
			{
				Row r = sheet.createRow(row);
				Cell cell = r.createCell(0);
				cell.setCellValue(COVER_NAMES[c]);
				cell.setCellStyle(_BOLD);
				sheet.addMergedRegion(
						new CellRangeAddress(row, row, 0, 2));
				row++;
			}
			
			{
				String name = null, value = null;
				switch (c) {
				case IFunctionTestResult.STATEMENT:
				case IFunctionTestResult.BRANCH:
				case IFunctionTestResult.SUBCONDITION:
				case IFunctionTestResult.ALLPATH:
					name = "Coverage";
					value = result.getPercent(c) + "%";
					break;
				case IFunctionTestResult.ERROR:
				case IFunctionTestResult.LOOP:
					name = "Count";
					value = result.getTestpaths(c).size() + "";
					break;
				}
				
				Row r = sheet.createRow(row);
				r.createCell(0).setCellValue(name);
				sheet.addMergedRegion(
						new CellRangeAddress(row, row, 0, 1));
				
				r.createCell(2).setCellValue(value);
				row++;
			}
			
			{
				Row r = sheet.createRow(row++);
				for (int i = 0; i < colsName.length; i++){
					Cell cell = r.createCell(i);
					cell.setCellValue(colsName[i]);
					cell.setCellStyle(STYLE[BOLD|TOP|DOWN]);
				}
			}
			
			List<ITestpath> paths = result.getTestpaths(c);
			int i = 0;
			for (ITestpath tp: paths){
				Row r = sheet.createRow(row++);
				ISolution sln = tp.getSolution();
				
				r.createCell(0).setCellValue(++i);
				r.createCell(1).setCellValue(tp.toString());
				r.createCell(2).setCellValue(sln.getMessage());
				r.createCell(3).setCellValue(toString(sln.getReturnValue()));
				r.createCell(4);
				r.createCell(5);
				r.createCell(6).setCellValue(toString(sln.getSolver()));
				
				for (int j = 0; j <= 6; j++)
					r.getCell(j).setCellStyle(STYLE[
					    i == paths.size() ? DOWN : 0]);
			}
			
			row+=2;
		}
		
		for (int i = 0; i < colsWidth.length; i++)
			sheet.setColumnWidth(i, colsWidth[i]*256);
	}
	
	static String toString(Object o){
		return o == null ? null : o.toString();
	}
	
	static boolean has(int flags, int flag){
		return (flags & flag) != 0;
	}
	
	static int add(int flags, int flag){
		return flags | flag;
	}
	
	static final int BOLD = 1, TOP = 2, DOWN = 4;
	
	protected CellStyle createStyle(boolean bold, boolean top, boolean bottom,
			boolean left, boolean right){
		CellStyle style = getWorkbook().createCellStyle();
		
		if (bold){
			Font f = workbook.createFont();
			f.setBoldweight(Font.BOLDWEIGHT_BOLD);
			f.setBold(true);
			style.setFont(f);
		}
		
		if (top){
			style.setBorderTop(CellStyle.BORDER_THIN);
		}
		
		if (bottom){
			style.setBorderBottom(CellStyle.BORDER_THIN);
		}
		
		if (left){
			style.setBorderLeft(CellStyle.BORDER_THIN);
		}
		
		if (right){
			style.setBorderRight(CellStyle.BORDER_THIN);
		}
		
		return style;
	}

	@Override
	public void export() throws IOException {
		if (workbook == null || workbook.getNumberOfSheets() == 0)
			return;
		
		FileOutputStream fout = 
	            new FileOutputStream(out);
		workbook.write(fout);
	    fout.close();
	}

	@Override
	public void close() {
		if (workbook != null)
			try {
				workbook.close();
			} catch (IOException e) {
			}
	}
	
}
