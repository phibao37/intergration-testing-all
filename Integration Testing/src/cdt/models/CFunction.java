package cdt.models;

import org.eclipse.cdt.core.dom.ast.IASTStatement;

import api.models.IType;
import api.models.IVariable;
import core.models.Function;

public class CFunction extends Function<IASTStatement> {
	
	private String declare;
	
	public CFunction(String name, IVariable[] paras, IType returnType,
			IASTStatement body) {
		super(name, paras, returnType, body);
	}
	
	public CFunction(String name, IVariable[] paras, IType returnType) {
		this(name, paras, returnType, null);
	}
	
	public CFunction setDeclareStr(String str){
		declare = str;
		return this;
	}
	
	public String getDeclareStr(){
		return declare;
	}

}
