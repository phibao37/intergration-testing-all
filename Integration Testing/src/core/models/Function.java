package core.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import api.Value;
import api.models.ICFG;
import api.models.IFunction;
import api.models.IType;
import api.models.IVariable;

public class Function<T> extends Element implements IFunction {

	private String mName;
	private IVariable[] mParas;
	private IType mReturnType;
	private T mBody;
	
	private ICFG mCFG_12, mCFG_3;
	private List<IFunction> mRefers;
	private File mFile;
	
	public Function(String name, IVariable[] paras, IType returnType, T body){
		mName = name;
		mParas = paras;
		mReturnType = returnType;
		mRefers = new ArrayList<>();
		setBody(body);
	}
	
	@Override
	public String getName() {
		return mName;
	}

	@Override
	public IVariable[] getParameters() {
		return mParas;
	}

	@Override
	public IType getReturnType() {
		return mReturnType;
	}
	
	public void setBody(T body){
		mBody = body;
	}
	
	public T getBody(){
		return mBody;
	}

	@Override
	public void setCFG(int cover, ICFG cfg) {
		switch (cover){
		case Value.COVER_STATEMENT:
		case Value.COVER_BRANCH:
			mCFG_12 = cfg;
			break;
		case Value.COVER_SUBCONDITION:
			mCFG_3 = cfg;
			break;
		}
	}

	@Override
	public ICFG getCFG(int cover) {
		switch (cover){
		case Value.COVER_STATEMENT:
		case Value.COVER_BRANCH:
			return mCFG_12;
		case Value.COVER_SUBCONDITION:
			return mCFG_3;
		default:
			return null;
		}
	}

	@Override
	public void addRefer(IFunction refer) {
		mRefers.add(refer);
	}

	@Override
	public List<IFunction> getRefers() {
		return mRefers;
	}

	@Override
	public void setSourceFile(File file) {
		mFile = file;
	}

	@Override
	public File getSourceFile() {
		return mFile;
	}

}
