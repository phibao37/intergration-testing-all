package core;
import java.io.*;
import java.util.*;

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
		return getContentStream(new FileInputStream(file));
	}
	
	/**
	 * Lấy chuỗi nội dung từ một inputstream
	 */
	public static String getContentStream(InputStream stream) throws IOException{
		StringBuilder content = new StringBuilder();
		String line;

		try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
			while ((line = br.readLine()) != null)
				content.append(line).append('\n');
		}
		
		return content.toString();
	}
	
	/**
	 * Tìm xem một đối tượng có trong một mảng hay không
	 */
	public static boolean find(Object[] arr, Object find){
		for (Object o: arr)
			if ((o == null && find == null) || (o != null && o.equals(find)))
				return true;
		return false;
	}
	
	/**
	 * Tìm xem một đối tượng có trong một danh sách hay không
	 */
	public static boolean find(Iterable<?> list, Object find){
		for (Object o: list)
			if ((o == null && find == null) || (o != null && o.equals(find)))
				return true;
		return false;
	}
	
	/**
	 * Tìm xem một đối tượng có trong một danh sách hay không, chỉ sử dụng ==
	 */
	public static boolean findExact(Iterable<?> list, Object find){
		for (Object o: list)
			if (o == find)
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
		return merge(sep, list.toArray());
	}
	
	/**
	 * @see #merge(String, Object[])
	 */
	public static String merge(String sep, int[] args){
		if (args == null || args.length == 0)
			return "";
		
		String result = String.valueOf(args[0]);
		for (int i = 1; i < args.length; i++)
			result += sep + args[i];
		return result;
	}
	
	/**
	 * Chuyển từ mảng sang danh sách
	 */
	public static <T> ArrayList<T> toList(T[] array){
		return new ArrayList<>(Arrays.asList(array));
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
	
	/**
	 * Nhân 2 danh sách với nhau và trả về danh sách kết quả
	 * @param list1 danh sách bên trái
	 * @param list2 danh sách bên phải
	 * @return danh sách kết quả, mỗi phần tử là một danh sách bao gồm 
	 * 1 phần tử của danh sách bên trái ghép với 1 phần tử của danh sách bên phải
	 */
//	public static <T extends Collection<V>, V> ArrayList<ArrayList<V>> multiply(
//			List<T> list1, List<T> list2){
//		ArrayList<ArrayList<V>> lists = new ArrayList<>();
//
//		for (T item1: list1)
//			for (T item2: list2){
//				ArrayList<V> list = new ArrayList<V>();
//				list.addAll(item1);
//				list.addAll(item2);
//				lists.add(list);
//			}
//
//		return lists;
//	}
	
	/**
	 * Định dạng chuỗi theo các tham số
	 */
	public static String format(String format, Object... args){
		return args.length == 0 ? format : String.format(format, args);
	}
	
	/**
	 * Trả về nội dung hiển thị của chuỗi, hoặc ifNull nếu đối tượng là null
	 */
	public static String toString(Object o, String ifNull){
		return o == null ? ifNull : o.toString();
	}
	
	/**
	 * Nhân mỗi phần tử của danh sách bên trái với mỗi phân tử của danh sách bên phải
	 * @param list1 danh sách bên trái
	 * @param list2 danh sách bên phải
	 */
	public static <T extends Collection<V>, V> void addMultiply(
			List<ArrayList<V>> list1, List<T> list2){
		List<ArrayList<V>> lists = new ArrayList<>(list1);
		list1.clear();
		
		for (ArrayList<V> item1: lists)
			for (T item2: list2){
				ArrayList<V> list = new ArrayList<>();
				list.addAll(item1);
				list.addAll(item2);
				list1.add(list);
			}
	}
	
	/**
	 * Chạy một tiến trình và đợi lấy chuỗi kết quả trả về
	 * @param target đối tượng cần được thực thi, thường là String hoặc File
	 * @param envp danh sách các biến môi trường, hoặc null
	 * @param dir thư mục mà tiến trình sẽ được thực thi, hoặc null
	 * @param args danh sách các tham số tùy chọn
	 * @return chuỗi kết quả sau khi thực thi
	 * @throws IOException có lỗi I/O khi thực thi 
	 * @throws InterruptedException tiến trình đang chạy bị dừng đột ngột
	 * @throws ProcessErrorException tiến trình trả về mã lỗi khác 0
	 * @throws NullPointerException đối tượng thực thi hoặc các tham số bằng null
	 */
//	public static String runCommand(Object target, String[] envp, 
//			File dir, Object... args) 
//			throws IOException, InterruptedException, 
//			ProcessErrorException, NullPointerException{
//		
//		String[] cmdArray = new String[args.length + 1];
//		cmdArray[0] = target.toString();
//		for (int i = 0; i < args.length; i++)
//			cmdArray[i + 1] = args[i].toString();
//		
//		Process p = Runtime.getRuntime().exec(cmdArray, envp, dir);
//		int exit = p.waitFor();
//		
//		if (exit != 0)
//			throw new ProcessErrorException(exit, 
//					Utils.getContentStream(p.getErrorStream()));
//		
//		return Utils.getContentStream(p.getInputStream());
//	}
	
	/**
	 * Trả về kích thước của tập tin hoặc thư mục
	 */
	public static long getFileSize(File file){
		long size = 0;
		
		if (file.isDirectory()) {
			File[] childs = file.listFiles();
			assert childs != null;

			for (File child : childs)
				size += getFileSize(child);
		}
		else
			size += file.length();
		
		return size;
	}
	
	/**
	 * Xóa một tập tin hoặc một thư mục
	 */
	public static void deleteFile(File file){
		if (file.isDirectory()){
			File[] childs = file.listFiles();
			assert childs != null;
			for (File child: childs)
				deleteFile(child);
		}
		
		file.delete();
	}
	
	/**
	 * Kiểm tra một tập chỉ thị có chứa 1 chỉ thị cụ thể hay không
	 */
	public static boolean hasFlag(long flags, long flag){
		return (flags & flag) != 0;
	}
	
	/**
	 * Kiểm tra một số thực có thể rút gọn lại thành số nguyên: 2; 1.00; ...
	 */
//	public static boolean isIntegerValue(BigDecimal bd) {
//		return bd.signum() == 0 || bd.scale() <= 0
//				|| bd.stripTrailingZeros().scale() <= 0;
//	}

	/**
	 * Chuyển từ kiểu đơn giản sang cờ hiệu dùng cho IDExpression
	 */
//	public static int basicTypeToFlag(BasicType type){
//		switch (type.getSize()){
//		case BasicType.BOOL_SIZE:
//			return IDExpression.BOOLEAN;
//		case BasicType.CHAR_SIZE:
//			return IDExpression.CHARACTER;
//		case BasicType.INT_SIZE:
//			return IDExpression.INTEGER;
//		case BasicType.LONG_SIZE:
//			return IDExpression.LONG;
//		case BasicType.FLOAT_SIZE:
//			return IDExpression.FLOAT;
//		case BasicType.DOUBLE_SIZE:
//			return IDExpression.DOUBLE;
//		default:
//			return 0;
//		}
//	}
	
	/**
	 * Trả về định dạng của tệp tin, là chuỗi sau dấu "." cuối cùng của tên tập tin
	 */
	public static String getExtension(File file){
		String extension = "", fileName = file.getAbsolutePath();

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
		    extension = fileName.substring(i+1);
		}
		return extension;
	}
	
	/**
	 * Đưa một nội dung trong body vào một trang html hoàn chỉnh
	 */
	public static String html(String body){
		return String.format("<html><body>%s</body></html>", body);
	}
	
	/**
	 * Trả về ánh xạ giữa chỉ số và chuỗi giá trị của phần tử
	 */
	public static LinkedHashMap<int[], String> getValueMap(String arrayValue){
		LinkedHashMap<int[], String> map = new LinkedHashMap<>();
		for (String elm: arrayValue.split(", ?")){
			String[] part = elm.split(" ?=> ?");
			String[] index = part[0].split(" ");
			int[] indexes = new int[index.length];
			
			for (int i = 0; i < index.length; i++)
				indexes[i] = Integer.valueOf(index[i]);
			map.put(indexes, part[1]);
		}
		return map;
	}
	
}
