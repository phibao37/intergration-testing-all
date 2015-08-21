package core.inte;

import java.util.HashMap;

import core.models.Expression;
import core.models.Function;

/**
 * Một bộ Stub là một danh sách các cặp (theo ánh xạ) giữa một hàm số và giá trị trả về
 * của nó. Giá trị trả về này được chọn theo đặc tả, ứng với một trạng thái nhất định 
 * của chương trình
 */
public class StubSuite extends HashMap<Function, Expression> {
	private static final long serialVersionUID = 1L;

	private String mName;
	
	/**
	 * Đặt tên cho bộ stub, dùng để hiển thị và phân biệt với các bộ khác
	 */
	public void setName(String name){
		mName = name;
	}
	
	/**
	 * Trả về tên của bộ stub, hoặc <i>null</i> nếu chưa đặt
	 */
	public String getName(){
		return mName;
	}

	@Override
	public String toString() {
		return mName == null || mName.isEmpty() ? "StubSuite" : mName;
	}
}
