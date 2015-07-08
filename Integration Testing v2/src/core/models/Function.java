package core.models;

import java.io.File;

import core.Utils;
import core.unit.CFG;
import core.visitor.BodyFunctionVisitor;

/**
 * Mô tả một hàm trong chương trình. Một hàm được khai báo bao gồm tên hàm, danh sách
 * các tham số, nội dung thân hàm và giá trị trả về.<br/>
 * Mỗi một hàm số luôn được liên kết với một đồ thị dòng điều khiển ({@link CFG}) để
 * phục vụ việc kiểm thử. Đồ thị này được tạo ra dựa trên nội dung của phần thân hàm
 * và một bộ chuyển đổi nội dung thân hàm ({@link BodyFunctionVisitor})
 * @author ducvu
 *
 */
public class Function extends Element {
	
	private String mName;
	private Variable[] mParas;
	private Object mBody;
	private Type mType;
	
	private CFG mCFG_12;
	private CFG mCFG_3;
	
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
}
