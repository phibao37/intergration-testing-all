package core;

import java.io.File;

/**
 * Các cài đặt ứng dụng
 */
public class Config {

	public static File DIR_TEMP = new File("local/temp").getAbsoluteFile();
	
	public static File DIR_GCC = new File(
			"C:/Program Files (x86)/Dev-Cpp/MinGW64/bin");
	
	
	/*---------------------------------------------------------------------*/
	
	private static void setupInit(){
		DIR_TEMP.mkdirs();
	}
	
	static {
		setupInit();
	}
}
