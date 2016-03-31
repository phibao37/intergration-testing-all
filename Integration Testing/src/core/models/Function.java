package core.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import core.Utils;
import api.IProject;
import api.models.ICFG;
import api.models.IFunction;
import api.models.IType;
import api.models.IVariable;

public abstract class Function<T> extends Element implements IFunction {

	private String mName;
	private IVariable[] mParas;
	private IType mReturnType;
	private T mBody;
	private ICFG mCFG_12, mCFG_3;
	private List<IFunction> mRefers;
	
	private File mFile;
	private IProject project;
	private int status;
	private boolean testing;
	
	public Function(String name, IVariable[] paras, IType returnType, T body, 
			IProject project){
		mName = name;
		mParas = paras;
		mReturnType = returnType;
		mRefers = new ArrayList<>();
		this.project = project;
		
		setContent(String.format("%s %s(%s)", mReturnType, mName, 
				Utils.merge(", ", paras)));
		setBody(body);
		setStatus(LOADED);
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
	public IProject getProject() {
		return project;
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public int getStatus() {
		return status;
	}
	
	

	@Override
	public void setTesting(boolean testing) {
		this.testing = testing;
	}

	@Override
	public boolean isTesting() {
		return testing;
	}

	@Override
	public ICFG getCFG(int cover) {
		switch (cover){
		case ICFG.COVER_STATEMENT:
		case ICFG.COVER_BRANCH:
			if (mCFG_12 == null){
				mCFG_12 = new CFG(getBodyParser().parseBody(
						getBody(), false, getProject()));
			}
			return mCFG_12;
		case ICFG.COVER_SUBCONDITION:
			if (mCFG_3 == null){
				mCFG_3 = new CFG(getBodyParser().parseBody(
						getBody(), true, getProject()));
			}
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
	public String getHTML() {
		String para = "";
		
		if (mParas.length > 0){
			para = mParas[0].getHTML();
			for (int i = 1; i < mParas.length; i++)
				para += ", " + mParas[i].getHTML();
		}
		
		return String.format("%s %s(%s)", mReturnType.getHTML(), mName, para);
	}

	@Override
	public File getSourceFile() {
		return mFile;
	}

}
