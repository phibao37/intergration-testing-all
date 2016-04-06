package test.demo;

import java.io.File;
import api.IProject;
import api.models.IFunction;
import api.models.IFunctionTestResult;
import cdt.CProject;

public class MainDemo1 {

	public static void main(String[] args) throws InterruptedException {
		
		IProject project = new CProject(new File("D:/Documents/C/test.cpp"));
		
		IFunction test = project.getFunctions().get(0);
		IFunctionTestResult result = project.testFunction(test);
		
		System.out.println(result);
	}

}
