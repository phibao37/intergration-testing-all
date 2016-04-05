package api.parser;

import java.io.File;

import api.IProject;

/**
 * Duyệt qua toàn bộ nội dung của một tập tin mã nguồn, sau đó lọc ra danh sách các 
 * đối tượng toàn cục được khai báo như: danh sách hàm, danh sách biến toàn cục, ...
 *
 */
public interface IProjectParser {

	/**
	 * Phân tích một tệp mã nguồn và lấy các cấu trúc bên trong nạp vào trong chương trình
	 * @param source tệp mã nguồn
	 * @param project nơi lưu giữ các cấu trúc của project
	 */
	void parseSource(File source, IProject project);
	
}
