package core.solver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Lớp tạo đối tượng tương tác với bộ giải hệ điều kiện Z3 (sử dụng ngôn ngữ SMT2)
 */
public class Z3 {
	
	public static void main(String[] args){
		Z3 z3 = new Z3()
			.addLine("declare-const a Int")
			//.addLine("assert (> a 10)")
			//.addLine("assert (< a 13)")
			.addLine("assert (= 4 (- a))")
			.addLine("check-sat")
			.addLine("get-model")
			.execute();
		
		while (z3.hasLine())
			System.out.println(z3.getLine());
	}
	
	/**
	 * Thiết đặt thư mục chứa chương trình ứng dụng (chứa z3.exe)
	 */
	public static void setDirectory(String dir){
		BIN_DIR = new File(dir);
		if (!BIN_DIR.isDirectory())
			throw new IllegalArgumentException(dir + " is not a directory");
	}
	
	/** Thư mục chứa chương trình thực thi z3*/
	private static File BIN_DIR = new File("D:\\App\\Library\\z3\\bin");
	
	/** Tên tập tin để nhập dữ liệu để truyền vào z3*/
	private static final String INPUT = "input.smt2";
	
	/** Tên tập tin thực thi*/
	private static final String Z3 = "z3";
	
	private LinkedList<String> inBlock = new LinkedList<String>();
	private LinkedList<String> outBlock = new LinkedList<String>();
	private String raw;
	
	/**
	 * Thêm 1 dòng input vào stack, không chứa dấu ngoặc ở đầu dòng và cuối dòng
	 * @param line nội dung input cần thêm
	 * @param args các tham số format bổ sung
	 * @return đối tượng hiện thời (this)
	 */
	public Z3 addLine(String line, Object... args){
		inBlock.add(String.format(line, args));
		return this;
	}
	
	/**
	 * Thêm nội dung input mặc định vào stack. Khi xây dựng nội dung input, nội dung
	 * mặc định này sẽ được thêm trước, sau đó mới tới các dòng được thêm bởi 
	 * {@link #addLine(String, Object...)}
	 * @param whole nội dung input mặc định
	 * @return đối tượng hiện thời (this)
	 */
	public Z3 setRaw(String whole){
		raw = whole;
		return this;
	}
	
	/**
	 * In các dòng input trong stack
	 */
	public void printInput(){
		for (String line: inBlock)
			System.out.printf("(%s)\n", line);
		System.out.println();
	}
	
	/**
	 * Lấy ra từng dòng theo thứ tự xuất hiện trong output, hoặc null nếu rỗng 
	 */
	public String getLine(){
		return outBlock.pollFirst();
	}
	
	/**
	 * Lấy ra danh sách toàn bộ các dòng trong output
	 */
	public List<String> getLines(){
		return outBlock;
	}
	
	/**
	 * Kiểm tra còn có dòng nào trong output hay không
	 */
	public boolean hasLine(){
		return !outBlock.isEmpty();
	}
	
	/**
	 * Xây dựng input và thực thi với bộ giải hệ. Đầu ra (output) có thể được lấy sau đó
	 * thông qua {@link #getLine()} hoặc {@link #getLines()}
	 * @return đối tượng hiện thời (this)
	 */
	public Z3 execute(){
		try{
			File input = new File(BIN_DIR, INPUT);
			File z3 = new File(BIN_DIR, Z3);
			FileOutputStream fout = new FileOutputStream(input);
			
			if (raw == null) 
				raw = "";
			else
				raw += "\n";
			
			for (String line : inBlock)
				raw += "(" + line + ")\n";
			
			fout.write(raw.getBytes());
			
			raw = null;
			inBlock.clear();
			outBlock.clear();
			fout.close();
			
			Process p = Runtime.getRuntime().exec(z3 + " -smt2 " + input);
			while (p.isAlive()) {
				Thread.sleep(100);
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			
			while ((line = in.readLine()) != null) 
				outBlock.add(line);
			
		} catch (Exception e){
			e.printStackTrace();
		}
		return this;
	}

}
