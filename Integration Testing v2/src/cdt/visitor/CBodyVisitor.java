package cdt.visitor;

import java.util.ArrayList;

import javafx.util.Pair;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTBreakStatement;
import org.eclipse.cdt.core.dom.ast.IASTCaseStatement;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTContinueStatement;
import org.eclipse.cdt.core.dom.ast.IASTDefaultStatement;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNullStatement;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;

import core.ProcessInterface;
import core.models.Statement;
import core.models.statement.FlagStatement;
import core.models.statement.ScopeStatement;
import core.visitor.BodyFunctionVisitor;

/**
 * Bộ duyệt các câu lệnh trong thân hàm của ngôn ngữ C
 * @author ducvu
 *
 */
public class CBodyVisitor implements BodyFunctionVisitor {

	private Statement END;
	private ArrayList<Statement> stmList = new ArrayList<>();
	private boolean mSubCondition;
	private EpUtils mUtils;
	
	@Override
	public Statement[] parseBody(Object body, boolean subCondition, 
			ProcessInterface process) {
		mUtils = new EpUtils(process);
		Statement BEGIN = FlagStatement.newBeginFlag();
		END = FlagStatement.newEndFlag();
		mSubCondition = subCondition;
		
		visitBlock((IASTCompoundStatement) body, BEGIN, END, null, null);
		
		stmList.clear();
		linkStatement(BEGIN, stmList);
		return stmList.toArray(new Statement[stmList.size()]);
	}
	
	/**
	 * Duyệt qua một khối câu lệnh {} và tạo liên kết
	 * @see #visitStatement(IASTStatement, Statement, Statement, Statement, Statement)
	 */
	private void visitBlock(IASTCompoundStatement block, Statement begin, Statement end, 
			Statement _break, Statement _continue){
		IASTStatement[] childs = block.getStatements();
		
		//Nếu không có câu lệnh con này, nối điểm đầu với điểm kết thúc là xong
		if (childs.length == 0){
			begin.setBranch(end);
			return;
		}
		
		Statement scopeIn = ScopeStatement.newOpenScope();
		Statement scopeOut = ScopeStatement.newCloseScope(end);
		Statement[] points = new Statement[childs.length + 1];
		begin.setBranch(scopeIn);
		
		//Tạo các điểm nối trung gian
		points[0] = scopeIn;
		for (int i = 1; i < childs.length; i++)
			points[i] = new ForwardStatement();
		points[childs.length] = scopeOut;
		
		for (int i = 0; i < childs.length; i++)
			visitStatement(childs[i], points[i], points[i+1], _break, _continue);
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
	private void visitStatement(IASTStatement stm, Statement begin, Statement end, 
			Statement _break, Statement _continue){
		if (stm instanceof IASTIfStatement){
			IASTIfStatement stmIf = (IASTIfStatement) stm;
			IASTExpression astCond = stmIf.getConditionExpression();
			IASTStatement astThen = stmIf.getThenClause();
			IASTStatement astElse = stmIf.getElseClause();
			
			Statement afterTrue = new ForwardStatement();
			Statement afterFalse = new ForwardStatement();
			
			visitCondition(astCond, begin, afterTrue, afterFalse);
			
			//Duyệt nhánh đúng
			visitStatement(astThen, afterTrue, end, _break, _continue);
			
			//Duyệt nhánh sai
			visitStatement(astElse, afterFalse, end, _break, _continue);
		}
		
		else if (!notNull(stm)){
			begin.setBranch(end);
		}
		
		else if (stm instanceof IASTForStatement){
			IASTForStatement stmFor = (IASTForStatement) stm;
			IASTStatement astInit = stmFor.getInitializerStatement();
			IASTExpression astCond = stmFor.getConditionExpression();
			IASTExpression astIter = stmFor.getIterationExpression();
			IASTStatement astBody = stmFor.getBody();
			
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
			
			visitStatement(astInit, bfInit, bfCond, _break, _continue);
			
			if (notNull(astCond))
				visitCondition(astCond, bfCond, bfBody, _break);
			else
				bfCond.setBranch(bfBody);
			
			visitStatement(astBody, bfBody, _continue, _break, _continue);
			
			//Cả điều kiện và phần thân đều rỗng => lặp vô hạn
			if (bfCond.getTrue().getTrue() == _continue){
				throw new RuntimeException("This FOR statement is infinity."
						+ "No condition or body found: " + stmFor.getRawSignature());
			}
			
			if (notNull(astIter)){
				Statement stmIter = new CStatement(astIter);
				_continue.setBranch(stmIter);
				stmIter.setBranch(bfCond);
			} else
				_continue.setBranch(bfCond);
		}
		
		else if (stm instanceof IASTWhileStatement){
			IASTWhileStatement astWhile = (IASTWhileStatement) stm;
			IASTStatement astBody = astWhile.getBody();
			
			Statement beforeCond = new ForwardStatement();
			Statement afterCond = new ForwardStatement();
			
			begin.setBranch(beforeCond);
			_break = end;
			_continue = beforeCond;
			visitCondition(astWhile.getCondition(), beforeCond, afterCond, _break);
			
			if (notNull(astBody)){
				visitStatement(astBody, afterCond, _continue, _break, _continue);
			} else
				afterCond.setBranch(_continue);
		}
		
		else if (stm instanceof IASTDoStatement){
			IASTDoStatement astDo = (IASTDoStatement) stm;
			IASTStatement astBody = astDo.getBody();
			
			Statement beforeDo = new ForwardStatement();
			Statement beforeCond = new ForwardStatement();
			
			begin.setBranch(beforeDo);
			_break = end;
			_continue = beforeCond;
			
			if (notNull(astBody))
				visitStatement(astBody, beforeDo, beforeCond, _break, _continue);
			else
				beforeDo.setBranch(beforeCond);
			
			visitCondition(astDo.getCondition(), beforeCond, beforeDo, _break);
		}
		
		else if (stm instanceof IASTSwitchStatement){
			IASTSwitchStatement astSw = (IASTSwitchStatement) stm;
			Statement scopeIn = ScopeStatement.newOpenScope();
			Statement scopeOut = ScopeStatement.newCloseScope(end);
			begin.setBranch(scopeIn);
			visitSwitch(astSw.getControllerExpression(), 
					(IASTCompoundStatement) astSw.getBody(), scopeIn, scopeOut);
		}
		
		else if (stm instanceof IASTCompoundStatement){
			visitBlock((IASTCompoundStatement) stm, begin, end, _break, _continue);
		}
		
		else if (stm instanceof IASTBreakStatement){
			begin.setBranch(_break);
		}
		
		else if (stm instanceof IASTContinueStatement){
			begin.setBranch(_continue);
		}
		
		else if (stm instanceof IASTReturnStatement){
			Statement stmReturn = new CStatement(stm);
			begin.setBranch(stmReturn);
			stmReturn.setBranch(END);
		}
		
		else {
			Statement normal = new CStatement(stm);
			begin.setBranch(normal);
			normal.setBranch(end);
		}
	}
	
	/**
	 * Duyệt qua câu lệnh SWITCH
	 * @param cond biểu thức quyết định switch
	 * @param body phần thân switch
	 * @param begin câu lệnh cần được gán 2 nhánh tới câu lệnh đầu tiên trong khối
	 * @param end câu lệnh kết thúc, cũng là câu lệnh mà break đi tới
	 */
	private void visitSwitch(IASTExpression cond, IASTCompoundStatement body,
			Statement begin, Statement end){
		ArrayList<Pair<ArrayList<IASTCaseStatement>, Statement>> caseLink = 
				new ArrayList<>();
		ArrayList<IASTCaseStatement> cases = new ArrayList<>();

		IASTStatement[] childs = body.getStatements();
		Statement defaultPoint = null, before = new ForwardStatement(), after;
		int i = 0;
		
		while (i < childs.length){
			IASTStatement stm = childs[i];
			
			if (stm instanceof IASTCaseStatement)
				cases.add((IASTCaseStatement) stm);
			else if (stm instanceof IASTDefaultStatement){
				cases.clear();
				while (i+1 < childs.length && childs[i+1] instanceof IASTCaseStatement)
					i++;
				defaultPoint = before;
			} else {
				//Vừa mới ra khỏi một dãy case
				if (cases.size() > 0){
					caseLink.add(new Pair<>(cases, before));
					cases = new ArrayList<>();
				}
				
				after = new ForwardStatement();
				visitStatement(stm, before, after, end, null);
				before = after;
			}
			i++;
		}
		before.setBranch(end);
		
		//Nút trước khi bắt đầu default
		Statement beforeDefault = new ForwardStatement();
		
		beforeDefault.setBranch(defaultPoint == null ? end : defaultPoint);
		if (caseLink.size() == 0){
			begin.setBranch(beforeDefault);
			return;
		}
		
		Statement[] mid = new Statement[caseLink.size() + 1];
		mid[0] = begin;
		for (i = 1; i < mid.length - 1; i++)
			mid[i] = new ForwardStatement();
		mid[i] = beforeDefault;
		String control = cond.getRawSignature();
		
		for (i = 0; i < caseLink.size(); i++){
			Pair<ArrayList<IASTCaseStatement>, Statement> pair = caseLink.get(i);
			cases = pair.getKey();
			String join = "";
			for (IASTCaseStatement astCase: cases)
				join += String.format("||%s==%s", control,
								astCase.getExpression().getRawSignature());
			
			visitCondition(EpUtils.getExpression(join.substring(2)), 
					mid[i], pair.getValue(), mid[i+1]);
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
	private void visitCondition(IASTExpression cond, Statement begin, 
			Statement endTrue, Statement endFalse){
		cond = normalize(cond);
		if (!mSubCondition){
			visitNormalCondition(cond, begin, endTrue, endFalse);
			return;
		}
		
		if (cond instanceof IASTBinaryExpression){
			IASTBinaryExpression astBin = (IASTBinaryExpression) cond;
			IASTExpression op1 = astBin.getOperand1();
			int op = astBin.getOperator();
			IASTExpression op2 = astBin.getOperand2();
			
			if (op == IASTBinaryExpression.op_logicalAnd){
				Statement midTrue = new ForwardStatement();
				Statement midFalse = new ForwardStatement();
				
				//Nếu điều kiện đầu sai, coi như sai luôn
				midFalse.setBranch(endFalse);
				visitCondition(op1, begin, midTrue, midFalse);
				visitCondition(op2, midTrue, endTrue, endFalse);
			} 
			else if (op == IASTBinaryExpression.op_logicalOr){
				Statement midTrue = new ForwardStatement();
				Statement midFalse = new ForwardStatement();
				
				//Nếu điều kiện đầu đúng, coi như đúng luôn
				midTrue.setBranch(endTrue);
				visitCondition(op1, begin, midTrue, midFalse);
				visitCondition(op2, midFalse, endTrue, endFalse);
			} else
				visitNormalCondition(cond, begin, endTrue, endFalse);
		} 
		else if (cond instanceof IASTUnaryExpression){
			IASTUnaryExpression astUnary = (IASTUnaryExpression) cond;
			int op = astUnary.getOperator();
			IASTExpression opr = astUnary.getOperand();
			
			if (op == IASTUnaryExpression.op_not){
				//Đảo ngược lại 2 đầu ra
				visitCondition(opr, begin, endFalse, endTrue);
			} else
				visitNormalCondition(cond, begin, endTrue, endFalse);
		}
	}
	
	/**
	 * Điều kiện đơn giản nhất (hoặc điều kiện kép nếu chế độ subCondition không bật
	 * @see #visitCondition(IASTExpression, Statement, Statement, Statement)
	 */
	private void visitNormalCondition(IASTExpression cond, Statement begin, 
			Statement endTrue, Statement endFalse){
		Statement stmCond = new CStatement(normalize(cond));
		begin.setBranch(stmCond);
		stmCond.setBranch(endTrue, endFalse);
	}
	
	/**
	 * Tối thiểu các biểu thức ngoặc
	 */
	private static IASTExpression normalize(IASTExpression ex){
		while (ex instanceof IASTUnaryExpression){
			IASTUnaryExpression unary = (IASTUnaryExpression) ex;
			if (unary.getOperator() == IASTUnaryExpression.op_bracketedPrimary)
				ex = unary.getOperand();
			else
				break;
		}
		return ex;
	}
	
	/**
	 * Kiểm tra không phải kiểu null hoặc câu lệnh null (;)
	 */
	private static boolean notNull(IASTNode node){
		return node != null && !(node instanceof IASTNullStatement);
	}
	
	/**
	 * Bộ duyệt mặc định
	 */
	public static CBodyVisitor DEFAULT = new CBodyVisitor();
	
	/**
	 * Câu lệnh C
	 */
	class CStatement extends Statement{

		public CStatement(IASTNode node) {
			super(node.getRawSignature());
			setRoot(mUtils.parseNode(node));
			//try{getRoot().printTree("  ");} catch (Exception e) {}
		}
		
	}
}

