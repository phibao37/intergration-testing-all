package graph.node;

import core.models.Statement;

/**
 * Nút đồ họa chứa 1 câu lệnh
 * @author ducvu
 *
 */
public class StatementNode extends Node {
	private static final long serialVersionUID = 175537134709065602L;
	
	private String mLabel = "";
	
	/** Tạo một nút từ một câu lệnh tương ứng*/
	public StatementNode(Statement statement){
		super(statement);
	}
	
	/** Trả về câu lệnh tương ứng với nút*/
	public Statement getStatement(){
		return (Statement) mElement;
	}
	
	/** Kiểm tra nút có là nút điều kiện hay không*/
	public boolean isConditionNode(){
		return getStatement().isCondition();
	}
	
	/** Kiểm tra nút có là nút điều kiện đơn giản <br/>
	 * (1 câu lệnh duy nhất tại nhánh, hoặc câu lệnh return) hay không*/
	public boolean is1StmConditionNode(){
		if (!isConditionNode())
			return false;
		Statement stm = getStatement(), 
				trueStm = stm.getTrue(),
				falseStm = stm.getFalse();
		if (trueStm.isCondition())
			return false;
		return trueStm.getType() == Statement.RETURN || trueStm.getTrue() == falseStm;
	}
	
	/**
	 * Gán nút này không có số thứ tự
	 */
	public static final int LABEL_NONE = -1;
	
	/**
	 * Cờ hiệu nút này có nhánh true được chọn
	 */
	public static final int FLAG_SELECT_TRUE = 1;
	
	/**
	 * Cờ hiệu nút này có nhánh false được chọn
	 */
	public static final int FLAG_SELECT_FALSE = 2;
	
	/**
	 * Cờ hiệu nút này có nhánh true được chọn bổ sung
	 */
	public static final int FLAG_SELECT_TRUE_EXTRA = 4;
	
	/**
	 * Cờ hiệu nút này có nhánh false được chọn bổ sung
	 */
	public static final int FLAG_SELECT_FALSE_EXTRA = 8;
	
	/**
	 * Thêm nhãn thứ tự cho nút
	 * @param label số thứ tự vị trí của nút trong đường đi, hoặc {@link #LABEL_NONE} 
	 * nếu muốn hủy bỏ toàn bộ 
	 */
	public void setLabel(int label){
		if (label == LABEL_NONE)
			mLabel = "";
		else
			mLabel += (mLabel.isEmpty() ? "" : ", ") + label;
	}
	
	/**
	 * Trả về nhãn thứ tự cho nút
	 */
	public String getLabel(){
		return mLabel;
	}
	
}
