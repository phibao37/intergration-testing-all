package core.unit;

import java.util.ArrayList;

import core.eval.Evaluateable;
import core.eval.SimpleEval;
import core.models.ArrayVariable;
import core.models.Expression;
import core.models.Variable;
import core.models.expression.ArrayIndexExpression;
import core.models.expression.NameExpression;
import core.models.expression.PlaceHolderExpression;
import core.visitor.ExpressionVisitor;

/**
 * Bảng lưu trữ danh sách các biến số và thực thi các tác vụ như:
 * <ul>
 * 	<li>cập nhật giá trị cho các biến số</li>
 * 	<li>thay thế các biểu thức cho trước bởi các biến số mà nó đang lưu giữ</li>
 * </ul>
 * @author ducvu
 *
 */
public class VariableTable extends ArrayList<Variable> {
	private static final long serialVersionUID = -1048482067823015287L;
	
	private int mScope = 0;
	
	/**
	 * Đang mở ra một khối { (scope) mới, 
	 * các biến số mới thêm sẽ được coi như ở trong khối này
	 */
	public void newOpenScope(){
		mScope++;
	}
	
	/**
	 * Đang có một khối } (scope) đóng lại.<br/>
	 * Các biến đang nằm ở trong scope hiện thời sẽ bị xóa, sau đó giá trị scope
	 * sẽ bị giảm đi 1
	 */
	public void newCloseScope(){
		for (int i = size()-1; i >= 0; i--)
			if (get(i).getScope() == mScope)
				remove(i);
		
		mScope--;
	}
	
	/**
	 * Thêm một biến số mới vào bảng biến
	 */
	@Override
	public boolean add(Variable e) {
		e.setScope(mScope);
		return super.add(e);
	}
	
	/**
	 * Cập nhật giá trị mới cho biến số
	 * @param name tên của biến số
	 * @param value giá trị mới cần cập nhật
	 * @throws NullPointerException - tên biến không tồn tại
	 */
	public void updateVariableValue(String name, Expression value){
		//System.out.printf("Gan %s = %s", name, value);
		value = fillExpression(value);
		//System.out.printf(" --> %s\n\n", value);
		find(name).setValue(value);
	}
	
	/**
	 * Cập nhật giá trị mới cho một phần tử của biến mảng
	 * @param name tên biến mảng
	 * @param indexs danh sách các biểu thức chỉ số xác định vị trí phần tử cần câp nhật
	 * @param value giá trị mới cho phần tử mảng
	 * @throws NullPointerException - tên biến không tồn tại
	 */
	public void updateArrayValue(String name, Expression[] indexs, Expression value){
		value = fillExpression(value);
		Expression[] newIndexs = new Expression[indexs.length];
		
		for (int i = 0; i < indexs.length; i++)
			newIndexs[i] = evalExpression(indexs[i]);
		
		((ArrayVariable)find(name)).setValueAt(value, newIndexs);
	}
	

	/**
	 * Thay thế các biến trong bảng có mặt trong biểu thức bằng giá trị của các biến đó 
	 * @param expression biểu thức cần thay thế
	 * @return biểu thức đã được thay thế
	 */
	public Expression fillExpression(Expression expression){
		PlaceHolderExpression copy = new PlaceHolderExpression(expression.clone());
		ArrayList<Expression> justReplaces = new ArrayList<>();
		
		expression.accept(new ExpressionVisitor() {

			@Override
			// Đang duyệt qua một tên biến, thay thế bằng giá trị của nó
			public int visit(NameExpression name) {
				Variable find = find(name.getName());

				if (find != null) {
					if (find.isValueSet()){
						Expression clone = find.getValue()
								.clone()
								.setBlockReplace(true);
						justReplaces.add(clone);
						copy.replace(name, clone);
					}
				} else {
					System.out.println("Not found: " + name);
				}
				return PROCESS_CONTINUE;
			}

			@Override
			// Đang duyệt qua một truy cập mảng (a[1]),....
			public int visit(ArrayIndexExpression array) {
				ArrayVariable find = (ArrayVariable) find(array.getName());

				if (find != null) {
					Expression[] indexes = array.getIndexes().clone();
					for (int i = 0; i < indexes.length; i++)
						indexes[i] = evalExpression(indexes[i]);
					if (find.isValueSet(indexes)){
						Expression clone = find.getValueAt(indexes)
								.clone()
								.setBlockReplace(true);
						justReplaces.add(clone);
						copy.replace(array, clone);
					}
				} else {
					System.out.println("Not found: " + array);
				}
				return PROCESS_CONTINUE;
			}

		});
		
		for (Expression just: justReplaces)
			just.setBlockReplace(false);
		
		expression = copy.getElement();
		return expression;
	}
	
	/**
	 * Tìm biến ứng với tên cho trước
	 * @param name tên biến cần tìm
	 * @return biến đang có hiệu lực nhất nếu có, hoặc null nếu không có biến nào
	 */
	public Variable find(String name){
		Variable find = null;
		
		for (Variable v: this)
			if (v.getName().equals(name)){
				if (find == null)
					find = v;
				
				//tìm biến có scope lớn nhất
				else if (v.getScope() >= find.getScope())
					find = v;
			}
		return find;
	}
	
	/**
	 * Lấy scope của biến đang có hiệu lực
	 * @param name tên biến cần lấy
	 * @return scope của biến đó, hoặc -1 nếu không tìm thấy
	 */
	public int getScope(String name){
		Variable find = find(name);
		
		if (find == null)
			return -1;
		else
			return find.getScope();
	}
	
	
	/**
	 * Thực hiện {@link #fillExpression(Expression)}, 
	 * sau đó tính toán để rút gọn biểu thức. Việc tính toán này chỉ cần thiết
	 * khi cần một giá trị cụ thể để truy cập vào các vị trí mảng
	 * @param expression biểu thức cần thay thế và tính toán
	 * @return biểu thức đã được tính toán
	 */
	public Expression evalExpression(Expression expression){
		return DEFAULT_EVAL.evalExpression(fillExpression(expression));
	}
	
	private static Evaluateable DEFAULT_EVAL = SimpleEval.DEFAULT;
	
	/**
	 * Thiết đặt bộ tính toán giá trị mặc định
	 * @param eval bộ tính toán giá trị biểu thức
	 */
	public static void setDefaultEval(Evaluateable eval){
		DEFAULT_EVAL = eval;
	}
}
