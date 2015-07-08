package core;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import core.graph.canvas.Canvas;
import core.graph.node.Node;


/**
 * Lớp tiện ích lưu trữ các cài đặt của ứng dụng
 * @author ducvu
 *
 */
public class Setting {
	/** Khoảng cách chiều ngang giữa hai nút kề nhau trong đồ thị*/
	public static final String CvMarginX = "canvas_margin_x";
	
	/** Khoảng cách chiều dọc giữa hai nút kề nhau trong đồ thị*/
	public static final String CvMarginY = "canvas_margin_Y";
	
	/** Kích thước văn bản bên trong nút đồ thị */
	public static final String NodeFontSize = "node_font_size";
	
	/** Cận dưới của bộ sinh random*/
	public static final String RandomLowerBound = "rand_lower_bound";
	
	/** Cận trên của bộ sinh random*/
	public static final String RandomUpperBound = "rand_upper_bound";
	
	public static final String SolveLoop = "solve_loop";
	
	private static Properties pro = new Properties();
	private static File setting = new File("integration/setting.properties");
	private static Map<String, Method> keyMap = new HashMap<String, Method>();
	
	/**
	 * Nạp các cài đặt vào chương trình
	 */
	public static void loadSetting(){
		keyMap.clear();
		try {
			keyMap.put(CvMarginX,
					Canvas.class.getMethod("setMarginX", Integer.class));
			keyMap.put(CvMarginY,
					Canvas.class.getMethod("setMarginY", Integer.class));
			keyMap.put(NodeFontSize,
					Node.class.getMethod("setFontSize", Integer.class));
			/*keyMap.put(RandomLowerBound,
				RandomGenarator.class.getMethod("setLowerBound", Integer.class));
			keyMap.put(RandomUpperBound,
					RandomGenarator.class.getMethod("setUpperBound", Integer.class));
			keyMap.put(SolveLoop,
					RandomSolveEquation.class.getMethod("setSolveLoop", Integer.class));*/
		} catch (Exception ex) {}
		
		try {
			if (setting.exists()) {
				FileInputStream file = new FileInputStream(setting);
				pro.load(file);
				file.close();
			} else {
				File dir = new File("integration");
				if (!dir.exists())
					dir.mkdir();
				FileOutputStream file = new FileOutputStream(setting);
				file.close();
			}
			
			for (String key: pro.stringPropertyNames()){
				String value = pro.getProperty(key);
				
				setObjectValue(key, value);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/** Gán giá trị cho một khóa xác định, chỉ ở trong chương trình*/
	private static void setObjectValue(String key, Object value) throws Exception{
		Method setter = keyMap.get(key);
		if (setter == null)
			throw new Exception(String.format("No property has name \"%s\"", key));
		
		Class<?> type = setter.getParameterTypes()[0];
		Method valueOf = type.getMethod("valueOf", String.class);
		Object result = valueOf.invoke(null, String.valueOf(value));
		setter.invoke(null, result);
	}
	
	/**
	 * Gán giá trị cho một thuộc tính cài đặt xác định và ghi nhận trong file lưu trữ
	 * @param key khóa của thuộc tính
	 * @param value giá trị của thuộc tính
	 */
	public static void setProperty(String key, Object value){
		try {
			FileOutputStream file = new FileOutputStream(setting);
			pro.setProperty(key, String.valueOf(value));
			pro.store(file, null);
			file.close();
			
			setObjectValue(key, value);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
