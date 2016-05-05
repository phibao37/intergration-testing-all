package api.graph;

import java.io.File;

public interface IFileInfo {

	int getOffset();
	
	int getLength();
	
	File getFile();
}
