package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Properties;


/**
 * Rút gọn của Settings. Lớp tiện ích giúp lưu trữ các cài đặt của ứng dụng
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
	public static String Z3_BIN_DIR = "D:\\App\\Library\\z3\\bin";
	
	/*---------------------------------------------------------------------*/
	
	private static Properties prop = new Properties();
	private static File prop_file = new File("integration/setting.properties");
	private static HashMap<String, Field> prop_map = new HashMap<String, Field>();
	
	/**
	 * Nạp các cài đặt vào chương trình
	 */
	public static void load(){
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
		
		try {
			if (!prop_file.exists())
				return;
			
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
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
