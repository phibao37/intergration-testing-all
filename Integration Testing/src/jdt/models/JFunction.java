package jdt.models;

import org.eclipse.jdt.core.dom.Statement;

import api.IProject;
import api.models.IType;
import api.models.IVariable;
import api.parser.IFunctionParser;
import core.models.Function;
import jdt.JFunctionParser;

public class JFunction extends Function<Statement>{

	public JFunction(String name, IVariable[] paras, IType returnType, Statement body, IProject project) {
		super(name, paras, returnType, body, project);
	}

	@Override
	public IFunctionParser getFunctionParser() {
		return new JFunctionParser();
	}

}
