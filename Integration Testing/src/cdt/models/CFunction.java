package cdt.models;

import org.eclipse.cdt.core.dom.ast.IASTStatement;

import api.models.IType;
import api.models.IVariable;
import api.parser.BodyParser;
import cdt.CBodyParser;
import cdt.CProject;
import core.models.Function;

public class CFunction extends Function<IASTStatement> {
	
	private String declare;
	
	public CFunction(String name, IVariable[] paras, IType returnType,
			IASTStatement body, CProject project) {
		super(name, paras, returnType, body, project);
	}
	
	public CFunction(String name, IVariable[] paras, IType returnType, 
			CProject project) {
		this(name, paras, returnType, null, project);
	}
	
	public CFunction setDeclareStr(String str){
		declare = str;
		return this;
	}
	
	public String getDeclareStr(){
		return declare;
	}

	@Override
	public BodyParser getBodyParser() {
		return new CBodyParser();
	}
}
