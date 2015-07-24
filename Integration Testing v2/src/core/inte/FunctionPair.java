package core.inte;

import core.models.Function;
import javafx.util.Pair;

/**
 * Mô tả một cặp hàm gọi hàm: <hàm cha> gọi <hàm con>, 
 * hoặc sử dụng chung biến toàn cục, ...
 */
public class FunctionPair extends Pair<Function, Function> {
	private static final long serialVersionUID = 1L;

	/**
	 * Tạo mới một cặp hàm gọi hàm
	 * @param caller hàm cha
	 * @param calling hàm con được gọi
	 */
	public FunctionPair(Function caller, Function calling) {
		super(caller, calling);
	}
	
	/**
	 * Trả về hàm cha
	 */
	public Function getCaller(){
		return getKey();
	}
	
	/**
	 * Trả về hàm con được gọi
	 */
	public Function getCalling(){
		return getValue();
	}

	@Override
	public String toString() {
		return getCaller().getName() + " => " + getCalling().getName();
	}
	
	
}
