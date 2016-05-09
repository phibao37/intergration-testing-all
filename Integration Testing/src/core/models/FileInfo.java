package core.models;

import java.io.File;

import api.graph.IFileInfo;

public class FileInfo implements IFileInfo {

	int offset, length;
	File file;
	
	public FileInfo(int offset, int length, File file) {
		this.offset = offset;
		this.length = length;
		this.file = file;
	}
	
	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public File getFile() {
		return file;
	}
	
}