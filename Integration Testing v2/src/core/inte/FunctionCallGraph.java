package core.inte;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import core.Utils;
import core.error.MainNotFoundException;
import core.models.Function;

/**
 * Mô tả đồ thị gọi hàm, là một cấu trúc cây gồm danh sách các hàm trong chương trình 
 * cùng được sử dụng từ một hàm số gốc (thường là hàm main).<br/>
 * Đồ thị dùng để duyệt qua các kiểu thực hiện kiểm thử như top-down, bottom-up,...
 *
 */
public class FunctionCallGraph extends ArrayList<Function> {
	private static final long serialVersionUID = 1L;
	
	public static final int BOTTOM_UP = 0;
	public static final int TOP_DOWN = 1;
	
	private Function mRoot;
	
	/**
	 * Thiết đặt hàm có tên "main" là hàm gốc
	 * @return đồ thị hiện thời
	 * @throws MainNotFoundException không tìm được hàm main
	 */
	public FunctionCallGraph setByMain() throws MainNotFoundException{
		for (Function f: this)
			if (f.getName().equals("main"))
				return setRoot(f);
		
		throw new MainNotFoundException();
	}
	
	/**
	 * Thiết đặt một hàm số chỉ định là hàm gốc
	 */
	public FunctionCallGraph setRoot(Function root){
		mRoot = root;
		return this;
	}
	
	/**
	 * Trả về hàm số gốc, gọi đến các hàm số khác trong nó
	 */
	public Function getRoot(){
		if (mRoot == null)
			try {
				setByMain();
			} catch (MainNotFoundException e){
				setRoot(get(0));
			}
		return mRoot;
	}
	
	@Override
	public void clear() {
		mRoot = null;
		super.clear();
	}
	
	/**
	 * Lấy danh sách theo thứ tự bottom-up hoặc top-down
	 */
	private ArrayList<FunctionPair> getList(boolean reverse){
		ArrayList<Function> breathList = new ArrayList<Function>();
		ArrayList<FunctionPair> pairs = new ArrayList<FunctionPair>();
		LinkedList<Function> queue = new LinkedList<Function>();
		
		queue.add(getRoot());
		while (!queue.isEmpty()){
			Function pick = queue.remove();
			breathList.add(pick);
			
			for (Function refer: pick.getRefers())
				if (!Utils.findExact(breathList, refer))
					queue.add(refer);
		}
		
		if (reverse)
		Collections.reverse(breathList);
		
		for (Function func: breathList)
			for (Function refer: func.getRefers())
				pairs.add(new FunctionPair(func, refer));
		
		return pairs;
	}
	
	/**
	 * Lấy danh sách thứ tự thực hiện kiểm thử tích hợp các cặp hàm theo chế độ tùy chọn
	 * @param type các chế độ duyệt
	 * @return danh sách các cặp kiểm thử theo thứ tự của chế độ duyệt
	 */
	public ArrayList<FunctionPair> getList(int type){
		switch (type) {
		case BOTTOM_UP:
			return getList(true);
		case TOP_DOWN:
			return getList(false);
		default:
			return null;
		}
	}
}
