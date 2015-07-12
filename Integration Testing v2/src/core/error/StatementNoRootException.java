package core.error;

import core.models.Statement;

/**
 * Ngoại lệ khi câu lệnh chưa được gán biểu thức gốc cho nó
 *
 */
public class StatementNoRootException extends CoreException {
	private static final long serialVersionUID = 1L;

	private Statement mStatement;
	
	public StatementNoRootException(Statement stm) {
		super("Statement %s not have root yet!", stm);
		mStatement = stm;
	}

	/**
	 * Trả về câu lệnh không có biểu thức gốc ứng với ngoại lệ
	 */
	public Statement getStatement(){
		return mStatement;
	}
}
