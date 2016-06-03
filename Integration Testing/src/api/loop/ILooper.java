package api.loop;

import java.util.Iterator;

/**
 * Ngữ cảnh lặp một đường đi lặp, bao gồm 1 danh sách các số lần lặp tương ứng với từng
 * câu lệnh lặp trong đường đi
 */
public interface ILooper {

	Iterator<Integer> iter();
	
	int getMaxLoop();
	
	final int TEST_THIS = -1;
}
