package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Các tiện ích chung
 * @author ducvu
 *
 */
public class Utils {
	
	/**
	 * Lấy nội dung từ một tập tin chỉ định
	 * @param file tập tin nguồn
	 * @return nội dung văn bản bên trong tập tin
	 * @throws IOException lỗi lấy nội dung
	 */
	public static String getContentFile(File file) throws IOException {
		StringBuilder content = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(file));
		try {
			String line = br.readLine();
			while (line != null) {
				content.append(line);
				content.append("\n");
				line = br.readLine();
			}
		}
		finally {
			br.close();
		}
		return content.toString();
	}
	
	/**
	 * Tìm xem một đối tượng có trong một mảng hay không
	 */
	public static boolean find(Object[] arr, Object find){
		for (Object o: arr)
			if ((o == null && find == o) || o.equals(find))
				return true;
		return false;
	}
	
	/**
	 * Trả về chuỗi nối từ một mảng
	 * @param sep chuỗi ngăn cách
	 * @param args danh sách phần tử
	 * @return chuỗi được nối
	 */
	public static <T> String merge(String sep, T[] args){
		if (args == null || args.length == 0)
			return "";
		
		String result = String.valueOf(args[0]);
		for (int i = 1; i < args.length; i++)
			result += sep + args[i];
		return result;
	}
	
	/**
	 * @see #merge(String, Object[])
	 */
	public static String merge(String sep, List<?> list){
		Object[] array = new Object[list.size()];
		list.toArray(array);
		
		return merge(sep, array);
	}
	
	/**
	 * Chuyển từ mảng sang danh sách
	 */
	public static <T> ArrayList<T> toList(T[] array){
		return new ArrayList<T>(Arrays.asList(array));
	}
	
	/**
	 * Chuyển kiểu cơ bản sang kiểu bao tương ưng
	 */
	public static Class<?> toWrapper(Class<?> cls){
		if (cls == int.class)
			cls = Integer.class;
		else if (cls == long.class)
			cls = Long.class;
		else if (cls == float.class)
			cls = Float.class;
		else if (cls == double.class)
			cls = Double.class;
		else if (cls == short.class)
			cls = Short.class;
		else if (cls == char.class)
			cls = Character.class;
		else if (cls == boolean.class)
			cls = Boolean.class;
		else if (cls == byte.class)
			cls = Byte.class;
		
		return cls;
	}
	
}
