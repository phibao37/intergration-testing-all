package core.models;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import core.GUI;
import core.Utils;
import core.error.StatementNoRootException;
import core.graph.Graphable;
import core.models.statement.FlagStatement;
import core.models.statement.ScopeStatement;
import core.solver.Solver.Result;
import core.unit.CFG;
import core.visitor.BodyFunctionVisitor;
import core.visitor.ExpressionVisitor;

/**
 * Mô tả một hàm trong chương trình. Một hàm được khai báo bao gồm tên hàm, danh sách
 * các tham số, nội dung thân hàm và giá trị trả về.<br/>
 * Mỗi một hàm số luôn được liên kết với một đồ thị dòng điều khiển ({@link CFG}) để
 * phục vụ việc kiểm thử. Đồ thị này được tạo ra dựa trên nội dung của phần thân hàm
 * và một bộ chuyển đổi nội dung thân hàm ({@link BodyFunctionVisitor})
 * @author ducvu
 *
 */
public class Function extends Element implements Graphable {
	
	private String mName;
	private Variable[] mParas;
	private Object mBody;
	private Type mType;
	
	private CFG mCFG_12;
	private CFG mCFG_3;
	
	private ArrayList<Function> mRefers;
	private File mFile;
	
	/**
	 * Tạo một hàm số mới qua tất cả các thông số của nó
	 * @param name tên của hàm
	 * @param paras danh sách các tham số cần truyền vào hàm
	 * @param body nội dung phần thân hàm
	 * @param returnType kiểu trả về của hàm
	 */
	public Function(String name, Variable[] paras, Object body, Type returnType){
		mName = name;
		mParas = paras;
		mBody = body;
		mType = returnType;
		setContent(String.format("%s %s(%s)%s", mType, mName, 
				Utils.merge(", ", paras), getBodyString(mBody)));
		mRefers = new ArrayList<Function>();
	}
	
	/**
	 * Áp dụng bộ chuyển đổi thân hàm để tạo ra đồ thị CFG
	 * @param bodyVisitor bộ chuyển đổi từ nội dung thân hàm để tạo ra cấu trúc
	 * các câu lệnh được liên kết với nhau
	 */
	public void parseCFG(BodyFunctionVisitor bodyVisitor){
		setCFG(new CFG(bodyVisitor.parseBody(mBody, false)), false);
		setCFG(new CFG(bodyVisitor.parseBody(mBody, true)), true);
	}
	
	/**
	 * Lấy nội dung chuỗi của phần thân hàm
	 */
	protected String getBodyString(Object body){
		return String.valueOf(body);
	}
	
	/**
	 * Thiết đặt đồ thị cho hàm số, chỉ dùng khi cần đặt trực tiếp.<br/>
	 * Nên sử dụng phương thức chuẩn {@link #parseCFG(BodyFunctionVisitor)} 
	 * @param cfg đồ thị dòng điều khiển của hàm số
	 * @param subCondition đồ thị này có phân tích các điều kiện con
	 */
	public void setCFG(CFG cfg, boolean subCondition){
		if (subCondition)
			mCFG_3 = cfg;
		else
			mCFG_12 = cfg;
	}
	
	/**
	 * Trả về tên của hàm số
	 */
	public String getName(){
		return mName;
	}
	
	/**
	 * Trả về danh sách các tham số của hàm
	 */
	public Variable[] getParameters(){
		return mParas;
	}
	
	/**
	 * Trả về kiểu của hàm số
	 */
	public Type getReturnType(){
		return mType;
	}
	
	/**
	 * Trả về đồ thị dòng điều khiển của hàm số
	 * @param subCondition lấy đố thị đã tách các điều kiện thành các điều kiện con
	 */
	public CFG getCFG(boolean subCondition){
		return subCondition ? mCFG_3 : mCFG_12;
	}
	
	/**
	 * Thêm một hàm nằm trong danh sách tham chiếu, tức những hàm được hàm này gọi
	 * bên trong phần thân của nó (có thể bao gồm chính nó)
	 * @param refer hàm được tham chiếu tới
	 */
	public void addRefer(Function refer){
		if (!mRefers.contains(refer))
			mRefers.add(refer);
	}
	
	/**
	 * Trả về danh sách các hàm được tham chiếu trong hàm này
	 * @see #addRefer(Function)
	 */
	public ArrayList<Function> getRefers(){
		return mRefers;
	}
	
	/**
	 * Duyệt lần lượt qua các câu lệnh (và các biểu thức gốc ở bên trong câu lệnh) 
	 * ở trong phần thân hàm
	 * @param visitor bộ duyệt biểu thức. 
	 * Sử dụng {@link ExpressionVisitor#visit(Statement)} để "bắt" được khi các
	 * câu lệnh được duyệt vào
	 * @throws NullPointerException chưa có đồ thị CFG
	 */
	public void accept(ExpressionVisitor visitor, boolean subCondition) 
			throws NullPointerException{
		int process;
		
		for (Statement stm: getCFG(subCondition).getStatements()){
			process = visitor.visit(stm);
			
			if (process == ExpressionVisitor.PROCESS_ABORT)
				break;
			else if (process == ExpressionVisitor.PROCESS_CONTINUE
					&& !(stm instanceof ScopeStatement)
					&& !(stm instanceof FlagStatement)){
				
				try {
					process = stm.getRoot().accept(visitor);
					if (process == ExpressionVisitor.PROCESS_ABORT)
						break;
				} catch (StatementNoRootException e) {
					//Câu lệnh chưa có biểu thức gốc, chuyển sang câu lệnh khác
				}
				
			}
		}
	}
	
	private TestcaseManager mTestcase;
	/**
	 * Trả về bộ quản lý các testcase úng với hàm số
	 */
	public TestcaseManager getTestcaseManager(){
		if (mTestcase == null){
			mTestcase = new TestcaseManager();
		}
		return mTestcase;
	}
	
	/**
	 * Thiết đặt tập tin chứa mã nguồn của hàm
	 * @param file tập tin mã nguồn
	 */
	public void setSourceFile(File file){
		mFile = file;
	}
	
	/**
	 * Trả về tập tin chứa mã nguồn của hàm
	 */
	public File getSourceFile(){
		return mFile;
	}
	
	/**
	 * Trả về dạng tóm tắt của hàm, bao gồm tên hàm và tên tập tin mã nguồn
	 */
	public String getNameAndFile(){
		return String.format("%s %s - %s", 
				getReturnType(), getName(), getSourceFile().getName());
	}
	
	@Override
	public String getNodeContent() {
		return getName();
	}

	@Override
	public String getHTMLContent() {
		StringBuilder b = new StringBuilder();
		int iMax = mParas.length - 1;
		
		if (iMax >= 0)
	    for (int i = 0; ; i++) {
	        b.append(mParas[i].getHTMLContent());
	        if (i == iMax)
	        	break;
	        b.append(", ");
	    }
		
		return String.format("%s %s(%s)", 
				mType == null ? "??" : mType.getHTMLContent(), 
				getName(), 
				b);
	}
	
	/**
	 * Kiểm tra hàm này phụ thuộc vào các hàm khác
	 */
	public boolean isDependence(){
		return !getRefers().isEmpty();
	}

	public class TestcaseManager extends ArrayList<Testcase>{
		private static final long serialVersionUID = 1L;
		
		private TestcaseManager() {}
		
		/**
		 * Kiểm tra xem kết quả tính toán được do giải hệ có khớp với testcase mong
		 * muốn hay không
		 */
		public TestResult test(Result result){
			Testcase test = null;
			boolean match = false;
			
			for (Testcase t: this)
				if (t.isMatchResult(result)){
					test = t;
					match = Objects.equals(
							t.getReturnOutput(), result.getReturnValue());
					break;
				}
			
			return new TestResult(getFunction(), test, result, match);
		}
		
		public Function getFunction(){
			return Function.this;
		}
		
		/**
		 * Trả về danh sách tên các biến testcase đầu vào
		 */
		public String getSummaryName(){
			String s = "(";
			
			if (mParas.length > 0){
				s += mParas[0].getName();
				for (int i = 1; i < mParas.length; i++)
					s += ", " + mParas[i].getName();
			}
			
			s += ")";
			return s;
		}

		@Override
		public boolean add(Testcase e) {
			return contains(e) ? false : super.add(e) | notifyTestcaseChanged();
		}
		
		/**
		 * @throws RuntimeException testcase bị trùng với testcase khác
		 */
		@Override
		public Testcase set(int index, Testcase element) throws RuntimeException {
			int i = indexOf(element);
			
			if (i >= 0 && i != index)
				throw new RuntimeException(element.getSummaryInput()
						+ " bị trùng với testcase số " + i);
			
			Testcase c = super.set(index, element);
			notifyTestcaseChanged();
			return c;
		}

		@Override
		public Testcase remove(int index) {
			Testcase t = super.remove(index);
			notifyTestcaseChanged();
			return t;
		}

		private boolean notifyTestcaseChanged(){
			GUI.instance.notifyFunctionTestcaseChanged(getFunction(), size());
			return true;
		}
		
	}
}
