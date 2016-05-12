package core.solver.z3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.Config;

/**
 * Lớp tạo đối tượng tương tác với bộ giải hệ điều kiện Z3 (sử dụng ngôn ngữ SMT2)
 */
public class Z3 {
	
	public static void main(String[] args) throws Exception{
		Z3 z3 = new Z3()
			.addLine("declare-fun a (Int Int) Int")
			.addLine("declare-fun b (Int Int) Int")
			.addLine("declare-fun c () Int")
			//.addLine("assert (> a 10)")
			//.addLine("assert (< a 13)")
			.addLine("assert (= 4 (a 2 2))")
			.addLine("assert (= (b 2 2) 1)")
			.addLine("assert (> c 4)")
			.addLine("check-sat")
			.addLine("get-model")
			.execute();
		
		System.out.println(z3.getLine());
		System.out.println(z3.getLine());
		while (z3.hasFunction())
			System.out.println("Function: " + z3.getFunction());
		System.out.println(z3.getLine());
	}
	
	/**
	 * Kiểm tra một chuỗi có các cặp dấu ngoặc ( và ) tương ứng với nhau
	 * @param lines chuỗi kiểm tra
	 * @return số lượng dấu ( và ) bằng nhau 
	 * @throws IllegalArgumentException xuất hiện dấu ) trước dấu (, thí dụ ())(
	 */
	private boolean checkBracket(String lines) throws IllegalArgumentException{
		int count = 0;
		
		for (char c: lines.toCharArray()){
			if (c == '(')
				count++;
			else if (c == ')')
				count--;
			if (count < 0)
				throw new IllegalArgumentException(lines);
		}
		return count == 0;
	}
	
	
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
	 * Kiểm tra còn có dòng nào trong output hay không
	 */
	public boolean hasLine(){
		return !outBlock.isEmpty();
	}
	
	/**
	 * Kiểm tra còn có khai báo hàm (define-func) nào trong output hay không 
	 */
	public boolean hasFunction(){
		if (mPickFunction == null)
			try{
				mPickFunction = pickFunction(false);
			}catch (IllegalArgumentException e) {}
		return mPickFunction != null;
	}
	
	/**
	 * Trả về khai báo hàm tiếp theo trong output, hoặc null nếu không có
	 */
	public Func getFunction(){
		if (mPickFunction == null)
			try{
				return pickFunction(true);
			}catch (IllegalArgumentException e) {
				return null;
			}
		else {
			Func pick = mPickFunction;
			mPickFunction = null;
			while (mPickCount > 0){
				outBlock.remove();
				mPickCount--;
			}
			return pick;
		}
	}
	
	/**
	 * Thêm một khai báo hàm vào stack
	 * @param func hàm khai báo (dạng declare-fun nếu chưa có giá trị, và define-fun 
	 * nếu đã có giá trị)
	 * @return đối tượng hiện hành (this)
	 */
	public Z3 addFunction(Func func){
		return addLine(func.toString());
	}
	
	private Func mPickFunction;
	private int mPickCount;
	private static Pattern PICK_FUNC = Pattern.compile(
			"\\(define-fun (\\w+) \\(((?> ?\\(x\\!\\d+ \\w+\\))*)\\) (\\w+) (.+)\\)");
	
	/**
	 * Lấy ra một khai báo hàm
	 * @param remove loại bỏ khỏi output hay không
	 * @return hàm được khai báo
	 * @throws IllegalArgumentException không đúng định dạng hàm hoặc đã hết output
	 */
	private Func pickFunction(boolean remove) throws IllegalArgumentException{
		String lines = "";
		mPickCount = 0;
		
		try {
			do {
				lines += " " + outBlock.get(mPickCount++).trim();
			} while (!checkBracket(lines));
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException(lines);
		}
		lines = lines.substring(1);
		
		Matcher m = PICK_FUNC.matcher(lines);
		if (!m.matches())
			throw new IllegalArgumentException(lines);
		
		Func func = new Func(m.group(1), m.group(3)).setValue(m.group(4));
		String paras = m.group(2);
		
		if (!paras.isEmpty()){
			for (String para: paras.split(" "))
				func.addParameter(para);
		}
		
		if (remove)
		while (mPickCount > 0){
			outBlock.remove();
			mPickCount--;
		}
		return func;
	}
	
	/**
	 * Xây dựng input và thực thi với bộ giải hệ. Đầu ra (output) có thể được lấy sau đó
	 * thông qua {@link #getLine()} hoặc {@link #getLines()}
	 * @return đối tượng hiện thời (this)
	 * @throws IOException thư mục Z3 không tồn tại
	 * @throws InterruptedException 
	 */
	public Z3 execute() throws IOException, InterruptedException{
		try{
			File BIN_DIR = Config.DIR_Z3_BIN;
			if (!BIN_DIR.isDirectory())
				throw new IOException(BIN_DIR + " is not a directory");
			
			if (!Config.DIR_TEMP.canWrite())
				throw new IOException("Can't write to " + Config.DIR_TEMP);
			
			File input = new File(Config.DIR_TEMP, INPUT);
			File z3 = new File(BIN_DIR, Z3);
				
			FileOutputStream fout = new FileOutputStream(input);
			
			if (raw == null) 
				raw = "";
			else
				raw += "\n";
			
			for (String line : inBlock)
				raw += "(" + line + ")\n";
			
			fout.write(raw.getBytes());
			
			//System.out.println("Raw = " + raw);
			raw = null;
			inBlock.clear();
			outBlock.clear();
			fout.close();
			
			Process p = null;
			try{
				p = Runtime.getRuntime().exec(
						String.format("\"%s\" -smt2 \"%s\"", z3, input));
			} catch (IOException e){
				throw new RuntimeException(e.getMessage());
			}
			p.waitFor();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			
			while ((line = in.readLine()) != null) 
				outBlock.add(line);
			
		} catch (IOException e){
			e.printStackTrace();
		}
		return this;
	}
	
	/**
	 * Kiểu hàm trong chương trình z3
	 */
	public static class Func{
		
		private String mType, mName, mValue;
		private ArrayList<String> mPara;
		
		/**
		 * Tạo một hàm mới với tên và kiểu
		 * @param name tên của hàm
		 * @param type kiểu trả về của hàm
		 */
		public Func(String name, String type){
			mName = name;
			mType = type;
			mPara = new ArrayList<String>();
		}
		
		/**
		 * Thêm tham số cho hàm
		 * @param para tham số, bao gồm cả cặp () nếu đang tạo hàm (define-func)
		 * @return hàm đang được thêm tham số
		 */
		public Func addParameter(String para){
			mPara.add(para);
			return this;
		}
		
		/**
		 * Đặt giá trị trả về cho hàm
		 * @param value giá trị mới
		 * @return hàm đang được đặt giá trị
		 */
		public Func setValue(String value){
			mValue = value;
			return this;
		}
		
		private static HashMap<String, String> typeMap = new HashMap<>();
		
		static{
			typeMap.put("Int", "0");
			typeMap.put("Real", "0.0");
			typeMap.put("Bool", "false");
		}
		
		/**
		 * Gán giá trị trả về cho hàm từ kiểu trả về của nó
		 * @return hàm đang được đặt giá trị
		 */
		public Func setValueFromType(){
			setValue(typeMap.get(getType()));
			return this;
		}
		
		/**
		 * Trả về tên của khai báo hàm
		 */
		public String getName(){
			return mName;
		}
		
		/**
		 * Trả về kiểu giá trị trả về của hàm
		 */
		public String getType(){
			return mType;
		}
		
		/**
		 * Trả về giá trị dạng chuỗi của hàm
		 */
		public String getValue(){
			return mValue;
		}

		@Override
		public String toString() {
			String s_para = "";
			
			if (!mPara.isEmpty()){
				s_para = mPara.get(0);
				for (int i = 1; i < mPara.size(); i++)
					s_para += " " + mPara.get(i);
			}
			
			if (mValue == null)
				return String.format("declare-fun %s (%s) %s", mName, s_para, mType);
			else
				return String.format("define-fun %s (%s) %s %s", 
						mName, s_para, mType, mValue);
		}

		@Override
		public int hashCode() {
			return toString().hashCode();
		}
		
	}

}
