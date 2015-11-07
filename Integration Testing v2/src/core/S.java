package core;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import core.solver.Solver;


/**
 * Rút gọn của Settings. Lớp tiện ích giúp lưu trữ các cài đặt của ứng dụng.<br/>
 * Các thuộc tính cài đặt cần là một biến thực thể <b>public</b>, <b>static</b> và 
 * <b>non-final</b>, <b>non-transient</b>. 
 * Hơn nữa kiểu của biến phải là một trong các điều kiện sau:
 * <ul>
 * 	<li>Là các kiểu cơ bản: boolean, byte, char, short, int, long, float, double</li>
 * 	<li>hoặc là kiểu tham chiếu có phương thức <b>valueOf</b> tạo đối tượng từ 
 * 	một chuỗi, thí dụ: {@link Integer#valueOf(String)} , 
 * {@link Float#valueOf(String)}, ...</li>
 * <li>hoặc có 1 constructor nhận 1 đầu vào là 1 chuỗi, thí dụ: 
 * {@link java.math.BigInteger#BigInteger(String)}, 
 * {@link java.io.File#File(String)}, ...
 * </li>
 * <li>hoặc là mảng của một trong các kiểu biến thỏa mãn một trong các điều kiện
 * trên, chỉ hỗ trợ mảng một chiều</li>
 * </ul>
 * @author ducvu
 *
 */
public class S {
	
	/**
	 * Khoảng cách chiều ngang giữa hai nút kề nhau trong đồ thị
	 */
	public static int CANVAS_MARGIN_X = 150;
	
	/**
	 * Khoảng cách chiều dọc giữa hai nút kề nhau trong đồ thị
	 */
	public static int CANVAS_MARGIN_Y = 90;
	
	/**
	 * Mặc định mở toolbar khi tạo một canvas mới
	 */
	public static boolean CANVAS_SHOW_TOOLBAR = true;
	
	/**
	 * Chế độ vẽ các nút theo cấp bậc cha/con
	 */
	public static boolean CANVAS_DRAW_TOPDOWN = true;
	
	/**
	 * Kích thước văn bản bên trong nút đồ thị
	 */
	public static int NODE_FONT_SIZE = 12;
	
	/**
	 * Thư mục bin chứa bộ giải hệ Z3
	 */
	public static File DIR_Z3_BIN = new File("integration/z3/bin").getAbsoluteFile();
	
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
	public static File DIR_GCC = new File("integration/Cygwin/bin").getAbsoluteFile();
	
	
	/**
	 * Số lần lặp tối đa để giải hệ random
	 */
	public static int RAND_LOOP = 500;
	
	/**
	 * Cận dưới khi sinh random cho các kiểu số
	 */
	public static int RAND_MIN = -50;
	
	/**
	 * Cận trên khi sinh random cho các kiểu số
	 */
	public static int RAND_MAX = 50;
	
	/**
	 * Danh sách các bộ giải được dùng để giải hệ ràng buộc
	 */
	public static Solver[] SOLVE_LIST = Solver.BASE_LIST;

	/*---------------------------------------------------------------------*/
	
	/**
	 * Chuẩn hóa các cài đặt khi được nạp từ tập tin.<br/>
	 * Thí dụ: tạo thư mục nếu chưa có, báo lỗi sai dữ liệu, ...
	 */
	public static void postSetting(){
		DIR_TEMP.mkdirs();
	}
	
	/*---------------------------NON-SETTING-------------------------------*/
	
	/**
	 * Phiên bản ứng dụng
	 */
	public static final String VERSION = "2.3.0";
	
	/**
	 * Chế độ debug ứng dụng
	 */
	public static transient boolean DEBUG = false;
	
	/**
	 * In ra console với định dạng, và chỉ in khi {@link #DEBUG} đang được bật
	 */
	public static void f(String format, Object ... args){
		if (DEBUG)
			System.out.printf(format, args);
	}
	
	/**
	 * In ra một chuỗi và xuống dòng, và chỉ in khi {@link #DEBUG} đang được bật
	 */
//	public static void p(String string){
//		if (DEBUG)
//			System.out.println(string);
//	}
	
	/*---------------------------INNER CLASS-------------------------------*/
	
	/**
	 * Các thông số về màn hình như kích thước ngang, dọc,...
	 */
	public static class SCREEN{
		
		/**
		 * Chiều ngang của màn hình (px)
		 */
		public static final int WIDTH;
		
		/**
		 * Chiều dọc của màn hình (px)
		 */
		public static final int HEIGHT;
		
		static {
			Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
			WIDTH = size.width;
			HEIGHT = size.height;
		}
	}
	
	/*---------------------------------------------------------------------*/
	
	private static Properties prop = new Properties();
	private static File prop_file = new File("integration/setting.properties");
	private static HashMap<String, Field> prop_map = new HashMap<>();
	
	static{
		prop_map.clear();
		for (Field field: S.class.getFields()){
			int mod = field.getModifiers();
			
			if (
					Modifier.isPublic(mod) 
					&& Modifier.isStatic(mod)
					&& !Modifier.isFinal(mod)
					&& !Modifier.isTransient(mod)
				){
				prop_map.put(field.getName(), field);
			}
		}

		if (prop_file.exists())
		try {

			FileInputStream file = new FileInputStream(prop_file);
			prop.load(file);
			file.close();

			for (String key : prop.stringPropertyNames()) {
				Field field = prop_map.get(key);
				field.set(null, inflateValue(field.getType(), prop.getProperty(key)));
			}

		}

		catch (Exception e) {
			e.printStackTrace();
		}
	
		postSetting();
	}
	
	/**
	 * Tạo đối tượng từ kiểu và chuỗi giá trị tương ứng
	 */
	private static Object inflateValue(Class<?> cls, String value) throws Exception{
		if (cls.isArray()){
			Class<?> itemCls = cls.getComponentType();
			if (value.isEmpty())
				return Array.newInstance(itemCls, 0);
			
			String[] values = value.split(", ?");
			Object r = Array.newInstance(itemCls, values.length);
			
			for (int i = 0; i < values.length; i++){
				Object item = inflateValue(itemCls, values[i]);
				Array.set(r, i, item);
			}
			
			return r;
		}
		
		if (cls.isPrimitive())
			cls = Utils.toWrapper(cls);
		try {
			return cls.getMethod("valueOf", String.class).invoke(null, value);
		} catch (NoSuchMethodException e1) {
			if (cls == Character.class)
				return cls.getConstructor(char.class)
				.newInstance(value.charAt(0));
			else
				return cls.getConstructor(String.class).newInstance(value);
		}
	}
	
	/**
	 * Lưu lại các thiết lập vào tập tin
	 */
	public static void save(){
		try{
			for (Entry<String, Field> entry: prop_map.entrySet())
				prop.setProperty(
						entry.getKey(), 
						pushValue(entry.getValue().get(null))
				);
			prop_file.getParentFile().mkdirs();
			FileOutputStream file = new FileOutputStream(prop_file);

			prop.store(file, null);
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String pushValue(Object value){
		if (value.getClass().isArray()){
			String r = "";
			int l = Array.getLength(value);
			
			if (l > 0){
				r = pushValue(Array.get(value, 0));
				for (int i = 1; i < l; i++)
					r += ", " + pushValue(Array.get(value, i));
			}
			
			return r;
		}
		else
			return String.valueOf(value);
	}
	
}
