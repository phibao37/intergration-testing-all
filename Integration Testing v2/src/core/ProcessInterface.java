package core;

import java.util.ArrayList;

import core.models.type.ObjectType;

/**
 * @author ducvu
 * Giao diện của một tiến trình ứng dụng, dùng để quản lý danh sách các cấu trúc đầu vào
 * trong chương trình
 */
public interface ProcessInterface {
	
	/**
	 * Trả về danh sách các cấu trúc (struct/class) được định nghĩa trong chương trình
	 */
	public ArrayList<ObjectType> getDeclaredTypes();
}
