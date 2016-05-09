package core.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
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
	private XSSFWorkbook workbook;
	private File out;
	
	public ExcelExporter(IProject project) {
		this.project = project;
	}
	
	@Override
	public IProject getProject() {
		return project;
	}
	
	protected XSSFWorkbook getWorkbook(){
		if (workbook == null){
			workbook = new XSSFWorkbook();
			String d = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss")
					.format(new Date());
			out = new File(Config.DIR_EXPORT, getProject().getRoot()
					.getName() + "#" + d + ".xlsx");
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
		
		XSSFSheet sheet = workbook.createSheet(tested.getSourceInfo()
				.getFile().getName() + "#" + tested.getName());
		String[] COVER_NAMES = {"Statement coverage", "Branch coverage",
				"Sub condition coverage", "All path", "Error path", "Loop path"};
		String[] colsName = {"Id", "Testpath", "Argument", "Return", "Expected", 
				"Pass", "Solver"};
		int[] colsWidth = {4, 40, 30, 15, 15, 10, 10};
		int row = 0;
		
		for (int c = 0; c < COVER_NAMES.length; c++){
			{
				XSSFRow r = sheet.createRow(row);
				XSSFCell cell = r.createCell(0);
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
				
				XSSFRow r = sheet.createRow(row);
				r.createCell(0).setCellValue(name);
				sheet.addMergedRegion(
						new CellRangeAddress(row, row, 0, 1));
				
				r.createCell(2).setCellValue(value);
				row++;
			}
			
			{
				XSSFRow r = sheet.createRow(row++);
				for (int i = 0; i < colsName.length; i++){
					XSSFCell cell = r.createCell(i);
					cell.setCellValue(colsName[i]);
					cell.setCellStyle(STYLE[BOLD|TOP|DOWN]);
				}
			}
			
			List<ITestpath> paths = result.getTestpaths(c);
			int i = 0;
			for (ITestpath tp: paths){
				XSSFRow r = sheet.createRow(row++);
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
		XSSFCellStyle style = getWorkbook().createCellStyle();
		
		if (bold){
			XSSFFont f = workbook.createFont();
			f.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
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
