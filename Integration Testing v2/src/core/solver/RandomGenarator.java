package core.solver;

import java.util.Random;

import core.S;
import core.models.type.BasicType;

/**
 * Bộ sinh giá trị random
 */
public class RandomGenarator {
	
	private static Random r = new Random();
	
	/**
	 * Trả về giá trị ngẫu nhiên ứng với một kiểu cụ thể
	 * @param type kiểu cần lấy giá trị
	 * @throws IllegalArgumentException cặp [cận dưới, cận trên] sai
	 */
	public static Object forType(BasicType type) throws IllegalArgumentException {
		int min = S.RAND_MIN, max = S.RAND_MAX;
		
		switch (type.getSize()){
		
		case BasicType.BOOL_SIZE:
			return r.nextBoolean();
		
		case BasicType.CHAR_SIZE:
		case BasicType.INT_SIZE:
		case BasicType.LONG_SIZE:
			return min + r.nextInt(max - min + 1);
			
		case BasicType.FLOAT_SIZE:
		case BasicType.DOUBLE_SIZE:
			if (min == max)
				return min;
			int intPart = r.nextInt(max - min) + min;
			int decPart = r.nextInt(101);
			
			return (100*intPart + decPart) / 100.0;
		}
		
		return null;
	}
	
	public static void main(String[] args){
		
		for (int i = 0; i < 100; i++)
			System.out.println(forType(BasicType.FLOAT));
	}
	
}
