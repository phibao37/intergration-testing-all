package core.unit;

import java.util.ArrayList;

import core.models.Statement;

/**
 * Mô tả một đường thi hành cơ bản, đó là một dãy có thứ tự duy nhất các câu lệnh
 * sẽ được chạy bởi chương trình
 * @author ducvu
 *
 */
public class BasisPath extends ArrayList<Statement> {
	private static final long serialVersionUID = 8275921168369912688L;
	
	/**
	 * Tạo ra bản sao đường thi hành
	 */
	public BasisPath clone(){
		BasisPath clone = (BasisPath) super.clone();
		
		/*for (int i = 0; i < size(); i++)
			clone.set(i, (Statement) get(i).clone());*/
		
		return clone;
	}
	
	public String toString(){
		String content = get(0).toString();
		
		for (int i = 1; i < size(); i++){
			Statement stm = get(i);
			
			content += String.format(" -> %s",	stm);
			if (stm.isCondition())
				content += String.format(" (%s)", stm.getTrue() == get(i+1));
		}
		
		return content;
	}
	
}
