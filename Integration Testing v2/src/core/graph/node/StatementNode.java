package core.graph.node;

import core.models.Statement;

/**
 * Nút đồ họa chứa 1 câu lệnh
 * @author ducvu
 *
 */
public class StatementNode extends Node {
	private static final long serialVersionUID = 175537134709065602L;
	
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
	
	/** Kiểm tra nút này đã được đặt vị trí*/
	public boolean isLocationSet(){
		return this.getX() != 0 || this.getY() != 0;
	}
	
}
