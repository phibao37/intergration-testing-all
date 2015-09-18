package cdt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import core.S;
import core.Utils;
import core.error.ProcessErrorException;

/**
 * Lớp giúp tạo đối tượng thao tác với bộ biên dịch GCC
 */
public class GCC {

	public static void main(String[] args) {
		try {
			String result = new GCC()
			.addLine("int min(int a[], int n){int m = a[0], i; for (i = 1; i < n; i++)")
			.addLine("if (a[i] < m) m = a[i]; return m;}")
			.addLine("int arr[] = {1, 4, -4, 3, 5};")
			.execute("printf(\"%d\",min(arr, 5));");
			
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	StringBuilder build;
	
	/**
	 * Tạo một bộ thao tác GCC mới
	 */
	public GCC(){
		build = new StringBuilder();
		include("<stdio.h>");
	}
	
	/**
	 * Thêm 1 include vào mã nguồn
	 * @param include nội dung include, tính cả cặp <> hoặc ""
	 * @return đối tượng hiện thời
	 */
	public GCC include(String include){
		build.append(String.format("#include %s\n", include));
		return this;
	}
	
	/**
	 * Thêm 1 nội dung mới vào mã nguồn, sau đó sang dòng mới
	 * @param content nội dung cần thêm, có thể có kí tự xuống dòng
	 * @return đối tượng hiện thời
	 */
	public GCC addLine(String content){
		build.append(content);
		build.append('\n');
		return this;
	}
	
	/**
	 * Chạy một biểu thức và trả về kết quả dạng chuỗi
	 * @param expression nội dung biểu thức
	 * @return chuỗi kết quả
	 * @throws IOException lỗi tương tác tập tin
	 * @throws InterruptedException 
	 * @throws ProcessErrorException 
	 */
	public String execute(String... expression)
			throws NullPointerException, IOException, 
			ProcessErrorException, InterruptedException{
		
		build.append("int main(){");
		for (String ex: expression)
			build.append(ex);
		build.append("return 0;}");
		
		File input = new File(S.DIR_TEMP, "input.c");
		File output = new File(S.DIR_TEMP, "input.exe");
		File gcc = new File(S.DIR_GCC, "gcc.exe");
		PrintStream out = new PrintStream(new FileOutputStream(input));
		
		out.print(build.toString());
		out.close();
		
		Utils.runCommand(gcc, null, S.DIR_GCC, "-o", output, input);
		return Utils.runCommand(output, null, S.DIR_GCC);
	}
	
}
