package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Properties;


/**
 * Rút gọn của Settings. Lớp tiện ích giúp lưu trữ các cài đặt của ứng dụng.<br/>
 * Các thuộc tính cài đặt cần là một biến thực thể <b>public</b>, <b>static</b> và 
 * <b>non-final</b>. Hơn nữa kiểu của biến phải là một trong các điều kiện sau:
 * <ul>
 * 	<li>Là các kiểu cơ bản: boolean, byte, char, short, int, long, float, double</li>
 * 	<li>hoặc là kiểu tham chiếu có phương thức <b>valueOf</b> tạo đối tượng từ 
 * 	một chuỗi, thí dụ: {@link Integer#valueOf(String)} , 
 * {@link Float#valueOf(String)}, ...</li>
 * <li>hoặc có 1 constructor nhận 1 đầu vào là 1 chuỗi, thí dụ: 
 * {@link java.math.BigInteger#BigInteger(String)}, 
 * {@link java.io.File#File(String)}, ...
 * </li>
 * </ul>
 * @author ducvu
 *
 */
public class S {
	
	/**
	 * Khoảng cách chiều ngang giữa hai nút kề nhau trong đồ thị
	 */
	public static int CANVAS_MARGIN_X = 120;
	
	/**
	 * Khoảng cách chiều dọc giữa hai nút kề nhau trong đồ thị
	 */
	public static int CANVAS_MARGIN_Y = 100;
	
	/**
	 * Kích thước văn bản bên trong nút đồ thị
	 */
	public static int NODE_FONT_SIZE = 12;
	
	/**
	 * Thư mục bin chứa bộ giải hệ Z3
	 */
	public static File DIR_Z3_BIN = new File("D:\\App\\Library\\z3\\bin");
	
	/**
	 * Thư mục lưu các tập tin tạm thời để xử lý
	 */
	public static File DIR_TEMP = new File("integration/temp").getAbsoluteFile();
	
	/**
	 * Số lượng lần lặp tối đa khi kiểm thử một vòng lặp
	 */
	public static int MAX_LOOP_TEST = 8;
	
	
	/**
	 * Thư mục chứa bộ biên dịch GCC
	 */
	public static File DIR_GCC = new File("D:\\App\\Library\\Cygwin\\bin");
	
	/*----------------------------CONSTANT---------------------------------*/
	
	/**
	 * Phiên bản ứng dụng
	 */
	public static final String VERSION = "2.0.0";
	
	/*---------------------------------------------------------------------*/
	
	/**
	 * Chuẩn hóa các cài đặt khi được nạp từ tập tin.<br/>
	 * Thí dụ: tạo thư mục nếu chưa có, báo lỗi sai dữ liệu, ...
	 */
	public static void postSetting(){
		DIR_TEMP.mkdirs();
	}
	
	/*---------------------------------------------------------------------*/
	
	private static Properties prop = new Properties();
	private static File prop_file = new File("integration/setting.properties");
	private static HashMap<String, Field> prop_map = new HashMap<String, Field>();
	
	static{
		prop_map.clear();
		for (Field field: S.class.getFields()){
			int mod = field.getModifiers();
			
			if (
					Modifier.isPublic(mod) 
					&& Modifier.isStatic(mod)
					&& !Modifier.isFinal(mod)
				){
				prop_map.put(field.getName(), field);
			}
		}

		if (prop_file.exists())
		try {
			
			FileInputStream file = new FileInputStream(prop_file);
			prop.load(file);
			file.close();
			
			for (String key: prop.stringPropertyNames()){
				Field field = prop_map.get(key);
				Class<?> cls = field.getType();
				Object value = prop.getProperty(key);
				
				if (cls.isPrimitive())
					cls = Utils.toWrapper(cls);
				try {
					value = cls.getMethod("valueOf", String.class).invoke(null,
							value);
				} catch (NoSuchMethodException e1) {
					value = cls.getConstructor(String.class).newInstance(value);
				}
				field.set(null, value);
			}
			
		} 
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		postSetting();
	}
	
	/**
	 * Lưu lại các thiết lập vào tập tin
	 */
	public static void save(){
		try{
			for (String key: prop_map.keySet()){
				Field field = prop_map.get(key);
				prop.setProperty(key, String.valueOf(field.get(null)));
			}
			prop_file.getParentFile().mkdirs();
			FileOutputStream file = new FileOutputStream(prop_file);

			prop.store(file, null);
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
