package api.models;

/**
 * Giao diện một đường thì hành cơ bản, là một dãy có thứ tự các câu lệnh đơn sẽ được
 * chạy bởi chương trình khi được thực thi
 */
public interface IBasisPath {
	
	/**
	 * Số câu lệnh trong đường thi hành
	 */
	public int size();
	
	public Iterable<IStatement> iter();
	
	public IStatement get(int index);
	
}
