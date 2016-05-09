package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map.Entry;
/**
 * Lớp tiện ích giúp lưu trữ các cài đặt của ứng dụng.<br/>
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
 */
public class Config {

	
	public static File DIR_TEMP = new File("local/temp").getAbsoluteFile();
	
	public static File DIR_Z3_BIN = new File("local/z3/bin").getAbsoluteFile();
	
	public static File DIR_GCC = new File(
			"C:/Program Files (x86)/Dev-Cpp/MinGW64/bin");
	
	public static File DIR_EXPORT = new File("local/export").getAbsoluteFile();
	
	/**
	 * Số lượng lần lặp tối đa khi kiểm thử một vòng lặp
	 */
	public static int MAX_LOOP_TEST = 8;
	
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
	 * Danh sách các bộ giải hệ ràng buộc
	 */
	public static String[] LIST_SOLVER = BaseProject.BASE_LIST_SOLVER;
	
	public static int CFG_MARGIN_X = 150;
	public static int CFG_MARGIN_Y = 120;
	public static boolean SHOW_CFG_DETAILS = true;
	public static boolean SHOW_CFG_STATEMENT_POS = true;
	
	/**
	 * Đường dẫn project đang được ghim
	 */
	public static File PINNED_PROJECT = null;
	
	/*---------------------------------------------------------------------*/
	
	private static void setupInit(){
		DIR_TEMP.mkdirs();
		DIR_EXPORT.mkdirs();
	}

/*---------------------------NON-SETTING-------------------------------*/
	
	/**
	 * Phiên bản ứng dụng
	 */
	public static final String VERSION = "3.0.0";
	
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
	
	/*---------------------------------------------------------------------*/
	
	private static Properties prop = new Properties();
	private static File prop_file = new File("local/setting.properties");
	private static HashMap<String, Field> prop_map = new HashMap<>();
	
	static{
		prop_map.clear();
		for (Field field: Config.class.getFields()){
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
	
		setupInit();
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
			for (Entry<String, Field> entry: prop_map.entrySet()){
				Object value = entry.getValue().get(null);
				
				if (value != null)
					prop.setProperty(entry.getKey(), pushValue(value));
				else
					prop.remove(entry.getKey());
			}
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
