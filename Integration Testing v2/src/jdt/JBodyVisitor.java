package jdt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;

import core.models.Statement;
import core.models.statement.FlagStatement;
import core.models.statement.ScopeStatement;
import core.visitor.BodyFunctionVisitor;

/**
 * Duyệt thân hàm Java và lấy ra danh sách các câu lệnh đã được liên kết
 */
public class JBodyVisitor implements BodyFunctionVisitor {

	private Statement BEGIN, END;
	private ArrayList<Statement> stmList = new ArrayList<>();
	private boolean mSubCondition;
	
	@Override
	public Statement[] parseBody(Object body, boolean subCondition) {
		BEGIN = FlagStatement.newBeginFlag();
		END = FlagStatement.newEndFlag();
		mSubCondition = subCondition;
		
		visitBlock((Block) body, BEGIN, END, null, null);
		stmList.clear();

		linkStatement(BEGIN, stmList);
		return stmList.toArray(new Statement[stmList.size()]);
	}
	
	/**
	 * Duyệt qua một câu lệnh khối {}, chứa một tập các câu lệnh con trong đó
	 */
	@SuppressWarnings("unchecked")
	private void visitBlock(Block block, Statement begin, Statement end, 
			Statement _break, Statement _continue){
		List<org.eclipse.jdt.core.dom.Statement> childs = block.statements();
		int length = childs.size();
		
		//Nếu không có câu lệnh con này, nối điểm đầu với điểm kết thúc là xong
		if (length == 0){
			begin.setBranch(end);
			return;
		}
		
		Statement scopeIn = ScopeStatement.newOpenScope();
		Statement scopeOut = ScopeStatement.newCloseScope(end);
		Statement[] points = new Statement[length + 1];
		begin.setBranch(scopeIn);
		
		//Tạo các điểm nối trung gian
				points[0] = scopeIn;
				for (int i = 1; i < length; i++)
					points[i] = new ForwardStatement();
				points[length] = scopeOut;
				
				for (int i = 0; i < length; i++)
					visitStatement(childs.get(i), 
							points[i], points[i+1], _break, _continue);
	}
	
	/**
	 * Đi qua một câu lệnh tổng quát.<br/>
	 * @param stm nút câu lệnh trong cây AST
	 * @param begin sau khi đã duyệt xong, câu lệnh này cần được chỉ 2 nhánh tới
	 * câu lệnh đầu tiên ở bên trong khối
	 * @param end các câu lệnh cuối cùng trong khối phải được kết thức tại đây
	 * @param _break câu lệnh BREAK trong khối này (nếu có) cần được kết thúc tại đây
	 * @param _continue câu lệnh CONTINUE trong khối này (nếu có) 
	 * cần được kết thúc tại đây
	 * @example
	 * <pre>
	 * {
	 * 		int i = 0;
	 *  	if (i < 10)
	 *   		break;
	 *  
	 *  	if (i < 11)
	 *   		continue;
	 *  
	 *  	if (i == 12)
	 *  		i = 1;
	 *  	else
	 *  		i = 2;
	 * }
	 * </pre>
	 * Sau khi duyệt qua câu lệnh khối này ({}), các điều kiện sau phải được thỏa mản:
	 * <ul>
	 * 	<li>Câu lệnh [begin] phải trỏ tới câu lệnh [int i = 0;] (*)</li>
	 * 	<li>Các câu lệnh [i = 1] và [i = 2] phải trỏ tới câu lệnh [end]
	 * 	<li>Câu lệnh [i < 10] phải trở tới câu lệnh [break]
	 * 	<li>Câu lệnh [i < 11] phải trỏ tới câu lệnh [continue]
	 * </ul>
	 * <b>Chú ý</b>: "trở tới" ở trên có nghĩa là cả 2 nhánh true/false đều được đặt
	 * liên kết tới. Hơn nữa, trỏ tới có thể là gián tiếp, nghĩa là nó trở tới và
	 * đi qua vài câu lệnh chuyển tiếp ({@link ForwardStatement} - nếu sử dụng), sau đó
	 * mới tới nơi đich 
	 */
	@SuppressWarnings("unchecked")
	private void visitStatement(org.eclipse.jdt.core.dom.Statement stm, 
			Statement begin, Statement end, Statement _break, Statement _continue){
		
		if (stm instanceof IfStatement){
			IfStatement stmIf = (IfStatement) stm;
			
			Statement afterTrue = new ForwardStatement();
			Statement afterFalse = new ForwardStatement();
			
			visitCondition(stmIf.getExpression(), begin, afterTrue, afterFalse);
			
			//Duyệt nhánh đúng
			visitStatement(stmIf.getThenStatement(), afterTrue, end, _break, _continue);
			
			//Duyệt nhánh sai
			visitStatement(stmIf.getElseStatement(), afterFalse, end, _break, _continue);
		}
		
		else if (!notNull(stm)){
			begin.setBranch(end);
		}
		
		else if (stm instanceof ForStatement){
			ForStatement stmFor = (ForStatement) stm;
			
			List<Expression> astInit = stmFor.initializers();
			Expression astCond = stmFor.getExpression();
			List<Expression> astUpdate = stmFor.updaters();
			
			//Tạo scope ảo cho trường hợp có khai báo thêm biến chạy
			Statement scopeIn = ScopeStatement.newOpenScope();
			Statement scopeOut = ScopeStatement.newCloseScope(end);
			begin.setBranch(scopeIn);
			
			Statement bfInit = new ForwardStatement(), //trước khi init
					bfCond = new ForwardStatement(), //trước khi so sánh
					bfBody = new ForwardStatement(); //trước khi vào phần thân
			
			scopeIn.setBranch(bfInit);
			_continue = new ForwardStatement();//sau khi hết phần thân hoặc gọi continue
			_break = scopeOut;//nhánh false của so sánh, hoặc có gọi break
			
			if (astInit.size() > 0)
				visitList(astInit, bfInit, bfCond);
			else
				bfInit.setBranch(bfCond);
			
			if (notNull(astCond))
				visitCondition(astCond, bfCond, bfBody, _break);
			else
				bfCond.setBranch(bfBody);
			
			visitStatement(stmFor.getBody(), bfBody, _continue, _break, _continue);
			
			//Cả điều kiện và phần thân đều rỗng => lặp vô hạn
			if (bfCond.getTrue().getTrue() == _continue){
				throw new RuntimeException("This FOR statement is infinity."
						+ "No condition or body found: " + stmFor.toString());
			}
			
			if (astUpdate.size() > 0)
				visitList(astUpdate, _continue, bfCond);
			else
				_continue.setBranch(bfCond);
		}
		
		else if (stm instanceof Block){
			visitBlock((Block) stm, begin, end, _break, _continue);
		}
		
		else if (stm instanceof ReturnStatement){
			Statement stmReturn = new JStatement(stm);
			begin.setBranch(stmReturn);
			stmReturn.setBranch(END);
		}
		
		else {
			Statement normal = new JStatement(stm);
			begin.setBranch(normal);
			normal.setBranch(end);
		}
		
	}
	
	/**
	 * Duyệt qua một biểu thức điều kiện và tách các điều kiện con ra
	 * @param cond nút điều kiện
	 * @param begin begin câu lệnh cần được thiết đặt 2 nhánh tới câu lệnh đầu tiên
	 * trong điều kiện
	 * @param endTrue câu lệnh mà nhánh đúng sẽ chỉ tới
	 * @param endFalse câu lệnh mà nhánh sai sẽ chỉ tới
	 */
	private void visitCondition(Expression cond, Statement begin, 
			Statement endTrue, Statement endFalse){
		cond = normalize(cond);
		if (!mSubCondition){
			visitNormalCondition(cond, begin, endTrue, endFalse);
			return;
		}
		
		if (cond instanceof InfixExpression){
			InfixExpression astBin = (InfixExpression) cond;
			Expression op1 = astBin.getLeftOperand();
			Operator op = astBin.getOperator();
			Expression op2 = astBin.getRightOperand();
			
			if (op == InfixExpression.Operator.CONDITIONAL_AND){
				Statement midTrue = new ForwardStatement();
				Statement midFalse = new ForwardStatement();
				
				//Nếu điều kiện đầu sai, coi như sai luôn
				midFalse.setBranch(endFalse);
				visitCondition(op1, begin, midTrue, midFalse);
				visitCondition(op2, midTrue, endTrue, endFalse);
			} 
			else if (op == InfixExpression.Operator.CONDITIONAL_OR){
				Statement midTrue = new ForwardStatement();
				Statement midFalse = new ForwardStatement();
				
				//Nếu điều kiện đầu đúng, coi như đúng luôn
				midTrue.setBranch(endTrue);
				visitCondition(op1, begin, midTrue, midFalse);
				visitCondition(op2, midFalse, endTrue, endFalse);
			} else
				visitNormalCondition(cond, begin, endTrue, endFalse);
		} 
		else if (cond instanceof PrefixExpression){
			PrefixExpression astUnary = (PrefixExpression) cond;
			PrefixExpression.Operator op = astUnary.getOperator();
			Expression opr = astUnary.getOperand();
			
			if (op == PrefixExpression.Operator.NOT){
				//Đảo ngược lại 2 đầu ra
				visitCondition(opr, begin, endFalse, endTrue);
			} else
				visitNormalCondition(cond, begin, endTrue, endFalse);
		}
		else
			visitNormalCondition(cond, begin, endTrue, endFalse);
	}
	
	/**
	 * Điều kiện đơn giản nhất (hoặc điều kiện kép nếu chế độ subCondition không bật
	 */
	private void visitNormalCondition(Expression cond, Statement begin, 
			Statement endTrue, Statement endFalse){
		Statement stmCond = new JStatement(normalize(cond));
		begin.setBranch(stmCond);
		stmCond.setBranch(endTrue, endFalse);
	}
	
	/**
	 * Duyệt qua một tập các câu lệnh và nối lại với nhau, thường dùng cho vòng for 
	 * <pre>
	 * for (<u>i = 0, j = 1, k = 2</u>; j < i + k; <u>i++, j--, k=i+j</u>){
	 * ...
	 * }
	 * </pre>
	 */
	private void visitList(List<Expression> list, Statement begin, Statement end){
		for (Expression ex: list){
			Statement stm = new JStatement(ex);
			begin.setBranch(stm);
			begin = stm;
		}
		begin.setBranch(end);
	}
	
	/**
	 * Tối thiểu các biểu thức ngoặc ()
	 */
	private static Expression normalize(Expression ex){
		while (ex instanceof ParenthesizedExpression){
			ex = ((ParenthesizedExpression) ex).getExpression();
		}
		return ex;
	}
	
	/**
	 * Kiểm tra không phải kiểu null hoặc câu lệnh null (;)
	 */
	private static boolean notNull(ASTNode node){
		return node != null && !(node instanceof EmptyStatement);
	}
}

class JStatement extends Statement{

	public JStatement(ASTNode node) {
		super(node.toString().replaceAll("\n", ""));
	}
	
}










