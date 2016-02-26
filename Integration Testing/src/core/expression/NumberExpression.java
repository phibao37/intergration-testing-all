package core.expression;

import api.expression.INumberExpression;
import core.Utils;
import core.models.type.BasicType;

/**
 * Mô tả các biểu thức hằng trong ngôn ngữ lập trình, thường là:
 * <ul>
 * 	<li>123: hằng dạng số nguyên</li>
 * 	<li>12.3: hằng dạng số thực</li>
 * 	<li>'1': hằng dạng kí tự</li>
 * 	<li>"123": hằng dạng chuỗi</li>
 * 	<li>true: hằng dạng nhị phân</li>
 * </ul>
 * @author ducvu
 *
 */
public class NumberExpression extends Expression implements INumberExpression{

	/**
	 * Demo NumberExpression Creator
	 */
	public static void main(String[] args){
		
		NumberExpression x;
		
		//Khởi tạo bằng cách truyền trực tiếp kiểu trong Java
		x = new NumberExpression(true);  //type = bool
		x = new NumberExpression('a');  //type = char
		x = new NumberExpression(12);  //type = int
		x = new NumberExpression(12l);  //type = long
		x = new NumberExpression(12f); //type = float
		x = new NumberExpression(12d);  //type = double
		
		//Khởi tạo bằng cách truyền chuỗi string trong mã nguồn,
		//cần thêm cờ hiệu để xác định được nhanh hơn
		x = new NumberExpression("true", BOOLEAN); //type = bool
		x = new NumberExpression("12", INTEGER | LONG); //type = int
		x = new NumberExpression("2147483647777", INTEGER | LONG); //type = long
		
		//Khởi tạo bằng chuỗi, không có gợi ý. Cần phân tích lâu hơn
		x = new NumberExpression("12.3");
		//x = new NumberExpression("12.3", FLOAT); //nhanh hon
		System.out.println(x.getType()); //type = float
	}
	
	/**
	 * Biểu thức hằng số nguyên 0
	 */
	public static final NumberExpression ZERO = new NumberExpression(0);
	
	/**
	 * Biểu thức hằng số nguyên 1
	 */
	public static final NumberExpression ONE = new NumberExpression(1);
	
	/**
	 * Cho biết nội dung có thể là dạng nhị phân
	 */
	public static final int BOOLEAN = 1;
	
	/**
	 * Cho biết nội dung có thể là dạng kí tự
	 */
	public static final int CHARACTER = 2;
	
	/**
	 * Cho biết nội dung có thể là dạng số nguyên
	 */
	public static final int INTEGER = 4;
	
	/**
	 * Cho biết nội dung có thể là dạng số nguyên lớn
	 */
	public static final int LONG = 8;
	
	/**
	 * Cho biết nội dung có thể là dạng số thực động
	 */
	public static final int FLOAT = 16;
	
	/**
	 * Cho biết nội dung có thể là dạng số thực
	 */
	public static final int DOUBLE = 32;
	
	/**
	 * Cho biết nội dung có thể là dạng số
	 */
	public static final int NUMBER = INTEGER | LONG | FLOAT | DOUBLE;
	
	private BasicType mType;
	private String mExtraDisplay;
	
	private boolean valBool;
	private char valChar;
	private int valInt;
	private long valLong;
	private float valFloat;
	private double valDouble;
	
	/**
	 * Tạo một biểu thức hằng, kiểu sẽ do nội dung quyết định
	 * @param content nội dung của biểu thức ở dạng chuỗi
	 */
	public NumberExpression(String content){
		this(content, -1);
	}
	
	/**
	 * Tạo một biểu thức hằng
	 * @param content nội dung của biểu thức ở dạng chuỗi
	 * @param posibleType các cờ hiệu về kiêu mà biểu thức này có thể thuộc về 
	 */
	public NumberExpression(String content, int posibleType){
		super(content);
		if (Utils.hasFlag(posibleType, BOOLEAN)){
			try{
				boolean val;
				
				if (content.equals("true"))
					val = true;
				else if (content.equals("false"))
					val = false;
				else
					throw new NumberFormatException();
				
				setBool(val);
			} catch (NumberFormatException e) {}
		}
		
		if (Utils.hasFlag(posibleType, CHARACTER)){
			if (content.matches("'\\w'"))
				setChar(content.charAt(1));
		}
		
		if (Utils.hasFlag(posibleType, INTEGER)){
			try{
				setInt(Integer.valueOf(content));
			} catch (NumberFormatException e) {}
		}
		
		if (Utils.hasFlag(posibleType, LONG)){
			try{
				setLong(Long.valueOf(content));
			} catch (NumberFormatException e) {}
		}
		
		if (Utils.hasFlag(posibleType, FLOAT)){
			try{
				setFloat(Float.valueOf(content));
			} catch (NumberFormatException e) {}
		}
		
		if (Utils.hasFlag(posibleType, DOUBLE)){
			try{
				setDouble(Double.valueOf(content));
			} catch (NumberFormatException e) {}
		}
		
		if (mType == null)
			System.out.printf("Chưa hỗ trợ biểu thức hằng: %s, %d\n", 
					content, posibleType);
	}
	
	/**
	 * Tạo biểu thức hằng theo giá trị của nó
	 * @param value giá trị hằng của biểu thức
	 */
	public NumberExpression(Object value){
		if (value instanceof Boolean)
			setBool((boolean) value);
		else if (value instanceof Character)
			setChar((char) value);
		else if (value instanceof Integer)
			setInt((int) value);
		else if (value instanceof Long)
			setLong((long) value);
		else if (value instanceof Float)
			setFloat((float) value);
		else if (value instanceof Double)
			setDouble((double) value);
		else
			System.out.println("Chưa hỗ trợ biểu thức hằng: " + value 
					+ ", kiểu: " + value == null ? null : value.getClass());
	}
	
	private void setBool(boolean val){
		valBool = val;
		valChar = (char) (val ? 1 : 0);
		valInt = val ? 1 : 0;
		valLong = valInt;
		valFloat = valInt;
		valDouble = valInt;
		if (mType == null){
			mType = BasicType.BOOL;
			setContent(val);
		}
	}
	
	private void setChar(char val){
		valBool = val != 0;
		valChar = val;
		valInt = val;
		valLong = val;
		valFloat = val;
		valDouble = val;
		if (mType == null){
			mType = BasicType.CHAR;
			setContent(val);
		}
	}
	
	private void setInt(int val){
		valBool = val != 0;
		valChar = (char) val;
		valInt = val;
		valLong = val;
		valFloat = val;
		valDouble = val;
		if (mType == null){
			mType = BasicType.INT;
			setContent(val);
		}
	}
	
	private void setLong(long val){
		valBool = val != 0;
		valChar = (char) val;
		valInt = (int) val;
		valLong = val;
		valFloat = val;
		valDouble = val;
		if (mType == null){
			mType = BasicType.LONG;
			setContent(val);
		}
	}
	
	private void setFloat(float val){
		valBool = val != 0;
		valChar = (char) val;
		valInt = (int) val;
		valLong = (long) val;
		valFloat = val;
		valDouble = val;
		if (mType == null){
			mType = BasicType.FLOAT;
			setContent(val);
		}
	}
	
	private void setDouble(double val){
		valBool = val != 0;
		valChar = (char) val;
		valInt = (int) val;
		valLong = (long) val;
		valFloat = (float) val;
		valDouble = val;
		if (mType == null){
			mType = BasicType.DOUBLE;
			setContent(val);
		}
	}
	
	/* (non-Javadoc)
	 * @see core.expression.IIDExpression#intValue()
	 */
	@Override
	public int intValue(){
		return valInt;
	}
	
	/* (non-Javadoc)
	 * @see core.expression.IIDExpression#longValue()
	 */
	@Override
	public long longValue(){
		return valLong;
	}
	
	/* (non-Javadoc)
	 * @see core.expression.IIDExpression#floatValue()
	 */
	@Override
	public float floatValue(){
		return valFloat;
	}
	
	/* (non-Javadoc)
	 * @see core.expression.IIDExpression#doubleValue()
	 */
	@Override
	public double doubleValue(){
		return valDouble;
	}
	
	/* (non-Javadoc)
	 * @see core.expression.IIDExpression#charValue()
	 */
	@Override
	public char charValue(){
		return valChar;
	}
	
	/* (non-Javadoc)
	 * @see core.expression.IIDExpression#boolValue()
	 */
	@Override
	public boolean boolValue(){
		return valBool;
	}

	/**
	 * Trả về kiểu của biểu thức hằng
	 */
	@Override
	public BasicType getType(){
		return mType;
	}
	
	/**
	 * Đặt nội dung qua đối tượng
	 */
	private void setContent(Object content){
		setContent(String.valueOf(content));
	}
	
	/**
	 * Thiết đặt một cách hiển thị khác của biểu thức, chỉ dùng để hiển thị.<br/>
	 * Thí dụ, ta có new IDExpression("0.333333333333333333").setExtraDisplay("1/3");
	 */
	public NumberExpression setExtraDisplay(String display){
		mExtraDisplay = display;
		return this;
	}
	
	@Override
	public String toString() {
		return mExtraDisplay != null ? mExtraDisplay : super.toString();
	}

}
