package api.parser;

import java.io.IOException;

import api.IProject;
import api.models.IFunction;

public interface IExporter {

	IProject getProject();
	
	void addFunction(IFunction tested);
	
	void export() throws IOException;
	
	void close();
}
